package nl.xillio.xill.api;

import java.io.File;

import nl.xillio.plugins.PluginLoader;
import nl.xillio.plugins.XillPlugin;

/**
 * This interface represents the main entrypoint to the Xill Language
 */
public interface Xill {
	/**
	 * The file extension used by scripts. This extension does not include the `.`
	 */
	String FILE_EXTENSION = "xill";
	int MAX_STACK_SIZE = 900;

	/**
	 * Create a new processor from this API entry point and pick a default debugger.
	 * 
	 * @param robotFile
	 *        the main robot to run
	 * @param projectFolder
	 *        the root folder of the workspace
	 * @param pluginLoader
	 *        the pluginloader
	 * @return A processor
	 * @see PluginLoader
	 * @see XillPlugin
	 */
	XillProcessor createProcessor(final File robotFile, final File projectFolder, final PluginLoader<XillPlugin> pluginLoader);

	/**
	 * Create a new processor from this API entry point.
	 * 
	 * @param robotFile
	 *        the main robot to run
	 * @param projectFolder
	 *        the root folder of the workspace
	 * @param pluginLoader
	 *        the pluginloader
	 * @param debugger
	 *        the debugger to put the debugging info into
	 * @return A processor
	 * @see PluginLoader
	 * @see XillPlugin
	 */
	XillProcessor createProcessor(final File robotFile, final File projectFolder, final PluginLoader<XillPlugin> pluginLoader, final Debugger debugger);
}
