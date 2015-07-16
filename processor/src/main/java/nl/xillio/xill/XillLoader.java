package nl.xillio.xill;

import java.io.File;
import java.io.IOException;

import javafx.stage.Stage;
import nl.xillio.plugins.PluginLoader;
import nl.xillio.xill.api.Debugger;
import nl.xillio.xill.api.PluginPackage;
import nl.xillio.xill.api.Xill;
import nl.xillio.xill.api.XillProcessor;
import nl.xillio.xill.debugging.XillDebugger;

/**
 * This class is responsible for creating the {@link XillProcessor}
 */
public class XillLoader implements Xill, nl.xillio.contenttools.PluginPackage {

	@Override
	public XillProcessor createProcessor(final File robotFile, final File projectFolder, final PluginLoader<PluginPackage> pluginLoader) {

		return createProcessor(robotFile, projectFolder, pluginLoader, new XillDebugger());
	}

	@Override
	public void load(final nl.xillio.contenttools.PluginPackage[] dependencies) {}

	@Override
	public void start(final Stage stage, final Xill xill) {

	}

	@Override
	public Object serve() {
		return this;
	}

	@Override
	public XillProcessor createProcessor(final File robotFile, final File projectFolder, final PluginLoader<PluginPackage> pluginLoader, final Debugger debugger) {
		try {
			return new nl.xillio.xill.XillProcessor(projectFolder, robotFile, pluginLoader, debugger);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}
