package nl.xillio.xill.components.expressions.pipeline;

import nl.xillio.xill.TestUtils;
import nl.xillio.xill.api.Debugger;
import nl.xillio.xill.api.components.MetaExpression;
import org.testng.annotations.Test;

import static java.util.Arrays.asList;
import static org.mockito.Mockito.mock;
import static org.testng.Assert.assertEquals;


public class CollectTerminalExpressionTest extends TestUtils {
    private final Debugger debugger = mock(Debugger.class);

    @Test
    public void testRunNormal() {
        MetaExpression input = parseObject(asList(1, 2, 3, 4, 67, 2, 7, 2, 7, 7, 3, 1));
        input.registerReference();

        CollectTerminalExpression expression = new CollectTerminalExpression(input);

        MetaExpression result = expression.process(debugger).get();

        assertEquals(result, input);
    }

}