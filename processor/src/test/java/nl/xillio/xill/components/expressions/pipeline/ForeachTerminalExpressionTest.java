package nl.xillio.xill.components.expressions.pipeline;

import nl.xillio.xill.TestUtils;
import nl.xillio.xill.api.Debugger;
import nl.xillio.xill.api.components.InstructionFlow;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.components.instructions.FunctionDeclaration;
import org.testng.annotations.Test;

import static java.util.Arrays.asList;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;


public class ForeachTerminalExpressionTest extends TestUtils {

    @Test
    public void testList() {
        FunctionDeclaration declaration = mockDeclaration();
        run(asList(1, 2, 3), declaration);
        verify(declaration, times(3)).run(any(), anyList());
    }

    @Test(expectedExceptions = IllegalStateException.class)
    public void testCloseException() {
        MetaExpression input = fromValue("I will be closed");
        run(input, mockDeclaration());
        input.getType(); // This will trigger an exception if the expression is closed
    }

    private void run(Object input, FunctionDeclaration function) {
        run(parseObject(input), function);
    }

    private void run(MetaExpression inputValue, FunctionDeclaration function) {
        ForeachTerminalExpression expression = new ForeachTerminalExpression(inputValue);
        expression.setFunction(function);
        expression.process(mock(Debugger.class));
    }

    private FunctionDeclaration mockDeclaration() {
        FunctionDeclaration declaration = mock(FunctionDeclaration.class);
        when(declaration.run(any(), anyList())).thenReturn(InstructionFlow.doResume(NULL));
        return declaration;
    }
}