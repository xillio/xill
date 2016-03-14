package nl.xillio.xill.components.expressions;

import nl.xillio.xill.TestUtils;
import nl.xillio.xill.api.Debugger;
import nl.xillio.xill.api.components.InstructionFlow;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.components.MetaExpressionIterator;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.components.instructions.FunctionDeclaration;
import nl.xillio.xill.components.instructions.InstructionSet;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;


public class PeekExpressionTest extends TestUtils {
    @Test
    public void testNormal() {
        MetaExpression input = parseObject(Arrays.asList(1, 2, 3, 4, 5));
        MapExpression expression = new MapExpression(input);
        AddOneFunction addOneFunction = spy(new AddOneFunction());
        expression.setFunction(addOneFunction);

        MetaExpression result = expression.process(mock(Debugger.class)).get();
        MetaExpressionIterator iterator = result.getMeta(MetaExpressionIterator.class);

        assertEquals(iterator.next(), fromValue(2));
        assertEquals(iterator.next(), fromValue(3));
        assertEquals(iterator.next(), fromValue(4));
        assertEquals(iterator.next(), fromValue(5));
        assertEquals(iterator.next(), fromValue(6));
        assertFalse(iterator.hasNext());

        verify(addOneFunction, times(5)).run(any(), anyList());
    }

    @Test
    public void testNull() {
        MapExpression expression = new MapExpression(NULL);
        AddOneFunction addOneFunction = spy(new AddOneFunction());
        expression.setFunction(addOneFunction);

        MetaExpression result = expression.process(mock(Debugger.class)).get();
        MetaExpressionIterator iterator = result.getMeta(MetaExpressionIterator.class);

        assertFalse(iterator.hasNext());

        verify(addOneFunction, never()).run(any(), anyList());
    }

    private class AddOneFunction extends FunctionDeclaration {

        public AddOneFunction() {
            super(mock(InstructionSet.class), new ArrayList<>());
        }

        @Override
        public InstructionFlow<MetaExpression> run(Debugger debugger, List<MetaExpression> arguments) throws RobotRuntimeException {
            return InstructionFlow.doResume(fromValue(arguments.get(0).getNumberValue().intValue() + 1));
        }
    }

}