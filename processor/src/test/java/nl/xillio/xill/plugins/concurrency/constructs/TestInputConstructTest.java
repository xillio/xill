package nl.xillio.xill.plugins.concurrency.constructs;

import nl.xillio.xill.TestUtils;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.plugins.concurrency.data.XillQueue;
import org.testng.annotations.Test;

import java.util.Map;

import static org.testng.Assert.*;


public class TestInputConstructTest extends TestUtils {
    @Test
    public void testProcess() {
        MetaExpression input = list("Value 1", "Value 2");
        MetaExpression config = map("key", "value");

        MetaExpression result = process(new TestInputConstruct(), config, input);

        Map<String, MetaExpression> internalMap = result.getValue();

        assertTrue(internalMap.containsKey("key"));
        assertTrue(internalMap.containsKey("threadId"));
        assertTrue(internalMap.containsKey("input"));
        assertTrue(internalMap.containsKey("output"));

        assertTrue(internalMap.get("input").hasMeta(XillQueue.class));
        assertTrue(internalMap.get("output").hasMeta(XillQueue.class));

        XillQueue queue = internalMap.get("input").getMeta(XillQueue.class);
        assertTrue(queue.hasNext());
        assertEquals(queue.next().getStringValue(), "Value 1");
        assertEquals(queue.next().getStringValue(), "Value 2");
        assertFalse(queue.hasNext());
    }
}