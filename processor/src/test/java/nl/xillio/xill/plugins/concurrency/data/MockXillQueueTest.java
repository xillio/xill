package nl.xillio.xill.plugins.concurrency.data;

import nl.xillio.xill.api.components.MetaExpression;
import org.slf4j.Logger;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static nl.xillio.xill.api.components.ExpressionBuilderHelper.NULL;
import static nl.xillio.xill.api.components.ExpressionBuilderHelper.fromValue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.testng.Assert.assertEquals;


public class MockXillQueueTest {

    @Test
    public void testPush() throws Exception {
        Logger logger = mock(Logger.class);
        MockXillQueue queue = new MockXillQueue(emptyList(), logger);
        queue.push(fromValue("Hello World"));
        verify(logger).info(anyString(), anyString());
    }

    @Test
    public void testIterateItems() throws Exception {
        List<MetaExpression> input = asList(fromValue("Value1"), fromValue("Value2"));

        MockXillQueue queue = new MockXillQueue(input, mock(Logger.class));

        List<MetaExpression> result = new ArrayList<>();

        while (true) {
            MetaExpression item = queue.pop();
            if(item == NULL) {
                break;
            }
            result.add(item);
        }

        assertEquals(result, input);
    }
}