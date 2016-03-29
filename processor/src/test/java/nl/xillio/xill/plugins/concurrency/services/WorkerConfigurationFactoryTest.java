package nl.xillio.xill.plugins.concurrency.services;

import nl.xillio.xill.TestUtils;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.concurrency.data.WorkerConfiguration;
import org.testng.annotations.Test;

import java.util.Map;

import static org.testng.Assert.assertEquals;


public class WorkerConfigurationFactoryTest extends TestUtils {
    private final WorkerConfigurationFactory factory = new WorkerConfigurationFactory();

    @Test
    public void testMinimalConfiguration() {
        MetaExpression input = map("robot", "myRobot.xill");
        WorkerConfiguration configuration = factory.build(input);
        assertEquals(configuration.getRobot(), "myRobot.xill");
    }

    @Test
    public void testFullyExpressed() {
        MetaExpression input = map(
                "robot", "TestRobot.xill",
                "threadCount", 42,
                "queueSize", 1337
        );
        input.<Map<String, MetaExpression>>getValue().put("config", map("key", "value"));

        WorkerConfiguration configuration = factory.build(input);

        assertEquals(configuration.getRobot(), "TestRobot.xill");
        assertEquals(configuration.getConfiguration().getStringValue(), "{\"key\":\"value\"}");
        assertEquals(configuration.getOutputQueueSize(), 1337);
        assertEquals(configuration.getThreadCount(), 42);
    }

    @Test(expectedExceptions = RobotRuntimeException.class)
    public void testIllegalInput() {
        factory.build(fromValue("You shall not pass"));
    }

    @Test(expectedExceptions = RobotRuntimeException.class, expectedExceptionsMessageRegExp = ".*robot.*")
    public void testNoRobotOption() {
        factory.build(emptyObject());
    }
}