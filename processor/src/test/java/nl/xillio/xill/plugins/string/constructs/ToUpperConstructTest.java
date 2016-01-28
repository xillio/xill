package nl.xillio.xill.plugins.string.constructs;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.plugins.string.services.string.StringUtilityService;
import org.testng.Assert;
import org.testng.annotations.Test;

import static org.mockito.Mockito.*;

/**
 * Test the {@link ToUpperConstruct}.
 */
public class ToUpperConstructTest {

    /**
     * Test the process method under normal circumstances.
     */
    @Test
    public void processNormalUsage() {
        // Mock
        String stringValue = "testing";
        MetaExpression string = mock(MetaExpression.class);
        when(string.getStringValue()).thenReturn(stringValue);
        when(string.isNull()).thenReturn(false);

        String returnValue = "TESTING";
        StringUtilityService stringService = mock(StringUtilityService.class);
        when(stringService.toUpperCase(stringValue)).thenReturn(returnValue);
        // Run
        MetaExpression result = ToUpperConstruct.process(string, stringService);

        // Verify
        verify(stringService, times(1)).toUpperCase(stringValue);

        // Assert
        Assert.assertEquals(result.getStringValue(), returnValue);
    }
}
