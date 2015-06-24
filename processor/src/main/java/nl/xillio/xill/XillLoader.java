package nl.xillio.xill;

import java.io.File;
import java.io.IOException;

import javafx.stage.Stage;
import nl.xillio.plugins.CircularReferenceException;
import nl.xillio.plugins.PluginLoader;
import nl.xillio.xill.api.Debugger;
import nl.xillio.xill.api.PluginPackage;
import nl.xillio.xill.api.Xill;
import nl.xillio.xill.api.XillProcessor;
import nl.xillio.xill.constructs.System.SystemPluginPackage;
import nl.xillio.xill.debugging.XillDebugger;

/**
 * This class is responsible for creating the {@link XillProcessor}
 */
public class XillLoader implements Xill, nl.xillio.contenttools.PluginPackage {

	@Override
	public XillProcessor createProcessor(File robotFile, File projectFolder, PluginLoader<PluginPackage> pluginLoader) {
		
		return createProcessor(robotFile, projectFolder, pluginLoader, new XillDebugger());
	}

	@Override
	public void load(nl.xillio.contenttools.PluginPackage[] dependencies) {
	}

	@Override
	public void start(Stage stage, Xill xill) {

	}

	@Override
	public Object serve() {
		return this;
	}

	@Override
	public XillProcessor createProcessor(File robotFile, File projectFolder, PluginLoader<PluginPackage> pluginLoader, Debugger debugger) {
	//Add the system package
			pluginLoader.addToQueue(new SystemPluginPackage());
			try {
				pluginLoader.load();
			} catch (CircularReferenceException e1) {
				e1.printStackTrace();
			}
			
			try {
				return new nl.xillio.xill.XillProcessor(projectFolder, robotFile, pluginLoader, debugger);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
	}









}
