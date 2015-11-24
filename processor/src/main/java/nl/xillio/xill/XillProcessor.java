package nl.xillio.xill;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.Resource.Diagnostic;
import org.eclipse.xtext.nodemodel.INode;
import org.eclipse.xtext.nodemodel.util.NodeModelUtils;
import org.eclipse.xtext.resource.XtextResourceSet;
import org.eclipse.xtext.util.CancelIndicator;
import org.eclipse.xtext.validation.CheckMode;
import org.eclipse.xtext.validation.IResourceValidator;
import org.eclipse.xtext.validation.Issue.IssueImpl;

import com.google.inject.Inject;
import com.google.inject.Injector;

import nl.xillio.plugins.PluginLoader;
import nl.xillio.plugins.XillPlugin;
import nl.xillio.xill.api.Debugger;
import nl.xillio.xill.api.Issue;
import nl.xillio.xill.api.LanguageFactory;
import nl.xillio.xill.api.Xill;
import nl.xillio.xill.api.components.Robot;
import nl.xillio.xill.api.components.RobotID;
import nl.xillio.xill.api.errors.XillParsingException;
import xill.lang.XillStandaloneSetup;
import xill.lang.scoping.XillScopeProvider;
import xill.lang.validation.XillValidator;
import xill.lang.xill.IncludeStatement;

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
	private final PluginLoader<XillPlugin> pluginLoader;
	private final Debugger debugger;

	private final List<Resource> compiledResources = new ArrayList<>();

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
	public XillProcessor(final File projectFolder, final File robotFile, final PluginLoader<XillPlugin> pluginLoader,
					final Debugger debugger) throws IOException {
		this.projectFolder = projectFolder;
		this.robotFile = robotFile;
		this.pluginLoader = pluginLoader;
		this.debugger = debugger;
		injector.injectMembers(this);

		// obtain a resource set
		resourceSet = injector.getInstance(XtextResourceSet.class);
	}

	@Override
	public List<Issue> compile() throws IOException, XillParsingException {
		return compileAsSubrobot(null);
	}

	/**
	 * Compile as a subrobot
	 *
	 * @param rootRobot
	 *        The Root robot
	 * @return a list of issues
	 * @throws XillParsingException
	 */
	public List<Issue> compileAsSubrobot(final RobotID rootRobot) throws XillParsingException {
		compiledResources.clear();
		debugger.reset();
		return compile(robotFile, rootRobot);
	}

	private List<Issue> compile(final File robotPath, RobotID rootRobot) throws XillParsingException {
		XillScopeProvider.PROJECTFOLDER = projectFolder;
		Resource resource = resourceSet.getResource(URI.createFileURI(robotPath.getAbsolutePath()), true);

		gatherResources(resource);

		List<Issue> issues = new ArrayList<>();
		RobotID robotID = RobotID.getInstance(robotPath, projectFolder);
		if (rootRobot == null) {
			rootRobot = robotID;
		}

		LanguageFactory<xill.lang.xill.Robot> factory = new XillProgramFactory(pluginLoader, getDebugger(), rootRobot);

		xill.lang.xill.Robot mainRobotToken = null;

		// Validate all resources
		for (Resource currentResource : resourceSet.getResources()) {
			// Build RobotID
			File currentFile = new File(currentResource.getURI().toFileString());

			issues.addAll(validate(currentResource, RobotID.getInstance(currentFile, projectFolder)));
		}

		// Parse all resources
		for (Resource currentResource : resourceSet.getResources()) {
			for (EObject rootToken : currentResource.getContents()) {
				// Build RobotID
				File currentFile = new File(currentResource.getURI().toFileString());

				// Parse
				factory.parse((xill.lang.xill.Robot) rootToken, RobotID.getInstance(currentFile, projectFolder));

				// Check if is main robot token
				if (rootToken.eResource() == resource) {
					mainRobotToken = (xill.lang.xill.Robot) rootToken;
				}
			}
		}

		factory.compile();

		robot = factory.getRobot(mainRobotToken);
		return issues;

	}

	private void gatherResources(final Resource resource) throws XillParsingException {
		for (EObject root : resource.getContents()) {
			xill.lang.xill.Robot rootRobot = (xill.lang.xill.Robot) root;

			for (IncludeStatement include : rootRobot.getIncludes()) {
				URI uri = getURI(include);
				if (!resourceSet.getURIResourceMap().containsKey(uri)) {
					// This is not in there yet
					if (!new File(uri.toFileString()).exists()) {
						INode node = NodeModelUtils.getNode(include);
						throw new XillParsingException("The library " + uri.toFileString() + " does not exist.", node.getStartLine(), RobotID.getInstance(new File(resource.getURI().toFileString()), projectFolder));
					}
					Resource library = resourceSet.getResource(uri, true);
					gatherResources(library);
				}
			}
		}
	}

	private URI getURI(final IncludeStatement include) {
		String subPath = StringUtils.join(include.getName(), File.separator) + "." + Xill.FILE_EXTENSION;
		File libPath = new File(projectFolder, subPath);
		String fullPath = libPath.getAbsolutePath();

		return URI.createFileURI(fullPath);
	}

	private List<Issue> validate(final Resource resource, final RobotID robotID) throws XillParsingException {
		// Throw errors
		if (!resource.getErrors().isEmpty()) {
			Diagnostic error = resource.getErrors().get(0);
			throw new XillParsingException(error.getMessage(), error.getLine(), robotID);
		}

		// Validate
		List<Issue> issues = validator.validate(resource, CheckMode.ALL, CancelIndicator.NullImpl).stream()
			.map(issue -> {
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
			}).collect(Collectors.toList());

		// Throw an exception when an error was found
		Optional<Issue> error = issues.stream().filter(issue -> issue.getSeverity() == Issue.Type.ERROR).findFirst();

		if (error.isPresent()) {
			throw new XillParsingException(error.get().getMessage(), error.get().getLine(), robotID);
		}

		// Add warnings to issues
		resource.getWarnings().forEach(warning -> issues.add(new Issue(warning.getMessage(), warning.getLine(), Issue.Type.WARNING)));

		return issues;
	}

	/**
	 * @return The last compiled robot or null if compilation hasn't taken place
	 *         yet.
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
		return pluginLoader.getPluginManager().getPlugins().stream().map(XillPlugin::getName)
			.collect(Collectors.toList());
	}

	@Override
	public String[] getReservedKeywords() {
		return XillValidator.RESERVED_KEYWORDS;
	}
}
