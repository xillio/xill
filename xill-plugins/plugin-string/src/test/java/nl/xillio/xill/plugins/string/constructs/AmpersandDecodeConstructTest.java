package nl.xillio.xill.plugins.string.constructs;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.plugins.string.services.string.StringUtilityService;
import org.testng.Assert;
import org.testng.annotations.Test;

import static org.mockito.Mockito.*;

/**
 * Test the {@link AmpersandDecodeConstruct}.
 */
public class AmpersandDecodeConstructTest {

    /**
     * Test the process method under normal circumstances.
     */
    @Test
    public void processNormalUsage() {
        // Mock
        String stringValue = "Money &lt;&amp;gt; Health";
        MetaExpression string = mock(MetaExpression.class);
        when(string.getStringValue()).thenReturn(stringValue);

        int passesValue = 2;
        MetaExpression passes = mock(MetaExpression.class);
        when(passes.getNumberValue()).thenReturn(passesValue);

        String returnValue = "Money <> Health";
        StringUtilityService stringUtils = mock(StringUtilityService.class);
        when(stringUtils.unescapeXML(stringValue, passesValue)).thenReturn(returnValue);
        // Run
        MetaExpression result = AmpersandDecodeConstruct.process(string, passes, stringUtils);

        // Verify
        verify(stringUtils, times(1)).unescapeXML(stringValue, passesValue);

        // Assert
        Assert.assertEquals(result.getStringValue(), returnValue);
    }
}
