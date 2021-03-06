package nl.xillio.xill.plugins.string.constructs;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.string.services.string.StringUtilityService;
import org.testng.Assert;
import org.testng.annotations.Test;

import static org.mockito.Mockito.*;

/**
 * Test the {@link EndsWithConstruct}.
 */
public class EndsWithConstructTest {

    /**
     * Test the process method under normal circumstances.
     */
    @Test
    public void processNormalUsage() {
        // Mock
        String parentValue = "testing";
        MetaExpression parent = mock(MetaExpression.class);
        when(parent.getStringValue()).thenReturn(parentValue);
        when(parent.isNull()).thenReturn(false);

        String childValue = "ing";
        MetaExpression child = mock(MetaExpression.class);
        when(child.getStringValue()).thenReturn(childValue);
        when(child.isNull()).thenReturn(false);

        boolean returnValue = true;
        StringUtilityService stringService = mock(StringUtilityService.class);
        when(stringService.endsWith(parentValue, childValue)).thenReturn(returnValue);
        // Run
        MetaExpression result = EndsWithConstruct.process(parent, child, stringService);

        // Verify
        verify(stringService, times(1)).endsWith(parentValue, childValue);

        // Assert
        Assert.assertEquals(result.getBooleanValue(), returnValue);
    }

    /**
     * Test the process when the given values are null.
     */
    @Test(expectedExceptions = RobotRuntimeException.class)
    public void processNullValueGiven() {
        // Mock
        String parentValue = "testing";
        MetaExpression parent = mock(MetaExpression.class);
        when(parent.getStringValue()).thenReturn(parentValue);
        when(parent.isNull()).thenReturn(true);

        String childValue = "ing";
        MetaExpression child = mock(MetaExpression.class);
        when(child.getStringValue()).thenReturn(childValue);
        when(child.isNull()).thenReturn(true);

        boolean returnValue = true;
        StringUtilityService stringService = mock(StringUtilityService.class);
        when(stringService.endsWith(parentValue, childValue)).thenReturn(returnValue);
        EndsWithConstruct.process(parent, child, stringService);

        // Verify
        verify(stringService, times(0)).endsWith(parentValue, childValue);
    }
}
