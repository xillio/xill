package nl.xillio.xill.api;

import com.google.inject.Injector;
import nl.xillio.plugins.PluginLoadFailure;
import nl.xillio.plugins.XillPlugin;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

/**
 * This interface represents an object that can assemble a {@link XillProcessor}.
 *
 * @author Thomas Biesaart
 */
public interface XillEnvironment {

    /**
     * The default file extension for robot files.
     */
    public static final String ROBOT_EXTENSION = ".xill";

    /**
     * The default file extension for robot templates.
     */
    public static final String ROBOT_TEMPLATE_EXTENSION = ".xilt";

    /**
     * Enables or disables loading plugins from the user's home folder.
     *
     * @param value whether plugins from home folder should be loaded
     * @return self
     */
    XillEnvironment setLoadHomeFolder(boolean value);

    /**
     * Adds a folder to the plugin loading queue.
     *
     * @param path the path to the folder
     * @return self
     * @throws IOException if the provided path is not a folder, doesn't exist or is inaccessible.
     */
    XillEnvironment addFolder(Path path) throws IOException;

    /**
     * Sets the root injector for the plugin environment.
     *
     * @param injector the injector
     * @return self
     */
    XillEnvironment setRootInjector(Injector injector);

    /**
     * Loads plugins from the added folders.
     *
     * @return self
     * @throws IOException if we could not read the plugins
     * @see XillEnvironment#addFolder(Path)
     */
    XillEnvironment loadPlugins() throws IOException;

    /**
     * Builds a processor for a specific execution.
     *
     * @param projectRoot the root folder of the project
     * @param robotPath   the path to the main robot
     * @return the processor
     * @throws IOException if we could not load the processor
     */
    XillProcessor buildProcessor(Path projectRoot, Path robotPath) throws IOException;

    /**
     * Builds a processor for a specific execution, with a debugger.
     *
     * @param projectRoot the root folder of the project
     * @param robotPath   the path to the main robot
     * @param debugger    the debugger that should be used
     * @return the processor
     * @throws IOException if we could not load the processor
     */
    XillProcessor buildProcessor(Path projectRoot, Path robotPath, Debugger debugger) throws IOException;


    /**
     * Gets a list of all loaded plugins.
     *
     * @return all loaded plugins.
     */
    List<XillPlugin> getPlugins();

    /**
     * Gets a list of found plugins that could not be loaded due to licensing issues.
     *
     * @return all rejected plugins
     */
    default List<PluginLoadFailure> getMissingLicensePlugins() {
        return Collections.emptyList();
    }
}
