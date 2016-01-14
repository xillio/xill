package nl.xillio.xill;

import com.google.inject.Injector;
import me.biesaart.utils.Log;
import nl.xillio.plugins.PluginLoader;
import nl.xillio.plugins.XillPlugin;
import nl.xillio.xill.api.Debugger;
import nl.xillio.xill.api.Issue;
import nl.xillio.xill.api.LanguageFactory;
import nl.xillio.xill.api.Xill;
import nl.xillio.xill.api.components.Robot;
import nl.xillio.xill.api.components.RobotID;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.api.errors.XillParsingException;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.common.util.WrappedException;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xtext.nodemodel.INode;
import org.eclipse.xtext.nodemodel.util.NodeModelUtils;
import org.eclipse.xtext.resource.XtextResourceSet;
import org.eclipse.xtext.util.CancelIndicator;
import org.eclipse.xtext.validation.CheckMode;
import org.eclipse.xtext.validation.IResourceValidator;
import org.eclipse.xtext.validation.Issue.IssueImpl;
import org.slf4j.Logger;
import xill.lang.XillStandaloneSetup;
import xill.lang.scoping.XillScopeProvider;
import xill.lang.validation.XillValidator;
import xill.lang.xill.ConstructCall;
import xill.lang.xill.IncludeStatement;
import xill.lang.xill.InstructionSet;
import xill.lang.xill.UseStatement;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * This class is responsible for processing a single Xill file
 */
public class XillProcessor implements nl.xillio.xill.api.XillProcessor {
    private static final Logger LOGGER = Log.get();
    /**
     * The supported file extension
     */
    private final XtextResourceSet resourceSet;

    private final IResourceValidator validator;

    private Robot robot;

    private final File robotFile;
    private final File projectFolder;
    private final PluginLoader<XillPlugin> pluginLoader;
    private final Debugger debugger;
    private final Map<Construct, String> argumentSignatures = new HashMap<>();

    /**
     * Create a new processor that can run a file.
     *
     * @param projectFolder the project folder
     * @param robotFile     the robot file
     * @param pluginLoader  the plugin loader
     * @param debugger      the debugger
     * @throws IOException
     */
    public XillProcessor(final File projectFolder, final File robotFile, final PluginLoader<XillPlugin> pluginLoader,
                         final Debugger debugger) throws IOException {
        this.projectFolder = projectFolder;
        this.robotFile = robotFile;
        this.pluginLoader = pluginLoader;
        this.debugger = debugger;
        Injector injector = new XillStandaloneSetup().createInjectorAndDoEMFRegistration();
        injector.injectMembers(this);


        // obtain a resource set
        resourceSet = injector.getInstance(XtextResourceSet.class);
        validator = injector.getInstance(IResourceValidator.class);
    }

    @Override
    public List<Issue> compile() throws IOException, XillParsingException {
        return compileAsSubRobot(null);
    }

    @Override
    public List<Issue> validate() {
        synchronized (XillValidator.LOCK) {
            XillValidator.setProjectFolder(projectFolder);
            XillScopeProvider.setProjectFolder(projectFolder);
            debugger.reset();
            Resource resource = resourceSet.getResource(URI.createFileURI(robotFile.getAbsolutePath()), true);
            return validate(resource);
        }
    }

    /**
     * Compile as a sub robot.
     *
     * @param rootRobot The Root robot
     * @return a list of issues
     * @throws XillParsingException
     */
    public List<Issue> compileAsSubRobot(final RobotID rootRobot) throws XillParsingException {
        synchronized (XillValidator.LOCK) {
            XillValidator.setProjectFolder(projectFolder);
            XillScopeProvider.setProjectFolder(projectFolder);
            debugger.reset();
            return compile(robotFile, rootRobot);
        }
    }

    private List<Issue> compile(final File robotPath, RobotID rootRobot) throws XillParsingException {
        Resource resource = resourceSet.getResource(URI.createFileURI(robotPath.getAbsolutePath()), true);

        RobotID robotID = RobotID.getInstance(robotPath, projectFolder);
        if (rootRobot == null) {
            rootRobot = robotID;
        }

        LanguageFactory<xill.lang.xill.Robot> factory = new XillProgramFactory(pluginLoader, getDebugger(), rootRobot);


        List<Issue> issues = validate(resource);

        // Throw an exception when an error was found
        Optional<Issue> error = issues.stream().filter(issue -> issue.getSeverity() == Issue.Type.ERROR).findFirst();

        if (error.isPresent()) {
            throw new XillParsingException(error.get().getMessage(), error.get().getLine(), error.get().getRobot());
        }

        xill.lang.xill.Robot mainRobotToken = null;

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

    private List<Issue> validate(Resource resource) {
        try {
            gatherResources(resource);
        } catch (XillParsingException e) {
            LOGGER.error("Could not find a robot", e);
            File errorFile = new File(resource.getURI().toFileString());
            RobotID robotID = RobotID.getInstance(errorFile, projectFolder);

            Issue issue = new Issue(e.getMessage(), e.getLine(), Issue.Type.ERROR, robotID);
            return Collections.singletonList(issue);
        }

        List<Issue> issues = new ArrayList<>();
        // Validate all resources
        for (Resource currentResource : resourceSet.getResources()) {
            // Build RobotID
            File currentFile = new File(currentResource.getURI().toFileString());

            issues.addAll(validate(currentResource, RobotID.getInstance(currentFile, projectFolder)));
        }
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

    private List<Issue> validate(final Resource resource, final RobotID robotID) {
        try {
            return doValidate(resource, robotID);
        } catch (WrappedException e) {
            // In rare cases xText throws an exception. That means we have no information except the robot with which it happened.
            LOGGER.error("Exception during validation.", e);
            return Collections.singletonList(new Issue("An unexpected exception occurred during compilation." +
                    "\nThis can be caused by two reserved keywords incorrectly following each other.", 1, Issue.Type.ERROR, robotID));
        }
    }

    private List<Issue> doValidate(final Resource resource, final RobotID robotID) {
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

                    return new Issue(impl.getMessage(), impl.getLineNumber(), type, robotID);
                }).collect(Collectors.toList());

        // Add warnings to issues
        resource.getWarnings().forEach(warning -> issues.add(new Issue(warning.getMessage(), warning.getLine(), Issue.Type.WARNING, robotID)));

        // Check for the existence of the plugins
        for (EObject object : resource.getContents()) {
            xill.lang.xill.Robot robot = (xill.lang.xill.Robot) object;

            for (UseStatement useStatement : robot.getUses()) {
                String name = getName(useStatement);

                boolean found = pluginLoader.getPluginManager()
                        .getPlugins()
                        .stream()
                        .anyMatch(plugin -> plugin.getName().equals(name));

                if (!found) {
                    INode node = NodeModelUtils.getNode(object);

                    issues.add(new Issue("No plugin with name " + name + " was found.", node.getStartLine(), Issue.Type.ERROR, robotID));
                }
            }
        }

        // Check for the existence of the constructs
        if (issues.isEmpty()) {
            for (EObject object : resource.getContents()) {
                xill.lang.xill.Robot robot = (xill.lang.xill.Robot) object;
                Issue issue = checkConstructs(robot.getInstructionSet(), robotID);
                if (issue != null) {
                    issues.add(issue);
                }
            }
        }
        return issues;
    }

