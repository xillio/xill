package nl.xillio.xill.components.expressions.pipeline;

import nl.xillio.xill.TestUtils;
import nl.xillio.xill.api.Debugger;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.components.MetaExpressionIterator;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;

import static java.util.Arrays.asList;
import static org.mockito.Mockito.mock;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;


public class ConsumeTerminalExpressionTest extends TestUtils {
    @Test
    public void testListLength() {
        MetaExpression input = parseObject(asList(1, 2, 3));

        ConsumeTerminalExpression expression = new ConsumeTerminalExpression(input);

        MetaExpression result = expression.process(mock(Debugger.class)).get();

        assertEquals(result.getNumberValue().intValue(), 3);
    }

    @Test
    public void testEmptyListLength() {
        ConsumeTerminalExpression expression = new ConsumeTerminalExpression(emptyList());

        MetaExpression result = expression.process(mock(Debugger.class)).get();

        assertEquals(result.getNumberValue().intValue(), 0);
    }

    @Test
    public void testIterableLength() {
        MetaExpression input = fromValue("UNIT TEST");
        Iterator<Integer> iterator = Arrays.asList(1, 2, 3, 4, 5).iterator();
        input.storeMeta(new MetaExpressionIterator<>(iterator, TestUtils::fromValue));

        MetaExpression result = new ConsumeTerminalExpression(input).process(mock(Debugger.class)).get();

        assertEquals(result.getNumberValue().intValue(), 5);
        assertFalse(iterator.hasNext());
    }

    @Test
    public void testObjectLength() {
        LinkedHashMap<String, MetaExpression> map = new LinkedHashMap<>();
        map.put("Hello", fromValue("World"));
        map.put("Howdy ", fromValue("Y'all"));
        MetaExpression input = fromValue(map);

        ConsumeTerminalExpression expression = new ConsumeTerminalExpression(input);

        MetaExpression result = expression.process(mock(Debugger.class)).get();
        assertEquals(result.getNumberValue().intValue(), 2);
    }

    @Test
    public void testNull() {
        ConsumeTerminalExpression expression = new ConsumeTerminalExpression(NULL);

        MetaExpression result = expression.process(mock(Debugger.class)).get();
        assertEquals(result.getNumberValue().intValue(), 0);
    }
}