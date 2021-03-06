package nl.xillio.xill.plugins.string.constructs;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.plugins.string.services.string.StringUtilityService;
import org.testng.Assert;
import org.testng.annotations.Test;

import static org.mockito.Mockito.*;

/**
 * Test the {@link IndexOfConstruct}.
 */
public class IndexOfConstructTest {

    /**
     * Test the process method under normal circumstances.
     */
    @Test
    public void processNormalUsage() {
        // Mock
        String parentValue = "abcdefgabcdefg";
        MetaExpression parent = mock(MetaExpression.class);
        when(parent.getStringValue()).thenReturn(parentValue);

        String childValue = "a";
        MetaExpression child = mock(MetaExpression.class);
        when(child.getStringValue()).thenReturn(childValue);

        int indexValue = 2;
        MetaExpression index = mock(MetaExpression.class);
        when(index.getNumberValue()).thenReturn(indexValue);

        int returnValue = 7;
        StringUtilityService stringService = mock(StringUtilityService.class);
        when(stringService.indexOf(parentValue, childValue, indexValue)).thenReturn(returnValue);
        // Run
        MetaExpression result = IndexOfConstruct.process(parent, child, index, stringService);

        // Verify
        verify(stringService, times(1)).indexOf(parentValue, childValue, indexValue);

        // Assert
        Assert.assertEquals(result.getNumberValue().intValue(), returnValue);
    }
}
