package nl.xillio.xill;

import java.io.File;
import java.io.IOException;

import javafx.stage.Stage;
import nl.xillio.plugins.ContenttoolsPlugin;
import nl.xillio.plugins.PluginLoader;
import nl.xillio.plugins.XillPlugin;
import nl.xillio.xill.api.Debugger;
import nl.xillio.xill.api.Xill;
import nl.xillio.xill.api.XillProcessor;
import nl.xillio.xill.debugging.XillDebugger;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import xill.lang.validation.XillValidator;

/**
 * This class is responsible for creating the {@link XillProcessor}
 */
public class XillLoader implements Xill, ContenttoolsPlugin {

	private static final Logger LOGGER = LogManager.getLogger(XillLoader.class);

	@Override
	public XillProcessor createProcessor(final File robotFile, final File projectFolder, final PluginLoader<XillPlugin> pluginLoader) {

		return createProcessor(robotFile, projectFolder, pluginLoader, new XillDebugger());
	}

	@Override
	public void load(final ContenttoolsPlugin[] dependencies) {}

	@Override
	public void start(final Stage stage, final Xill xill) {

	}

	@Override
	public Object serve() {
		return this;
	}

	@Override
	public XillProcessor createProcessor(final File robotFile, final File projectFolder, final PluginLoader<XillPlugin> pluginLoader, final Debugger debugger) {
		try {
			return new nl.xillio.xill.XillProcessor(projectFolder, robotFile, pluginLoader, debugger);
		} catch (IOException e) {
			LOGGER.error("Failed to create processor.", e);
			// Did not create a specific exception, because it was no trivial matter to add a correct solution.
			throw new RuntimeException(e);
		}
	}

	@Override
	public String[] getReservedKeywords() {
		return XillValidator.RESERVED_KEYWORDS;
	}

}
