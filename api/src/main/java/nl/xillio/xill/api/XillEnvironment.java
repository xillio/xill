package nl.xillio.xill.api;

import com.google.inject.Injector;
import nl.xillio.plugins.XillPlugin;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

/**
 * This interface represents an object that can assemble {@link XillProcessor}.
 *
 * @author Thomas Biesaart
 */
public interface XillEnvironment {
    /**
     * Enable or disable loading plugins from the user's home folder.
     *
     * @param value true if plugins from home folder should be loaded
     * @return self
     */
    XillEnvironment setLoadHomeFolder(boolean value);

    /**
     * Add a folder to the plugin loading queue.
     *
     * @param path the path to the folder
     * @return self
     * @throws IOException if the provided path is not a folder or doesn't exist
     */
    XillEnvironment addFolder(Path path) throws IOException;

    /**
     * Set the root injector for the plugin environment.
     *
     * @param injector the injector
     * @return self
     */
    XillEnvironment setRootInjector(Injector injector);

    /**
     * Load plugins from the added folders.
     *
     * @return self
     */
    XillEnvironment loadPlugins() throws IOException;

    /**
     * Build a processor for a specific execution.
     *
     * @param projectRoot the root folder of the project
     * @param robotPath   the path to the main robot
     * @return the processor
     */
    XillProcessor buildProcessor(Path projectRoot, Path robotPath) throws IOException;

    /**
     * Build a processor for a specific execution.
     *
     * @param projectRoot the root folder of the project
     * @param robotPath   the path to the main robot
     * @param debugger    the debugger that should be used
     * @return the processor
     */
    XillProcessor buildProcessor(Path projectRoot, Path robotPath, Debugger debugger) throws IOException;


    /**
     * Get a list of all loaded plugins.
     *
     * @return the list
     */
    List<XillPlugin> getPlugins();
}
