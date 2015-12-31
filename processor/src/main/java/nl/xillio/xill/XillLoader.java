package nl.xillio.xill;

import javafx.stage.Stage;
import nl.xillio.plugins.ContenttoolsPlugin;
import nl.xillio.plugins.PluginLoader;
import nl.xillio.plugins.XillPlugin;
import nl.xillio.xill.api.Debugger;
import nl.xillio.xill.api.Xill;
import nl.xillio.xill.api.XillProcessor;
import nl.xillio.xill.debugging.XillDebugger;
import xill.lang.validation.XillValidator;

import java.io.File;
import java.io.IOException;

/**
 * This class is responsible for creating the {@link XillProcessor}
 */
public class XillLoader implements Xill, ContenttoolsPlugin {

    @Override
    public XillProcessor createProcessor(final File robotFile, final File projectFolder, final PluginLoader<XillPlugin> pluginLoader) {

        return createProcessor(robotFile, projectFolder, pluginLoader, new XillDebugger());
    }

    @Override
    public void load(final ContenttoolsPlugin[] dependencies) {
    }

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
            throw new RuntimeException(e);
        }
    }

    @Override
    public String[] getReservedKeywords() {
        return XillValidator.RESERVED_KEYWORDS;
    }

}