    private Issue checkConstructs(InstructionSet instructionSet, RobotID robotID) {
        TreeIterator<EObject> iterator = instructionSet.eAllContents();

        while (iterator.hasNext()) {
            EObject object = iterator.next();

            if (object instanceof ConstructCall) {
                ConstructCall call = (ConstructCall) object;
                String plugin = getName(call.getPackage());
                XillPlugin xillPlugin = pluginLoader.getPluginManager()
                        .getPlugins()
                        .stream()
                        .filter(p -> p.getName().equals(plugin))
                        .findAny()
                        .orElse(null);

                if (xillPlugin.getConstruct(call.getFunction()) == null) {
                    INode node = NodeModelUtils.getNode(object);
                    return new Issue("No construct with name " + call.getFunction() + " was found in package " + plugin, node.getStartLine(), Issue.Type.ERROR, robotID);
                }
            }
        }
        return null;
    }

    private String getName(UseStatement statement) {
        return statement.getPlugin() == null ? statement.getName() : statement.getPlugin();
    }

    /**
     * @return The last compiled robot or null if compilation hasn't taken place
     * yet.
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

    @Override
    public Map<String, List<String>> getCompletions(String currentLine, String prefix, int column, int row) {
        List<XillPlugin> plugins = pluginLoader.getPluginManager().getPlugins();

        Map<String, List<String>> result = new HashMap<>();
        getConstructCompletions(result, currentLine, column, prefix);

        if (result.isEmpty()) {
            result.put("keyword", Arrays.asList(XillValidator.RESERVED_KEYWORDS));
            result.put("package", plugins.stream().map(XillPlugin::getName)
                    .filter(name -> name.toLowerCase().startsWith(prefix.toLowerCase()))
                    .collect(Collectors.toList()));
        }


        return result;
    }

    /**
     * This method will fill the result map with construct advice based on the test in front of the cursor.
     *
     * @param result      the result map to fill
     * @param currentLine the current line
     * @param column      the cursor position
     * @param prefix      the piece of text in front of the cursor
     */
    private void getConstructCompletions(Map<String, List<String>> result, String currentLine, int column, String prefix) {
        if (column <= 0) {
            return;
        }

        int lastPeriod = currentLine.lastIndexOf(".");

        if (lastPeriod >= 0 && lastPeriod == column - prefix.length() - 1) {
            String tillColumn = currentLine.substring(0, lastPeriod);

            // Test all plugins
            for (XillPlugin xillPlugin : pluginLoader.getPluginManager().getPlugins()) {

                // Test if the plugin name is a match
                if (tillColumn.endsWith(xillPlugin.getName())) {
                    List<String> constructs = new ArrayList<>();

                    // Test all constructs
                    for (Construct construct : xillPlugin.getConstructs()) {

                        // Test if constructs are a match
                        if (prefix.isEmpty() || construct.getName().startsWith(prefix)) {
                            constructs.add(getSignature(construct));
                        }
                    }

                    // If there are results put them in the map
                    if (!constructs.isEmpty()) {
                        result.put(xillPlugin.getName(), constructs);
                    }
                }
            }
        }

    }

    /**
     * This method will build a signature that matches the construct.
     *
     * @param construct the construct
     * @return the signature
     */
    private String getSignature(Construct construct) {
        if (!argumentSignatures.containsKey(construct)) {
            ConstructContext context = new ConstructContext(getRobotID(), getRobotID(), construct, null, null, null, null);
            try (ConstructProcessor processor = construct.prepareProcess(context)) {

                List<String> args = new ArrayList<>();
                for (int i = 0; i < processor.getNumberOfArguments(); i++) {
                    String arg = processor.getArgumentName(i);
                    args.add("${" + (i + 1) + ":" + arg + "}");
                }

                String signature = construct.getName() + "(" + StringUtils.join(args, ", ") + ")";
                argumentSignatures.put(construct, signature);
            }
        }

        return argumentSignatures.get(construct);
    }
}
