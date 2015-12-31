package nl.xillio.xill.plugins.collection.constructs;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.ExpressionBuilderHelper;
import nl.xillio.xill.plugins.collection.services.sort.Sort;
import org.testng.Assert;
import org.testng.annotations.Test;

import static org.mockito.Mockito.*;

/**
 * Test the {@link SortConstruct}
 *
 * @author Sander Visser
 */
public class SortConstructTest extends ExpressionBuilderHelper {

    /**
     * Test the process method under normal circumstances.
     */
    @Test
    public void testProcessWithNormalInput() {

        // mock
        String expectedOutput = "This is the expected output";
        MetaExpression outputExpression = fromValue(expectedOutput);

        Sort sort = mock(Sort.class);
        when(sort.asSorted(extractValue(outputExpression), true, true, true)).thenReturn(true);

        MetaExpression recursive = mock(MetaExpression.class);
        when(recursive.getBooleanValue()).thenReturn(true);

        MetaExpression onKeys = mock(MetaExpression.class);
        when(onKeys.getBooleanValue()).thenReturn(true);

        MetaExpression reverse = mock(MetaExpression.class);
        when(reverse.getBooleanValue()).thenReturn(true);

        // run
        MetaExpression output = SortConstruct.process(outputExpression, recursive, onKeys, reverse, sort);

        // verify
        verify(sort, times(1)).asSorted(extractValue(outputExpression), true, true, true);
        verify(recursive, times(1)).getBooleanValue();
        verify(onKeys, times(1)).getBooleanValue();
        verify(reverse, times(1)).getBooleanValue();

        // assert
        Assert.assertEquals(output, TRUE);
    }
}
