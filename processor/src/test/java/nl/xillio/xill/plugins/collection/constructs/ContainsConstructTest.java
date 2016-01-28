package nl.xillio.xill.plugins.collection.constructs;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.components.ExpressionBuilderHelper;
import nl.xillio.xill.api.errors.NotImplementedException;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Map;

import static org.mockito.Mockito.*;

/**
 * Test the {@link ContainsConstruct}
 *
 * @author Sander Visser
 */
public class ContainsConstructTest extends ExpressionBuilderHelper {

    /**
     * Test the process method under normal circumstances with list input.
     */
    @Test
    public void testProcessCheckExistingValueInList() {

        @SuppressWarnings("unchecked")
        ArrayList<MetaExpression> list = mock(ArrayList.class);

        // mock
        MetaExpression input = mock(MetaExpression.class);
        MetaExpression value = mock(MetaExpression.class);
        boolean expectedOutput = true;
        when(input.getType()).thenReturn(LIST);
        when(input.getValue()).thenReturn(list);
        when(list.contains(value)).thenReturn(expectedOutput);

        // run
        MetaExpression output = ContainsConstruct.process(input, value);

        // verify
        verify(list, times(1)).contains(value);
        verify(input, times(1)).getType();
        verify(input, times(1)).getValue();

        // assert
        Assert.assertEquals(output.getBooleanValue(), expectedOutput);

    }

    /**
     * Test the process method under normal circumstances with object input.
     */
    @Test
    public void testProcessCheckExistingValueInObject() {

        @SuppressWarnings("unchecked")
        Map<String, MetaExpression> obj = mock(Map.class);

        // mock
        MetaExpression input = mock(MetaExpression.class);
        MetaExpression value = mock(MetaExpression.class);
        boolean expectedOutput = true;
        when(input.getType()).thenReturn(OBJECT);
        when(input.getValue()).thenReturn(obj);
        when(obj.containsValue(value)).thenReturn(expectedOutput);

        // run
        MetaExpression output = ContainsConstruct.process(input, value);

        // verify
        verify(obj, times(1)).containsValue(value);
        verify(input, times(1)).getType();
        verify(input, times(1)).getValue();

        // assert
        Assert.assertEquals(output.getBooleanValue(), expectedOutput);

    }

    /**
     * <p>
     * Test the process method with not implemented type
     * </p>
     * <p>
     * (since the type has not been implemented, we use ATOMIC type to check.
     * </p>
     *
     * @throws Throwable while testing
     */
    @Test(expectedExceptions = NotImplementedException.class, expectedExceptionsMessageRegExp = "This type is not implemented.")
    public void testProcessInvalidTypeCheck() throws Throwable {

        @SuppressWarnings("unchecked")
        ArrayList<MetaExpression> list = mock(ArrayList.class);

        // mock
        MetaExpression input = mock(MetaExpression.class);
        MetaExpression value = mock(MetaExpression.class);
        when(input.getType()).thenReturn(ATOMIC);
        when(input.getValue()).thenReturn(list);
        when(list.contains(value)).thenReturn(true);

        ContainsConstruct.process(input, value);

        // verify
        verify(list, times(0)).contains(value);
        verify(input, times(1)).getType();
        verify(input, times(0)).getValue();

    }

}
