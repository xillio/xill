package nl.xillio.xill.api;

import java.io.File;

import nl.xillio.plugins.PluginLoader;

/**
 * This interface represents the main entrypoint to the Xill Language
 */
public interface Xill {
	/**
	 * The file extension used by scripts. This extension does not include the `.`
	 */
	public static String FILE_EXTENSION = "xill";

	/**
	 * @param robotFolder
	 * @param projectFolder
	 * @param pluginLoader
	 * @return A processor
	 */
	public XillProcessor createProcessor(final File robotFolder, final File projectFolder, final PluginLoader<PluginPackage> pluginLoader);

	/**
	 * @param robotFolder
	 * @param projectFolder
	 * @param pluginLoader
	 * @param debugger
	 * @return A processor
	 */
	public XillProcessor createProcessor(final File robotFolder, final File projectFolder, final PluginLoader<PluginPackage> pluginLoader, final Debugger debugger);
}
