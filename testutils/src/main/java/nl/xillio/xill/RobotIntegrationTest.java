package nl.xillio.xill;


import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import me.biesaart.utils.FileUtils;
import nl.xillio.plugins.CircularReferenceException;
import nl.xillio.plugins.ContenttoolsPlugin;
import nl.xillio.plugins.PluginLoader;
import nl.xillio.plugins.XillPlugin;
import nl.xillio.xill.api.Xill;
import nl.xillio.xill.api.XillProcessor;
import nl.xillio.xill.api.errors.XillParsingException;
import nl.xillio.xill.services.inject.DefaultInjectorModule;
import org.reflections.Reflections;
import org.reflections.scanners.ResourcesScanner;
import org.testng.annotations.BeforeSuite;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

/**
 * This class represents a test case that will run a robot.
 */
public abstract class RobotIntegrationTest {

    private Xill xill;
    private File projectPath = new File("integration-test", getClass().getName());
    private File pluginPath = new File("../plugins");
    private PluginLoader<XillPlugin> pluginLoader;

    @BeforeSuite
    public void loadXill() throws IOException {
        ServiceLoader<ContenttoolsPlugin> loader = ServiceLoader.load(ContenttoolsPlugin.class);
        for (ContenttoolsPlugin plugin : loader) {
            Object object = plugin.serve();

            if (object != null) {
                this.xill = (Xill) object;
                break;
            }
        }

        if (xill == null) {
            throw new IOException("Could not find xill in classpath");
        }

        pluginLoader = PluginLoader.load(XillPlugin.class);
        pluginLoader.addFolder(pluginPath);
        try {
            pluginLoader.load();
        } catch (CircularReferenceException e) {
            throw new IOException("Could not load plugins", e);
        }

        List<Module> modules = new ArrayList<>(pluginLoader.getPluginManager().getPlugins());
        modules.add(new DefaultInjectorModule());
        Injector injector = Guice.createInjector(modules);
        pluginLoader.getPluginManager().getPlugins().forEach(injector::injectMembers);
        pluginLoader.getPluginManager().getPlugins().forEach(XillPlugin::initialize);
    }

    protected String getPackage() {
        return "tests";
    }

    public Object[][] getRobots() {
        Reflections reflections = new Reflections(getPackage(), new ResourcesScanner());
        return reflections
                .getResources(a -> true)
                .stream()
                .map(resource -> new Object[]{getClass().getResource("/" + resource), resource})
                .toArray(Object[][]::new);
    }

    public void runRobot(URL robot, String name) throws IOException {
        File robotFile = new File(projectPath, name);

        FileUtils.copyInputStreamToFile(robot.openStream(), robotFile);

        XillProcessor processor = xill.createProcessor(robotFile, projectPath, pluginLoader);

        try {
            processor.compile();
        } catch (XillParsingException e) {
            throw new IOException("Failed to parse xill robot", e);
        }
        processor.getRobot().process(processor.getDebugger());
    }
}
