package nl.xillio.xill;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import me.biesaart.utils.Log;
import nl.xillio.plugins.XillPlugin;
import nl.xillio.util.XillioHomeFolder;
import nl.xillio.xill.api.XillEnvironment;
import nl.xillio.xill.api.XillProcessor;
import nl.xillio.xill.debugging.XillDebugger;
import nl.xillio.xill.services.inject.DefaultInjectorModule;
import org.slf4j.Logger;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;

/**
 * This class is responsible for loading the processor and it's plugins.
 *
 * @author Thomas Biesaart
 */
public class XillEnvironmentImpl implements XillEnvironment {
    private static final Path HOME_PLUGIN_DIR = Paths.get(XillioHomeFolder.forXill3().toURI()).resolve("plugins");
    private static final Logger LOGGER = Log.get();
    private boolean loadHome = true;
    private final List<Path> folders = new ArrayList<>();
    private Injector rootInjector;
    private Map<String, XillPlugin> loadedPlugins = new HashMap<>();
    private boolean needLoad = true;

    @Override
    public XillEnvironment setLoadHomeFolder(boolean value) {
        loadHome = value;
        return this;
    }

    @Override
    public XillEnvironment addFolder(Path path) throws IOException {
        folders.add(path);
        return this;
    }

    @Override
    public XillEnvironment setRootInjector(Injector injector) {
        rootInjector = injector;
        return this;
    }

    public static void main(String... args) throws IOException {
        new XillEnvironmentImpl()
                .addFolder(Paths.get("C:\\Library\\Code\\Xillio\\xill\\plugins"))
                .buildProcessor(Paths.get("."), Paths.get("."));
    }

    @Override
    public XillProcessor buildProcessor(Path projectRoot, Path robotPath) throws IOException {
        if (needLoad) {
            if (rootInjector == null) {
                rootInjector = Guice.createInjector();
            }

            List<Path> folders = new ArrayList<>(this.folders);
            if (loadHome && Files.exists(HOME_PLUGIN_DIR)) {
                folders.add(HOME_PLUGIN_DIR);
            }
            loadClasspathPlugins();
            loadPlugins(folders);
            needLoad = false;

            List<Module> modules = new ArrayList<>(loadedPlugins.values());
            modules.add(new DefaultInjectorModule());
            Injector configuredInjector = rootInjector.createChildInjector(modules);

            LOGGER.info("Injecting plugin members");
            // Inject members
            loadedPlugins.values().forEach(configuredInjector::injectMembers);

            LOGGER.info("Loading constructs");
            // Load constructs
            loadedPlugins.values().forEach(XillPlugin::initialize);
        }

        return new nl.xillio.xill.XillProcessor(projectRoot.toFile(), robotPath.toFile(), new ArrayList<>(loadedPlugins.values()), new XillDebugger());
    }

    private void loadClasspathPlugins() {
        loadPlugins(ServiceLoader.load(XillPlugin.class));
    }

    private void loadPlugins(Iterable<Path> folders) throws IOException {
        for (Path folder : folders) {
            assertFolderExists(folder);

            Processor processor = new Processor();
            Files.walkFileTree(folder, processor);
            for (Path jarFile : processor.getJarFiles()) {
                URL url = jarFile.toUri().toURL();
                URLClassLoader classLoader = new URLClassLoader(new URL[]{url});
                ServiceLoader<XillPlugin> serviceLoader = ServiceLoader.load(XillPlugin.class, classLoader);
                loadPlugins(serviceLoader);
            }
        }
    }

    private void loadPlugins(ServiceLoader<XillPlugin> serviceLoader) {
        for (XillPlugin plugin : serviceLoader) {
            String name = plugin.getName();
            if (!loadedPlugins.containsKey(name)) {
                LOGGER.info("Found plugin {}", name);
                loadedPlugins.put(name, plugin);
            }
        }
    }

    private void assertFolderExists(Path path) throws IOException {
        if (!Files.exists(path)) {
            throw new NoSuchFileException("No folder found at " + path);
        }

        if (!Files.isDirectory(path)) {
            throw new NotDirectoryException(path + " is not a directory");
        }

    }

    /**
     * This class will save a list of all .jar files.
     */
    private static class Processor extends SimpleFileVisitor<Path> {
        private static final PathMatcher JAR_MATCHER = FileSystems.getDefault().getPathMatcher("glob:**.jar");
        private final List<Path> jarFiles = new ArrayList<>();

        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
            if (JAR_MATCHER.matches(file)) {
                jarFiles.add(file);
            }
            return super.visitFile(file, attrs);
        }

        public List<Path> getJarFiles() {
            return jarFiles;
        }
    }

}
