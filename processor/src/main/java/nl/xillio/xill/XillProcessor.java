package nl.xillio.xill;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import nl.xillio.plugins.PluginLoader;
import nl.xillio.xill.api.Debugger;
import nl.xillio.xill.api.Issue;
import nl.xillio.xill.api.LanguageFactory;
import nl.xillio.xill.api.PluginPackage;
import nl.xillio.xill.api.Xill;
import nl.xillio.xill.api.components.Robot;
import nl.xillio.xill.api.components.RobotID;
import nl.xillio.xill.api.errors.XillParsingException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.emf.common.util.DiagnosticException;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.Resource.Diagnostic;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.xtext.nodemodel.INode;
import org.eclipse.xtext.nodemodel.util.NodeModelUtils;
import org.eclipse.xtext.resource.XtextResourceSet;
import org.eclipse.xtext.util.CancelIndicator;
import org.eclipse.xtext.validation.CheckMode;
import org.eclipse.xtext.validation.IResourceValidator;
import org.eclipse.xtext.validation.Issue.IssueImpl;

import xill.lang.XillStandaloneSetup;
import xill.lang.xill.IncludeStatement;

import com.google.inject.Inject;
import com.google.inject.Injector;

/**
 * This class is responsible for processing a single Xill file
 */
public class XillProcessor implements nl.xillio.xill.api.XillProcessor {
	/**
	 * The supported file extension
	 */
	private static final Injector injector;
	private final XtextResourceSet resourceSet;

	@Inject
	private IResourceValidator validator;

	private Robot robot;

	private final File robotFile;
	private final File projectFolder;
	private final PluginLoader<PluginPackage> pluginLoader;
	private final Debugger debugger;

	// Set up the envirionment
	static {
		// creating the injector
		injector = new XillStandaloneSetup().createInjectorAndDoEMFRegistration();
	}

	/**
	 * Create a new processor that can run a file
	 *
	 * @param projectFolder
	 * @param robotFile
	 * @param pluginLoader
	 * @param debugger
	 * @throws IOException
	 */
	public XillProcessor(final File projectFolder, final File robotFile, final PluginLoader<PluginPackage> pluginLoader, final Debugger debugger) throws IOException {
		this.projectFolder = projectFolder;
		this.robotFile = robotFile;
		this.pluginLoader = pluginLoader;
		this.debugger = debugger;
		injector.injectMembers(this);

		// obtain a resource set
		resourceSet = injector.getInstance(XtextResourceSet.class);

		// Load all resources
		Collection<File> scripts = FileUtils.listFiles(projectFolder, new String[] {Xill.FILE_EXTENSION}, true);
		scripts.forEach(script -> resourceSet.createResource(URI.createFileURI(script.getAbsolutePath())));
	}

	@Override
	public List<Issue> compile() throws IOException, XillParsingException {
		debugger.reset();
		return compile(robotFile);
	}

	private List<Issue> compile(final File robotPath) throws XillParsingException {
		List<Issue> issues = new ArrayList<>();
		Resource resource = resourceSet.getResource(URI.createFileURI(robotPath.getAbsolutePath()), true);

		return compile(resource, new XillProgramFactory(pluginLoader, getDebugger(), RobotID.getInstance(robotPath, projectFolder)), issues);

	}

	private List<Issue> compile(final Resource resource, final LanguageFactory<xill.lang.xill.Robot> factory, final List<Issue> issues) throws XillParsingException {

		File currentFile = new File(resource.getURI().toFileString());
		RobotID robotID = RobotID.getInstance(currentFile, projectFolder);
		// Throw errors
		if (!resource.getErrors().isEmpty()) {
			Diagnostic error = resource.getErrors().get(0);
			throw new XillParsingException(error.getMessage(), error.getLine(), robotID);
		}

		// Perform validation
		issues.addAll(validate(resource, robotID));

		// Compile includes
		List<Robot> libraries = new ArrayList<>();
		for (EObject robot : resource.getContents()) {
			for (IncludeStatement include : ((xill.lang.xill.Robot) robot).getIncludes()) {
				String subPath = StringUtils.join(include.getName(), File.separator) + "." + Xill.FILE_EXTENSION;
				File libPath = new File(projectFolder, subPath);
				String fullPath = libPath.getAbsolutePath();

				Resource dependency = null;
				try {
					dependency = resourceSet.getResource(URI.createFileURI(fullPath), true);
				}catch(Exception e) { 
					//TODO Find out a way to catch the org.eclipse.emf.ecore.resource.impl.ResourceSetImpl$1DiagnosticWrappedException
				}
				
				if (dependency == null) {
					INode node = NodeModelUtils.getNode(robot);
					throw new XillParsingException("Required library at " + fullPath + " could not be found", node.getStartLine(), robotID);
				}
				
				issues.addAll(compile(dependency, factory, issues));
				libraries.add(this.robot);
			}
		}

		// Create the language tree
		robot = factory.parse((xill.lang.xill.Robot) resource.getContents().get(0), RobotID.getInstance(currentFile, projectFolder), libraries);

		// all issues (No errors in here)
		return issues;
	}

	private List<Issue> validate(final Resource resource, final RobotID robotID) throws XillParsingException {
		// Validate
		List<Issue> issues = validator.validate(resource, CheckMode.ALL, CancelIndicator.NullImpl).stream().map(
			issue -> {
				IssueImpl impl = (IssueImpl) issue;

				Issue.Type type = null;

				switch (impl.getSeverity()) {
					case ERROR:
						type = Issue.Type.ERROR;
						break;
					case IGNORE:
						type = Issue.Type.INFO;
						break;
					case INFO:
						type = Issue.Type.INFO;
						break;
					case WARNING:
						type = Issue.Type.WARNING;
						break;
				}

				return new Issue(impl.getMessage(), impl.getLineNumber(), type);
			}
			).collect(Collectors.toList());

		// Throw an exception when an error was found
		Optional<Issue> error = issues.stream().filter(issue -> issue.getSeverity() == Issue.Type.ERROR).findFirst();

		if (error.isPresent()) {
			throw new XillParsingException(error.get().getMessage(), error.get().getLine(), robotID);
		}

		// Add warnings to issues
		resource.getWarnings().forEach(warning -> {
			issues.add(new Issue(warning.getMessage(), warning.getLine(), Issue.Type.WARNING));
		});

		return issues;
	}

	/**
	 * @return The last compiled robot or null if compilation hasn't taken place yet.
	 */
	@Override
	public Robot getRobot() {
		return robot;
	}

	/**
	 * @return the debugger
	 */
	@Override
	public Debugger getDebugger() {
		return debugger;
	}

	@Override
	public RobotID getRobotID() {
		return RobotID.getInstance(robotFile, projectFolder);
	}

	@Override
	public Collection<String> listPackages() {
		return pluginLoader.getPluginManager().getPlugins().stream().map(PluginPackage::getName).collect(Collectors.toList());
	}
}
