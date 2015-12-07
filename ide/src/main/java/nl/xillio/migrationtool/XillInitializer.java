package nl.xillio.migrationtool;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import nl.xillio.events.Event;
import nl.xillio.events.EventHost;
import nl.xillio.plugins.CircularReferenceException;
import nl.xillio.plugins.PluginLoader;
import nl.xillio.plugins.XillPlugin;
import nl.xillio.xill.api.components.RobotID;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.docgen.*;
import nl.xillio.xill.docgen.data.Parameter;
import nl.xillio.xill.docgen.exceptions.ParsingException;
import nl.xillio.xill.docgen.impl.ConstructDocumentationEntity;
import nl.xillio.xill.services.inject.DefaultInjectorModule;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * This {@link Thread} is responsible for loading the plugins an and initializing the language.
 *
 * @author Thomas Biesaart
 */
public class XillInitializer extends Thread {
    private final DocGen docGen;
    private DocumentationParser parser;
    private static final Logger LOGGER = LogManager.getLogger(XillInitializer.class);
    private static final File PLUGIN_FOLDER = new File("plugins");
    private PluginLoader<XillPlugin> pluginLoader;
    private final EventHost<InitializationResult> onLoadComplete = new EventHost<>();
    private final String jarFile;
    private DocumentationSearcher searcher;
    private boolean pluginsLoaded = false;

    /**
     * Create a new XillInitializer.
     *
     * @param docGen the DocGen set to use
     */
    public XillInitializer(DocGen docGen) {
        this.docGen = docGen;
        jarFile = getClass().getResource("/editor.html").toExternalForm().replaceAll("editor\\.html", "");
    }

    @Override
    public void run() {
        parser = docGen.getParser();

        LOGGER.info("Cleaning up old files...");
        cleanUpTemporaryFiles();

        LOGGER.info("Loading Xill language plugins...");

        LOGGER.debug("Initializing loader...");
        initializeLoader();

        LOGGER.debug("Loading plugins...");
        loadPlugins();

        LOGGER.debug("Initializing injector...");
        Injector injector = initializeInjector();

        LOGGER.debug("Initializing plugins...");
        initializePlugins(injector);

        LOGGER.debug("Generating documentation...");
        generateDocumentation();

        LOGGER.debug("Generating documentation index...");
        generateIndex();

        LOGGER.info("Done loading plugins...");

        try {
            URL docUrl = new File(docGen.getConfig().getDocumentationFolder(), "index.html").toURI().toURL();
            onLoadComplete.invoke(new InitializationResult(docUrl, getSearcher()));
        } catch (MalformedURLException e) {
            LOGGER.error("Failed to call onLoadComplete event!", e);
        }
    }

    private void cleanUpTemporaryFiles() {
        for (File file : FileUtils.getTempDirectory().listFiles()) {
            if (file.getName().startsWith("+JXF") || file.getName().startsWith("sqlite") || file.getName().startsWith("xill_editor")) {
                file.delete();
            }
        }
    }

    private void generateIndex() {
        try {
            docGen.generateIndex();
        } catch (ParsingException e) {
            LOGGER.error("Failed to generate index", e);
        }
    }

    DocumentationSearcher getSearcher() {
        if (this.searcher == null) {
            this.searcher = docGen.getSearcher();
        }

        return this.searcher;
    }

    private Injector initializeInjector() {
        Module module = new DefaultInjectorModule();
        List<Module> modules = new ArrayList<>(getPlugins());
        modules.add(module);
        return Guice.createInjector(modules.toArray(new Module[modules.size()]));
    }

    private void initializePlugins(Injector injector) {
        for (XillPlugin plugin : getPlugins()) {
            try {
                injector.injectMembers(plugin);
                plugin.initialize();
            } catch (Exception e) {
                LOGGER.error("Exception while initializing " + plugin, e);
            }
        }
    }

    private void generateDocumentation() {
        getPlugins().forEach(this::generateDocumentation);
    }

