package nl.xillio.xill.plugins.collection.constructs;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.components.ExpressionBuilderHelper;
import nl.xillio.xill.plugins.collection.services.reverse.Reverse;
import org.testng.Assert;
import org.testng.annotations.Test;

import static org.mockito.Mockito.*;

/**
 * Test the {@link ReverseConstruct}
 *
 * @author Sander Visser
 */
public class ReverseConstructTest extends ExpressionBuilderHelper {

    /**
     * Test the process method under normal circumstances.
     */
    @Test
    public void testProcessWithNormalInput() {

        // mock
        String expectedOutput = "this is the expected Value";
        MetaExpression outputExpression = fromValue(expectedOutput);

        Reverse reverse = mock(Reverse.class);
        when(reverse.asReversed(extractValue(outputExpression), true)).thenReturn(true);

        MetaExpression recursive = mock(MetaExpression.class);
        when(recursive.getBooleanValue()).thenReturn(true);

        // run
        MetaExpression output = ReverseConstruct.process(outputExpression, recursive, reverse);

        // verify
        verify(reverse, times(1)).asReversed(extractValue(outputExpression), true);
        verify(recursive, times(1)).getBooleanValue();

        // assert
        Assert.assertEquals(output, TRUE);
    }
}
