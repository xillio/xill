package nl.xillio.xill;


import nl.xillio.xill.api.XillEnvironment;
import nl.xillio.xill.api.XillProcessor;
import nl.xillio.xill.api.errors.XillParsingException;
import org.reflections.Reflections;
import org.reflections.scanners.ResourcesScanner;
import org.testng.annotations.BeforeSuite;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * This class represents a test case that will run a robot.
 */
public abstract class RobotIntegrationTest {

    private XillEnvironment xill;
    private Path projectPath = Paths.get("integration-test", getClass().getName());

    @BeforeSuite
    public void loadXill() throws IOException {
        xill = new XillEnvironmentImpl();
        xill.addFolder(Paths.get("../plugins"));
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
        Path robotFile = projectPath.resolve(name);

        Files.copy(robot.openStream(), robotFile);

        XillProcessor processor = xill.buildProcessor(robotFile, projectPath);

        try {
            processor.compile();
        } catch (XillParsingException e) {
            throw new IOException("Failed to parse xill robot", e);
        }
        processor.getRobot().process(processor.getDebugger());
    }
}
