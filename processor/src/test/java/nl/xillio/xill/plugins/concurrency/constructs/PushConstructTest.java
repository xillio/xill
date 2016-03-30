package nl.xillio.xill.plugins.concurrency.constructs;

import nl.xillio.xill.TestUtils;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.concurrency.data.XillQueue;
import org.testng.annotations.Test;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;


public class PushConstructTest extends TestUtils {

    @Test
    public void testProcess() {
        XillQueue queue = mock(XillQueue.class);
        MetaExpression input = fromValue("All your base are belong to us");
        input.storeMeta(queue);

        process(new PushConstruct(), fromValue("Braaaiiiinz"), input);

        verify(queue).push(eq(fromValue("Braaaiiiinz")));
    }

    @Test(expectedExceptions = RobotRuntimeException.class)
    public void testNullInput() {
        XillQueue queue = mock(XillQueue.class);
        MetaExpression input = fromValue("You shall not pass");
        input.storeMeta(queue);

        process(new PushConstruct(), NULL, input);
    }
}