    private void generateDocumentation(XillPlugin plugin) {
        try (DocumentationGenerator generator = generator(plugin.getName())) {
            plugin.getConstructs().forEach(construct -> generateDocumentation(construct, generator));
        } catch (Exception e) {
            throw new XillioRuntimeException("Failed to generate documentation for " + plugin.getClass().getName(), e);
        }
    }

    DocumentationGenerator generator(String name) {
        DocumentationGenerator generator = docGen.getGenerator(name);
        generator.setProperty("jarFile", jarFile);

        return generator;
    }

    private void generateDocumentation(Construct construct, DocumentationGenerator generator) {
        // When the construct explicitly requests no documentation it will be skipped
        if (construct.hideDocumentation()) {
            return;
        }

        URL url = construct.getDocumentationResource();

        if (url == null) {
            LOGGER.warn("No documentation found for " + construct.getClass().getName());
            return;
        }

        try {
            DocumentationEntity entity = parser.parse(url, construct.getName());
            getSearcher().index(generator.getIdentity(), entity);
            generateParameters(construct, (ConstructDocumentationEntity) entity);
            generator.generate(entity);
        } catch (ParsingException e) {
            LOGGER.error("Failed to generate documentation from " + url, e);
        }
    }

    /**
     * Peek into the construct to get it's signature
     *
     * @param construct the construct
     * @param entity    the entity to push the signature into
     */
    private void generateParameters(Construct construct, ConstructDocumentationEntity entity) {
        ConstructContext context = new ConstructContext(RobotID.dummyRobot(), RobotID.dummyRobot(), construct, null, null);
        List<Parameter> parameters = new ArrayList<>();
        try(ConstructProcessor proc = construct.prepareProcess(context)) {

            for (int i = 0; i < proc.getNumberOfArguments(); i++) {
                String name = proc.getArgumentName(i);
                String type = proc.getArgumentType(i);
                String defaultValue = proc.getArgumentDefault(i);

                Parameter parameter = new Parameter(type, name);
                if (defaultValue != null) {
                    parameter.setDefault(defaultValue);
                }

                parameters.add(parameter);
            }
        }

        entity.setParameters(parameters);
    }

    private void initializeLoader() {
        pluginLoader = PluginLoader.load(XillPlugin.class);
        PLUGIN_FOLDER.mkdirs();
        pluginLoader.addFolder(PLUGIN_FOLDER);

        pluginLoader.getPluginManager().onPluginAccepted().addListener(plugin ->
                LOGGER.info("Loaded " + plugin.getClass().getSimpleName()));
    }

    private void loadPlugins() {
        try {
            pluginLoader.load();
        } catch (CircularReferenceException e) {
            throw new XillioRuntimeException(e);
        }

        pluginsLoaded = true;
    }

    /**
     * List the plugins.
     *
     * @return a list of all loaded plugins
     */
    public List<XillPlugin> getPlugins() {
        // Wait for all plugins to be loaded.
        while (!pluginsLoaded) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                LOGGER.error("Failed to list the plugins.", e);
            }
        }

        return new ArrayList<>(pluginLoader.getPluginManager().getPlugins());
    }

    /**
     * Get the loader.
     *
     * @return the last created {@link PluginLoader}
     */
    public PluginLoader<XillPlugin> getLoader() {
        return pluginLoader;
    }

    /**
     * This event is called every time the initializer is done loading plugins.
     *
     * @return the event
     */
    public Event<InitializationResult> getOnLoadComplete() {
        return onLoadComplete.getEvent();
    }

    public static class InitializationResult {
        private final URL documentationIndex;
        private final DocumentationSearcher searcher;

        public InitializationResult(URL documentationIndex, DocumentationSearcher searcher) {
            this.documentationIndex = documentationIndex;
            this.searcher = searcher;
        }

        public URL getDocumentationIndex() {
            return documentationIndex;
        }

        public DocumentationSearcher getSearcher() {
            return searcher;
        }
    }
}
