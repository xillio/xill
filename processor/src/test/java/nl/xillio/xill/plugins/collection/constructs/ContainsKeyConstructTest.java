package nl.xillio.xill.plugins.collection.constructs;

import nl.xillio.xill.api.components.ExpressionBuilderHelper;
import nl.xillio.xill.api.components.MetaExpression;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Map;

import static org.mockito.Mockito.*;

/**
 * Test the {@link ContainsKeyConstruct}
 */
public class ContainsKeyConstructTest extends ExpressionBuilderHelper {

    /**
     * Test the process when the object does contain the key.
     */
    @Test
    public void testProcessObjectDoesContainKey() {
        testNormal(true);
    }

    /**
     * Test the process when the object does not contain the key.
     */
    @Test
    public void testProcessObjectDoesNotContainKey() {
        testNormal(false);
    }

    /**
     * Test the contains key construct under normal circumstances.
     *
     * @param expected Whether the map should contain the key, which should also be the result of the process method.
     */
    private void testNormal(boolean expected) {
        // The key.
        String keyString = "foo";
        MetaExpression key = mock(MetaExpression.class);
        when(key.getStringValue()).thenReturn(keyString);

        // The map.
        Map<String, MetaExpression> map = mock(Map.class);
        when(map.containsKey(keyString)).thenReturn(expected);
        MetaExpression object = mock(MetaExpression.class);
        when(object.getValue()).thenReturn(map);

        // Run.
        MetaExpression output = ContainsKeyConstruct.process(object, key);

        // Verify.
        verify(map, times(1)).containsKey(keyString);

        // Assert.
        Assert.assertEquals(output.getBooleanValue(), expected);
    }
}
