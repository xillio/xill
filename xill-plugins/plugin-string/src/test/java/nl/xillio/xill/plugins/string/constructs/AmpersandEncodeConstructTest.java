package nl.xillio.xill.plugins.string.constructs;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.plugins.string.services.string.StringUtilityService;
import org.testng.Assert;
import org.testng.annotations.Test;

import static org.mockito.Mockito.*;

/**
 * Test the {@link AmpersandDecodeConstruct}.
 */
public class AmpersandEncodeConstructTest {

    /**
     * Test the process method under normal circumstances.
     */
    @Test
    public void processNormalUsage() {
        // Mock
        String stringValue = "<p><a href=\"default.asp\">HTML Tutorial</a> This is a link to a page on this website.</p>";
        MetaExpression string = mock(MetaExpression.class);
        when(string.getStringValue()).thenReturn(stringValue);

        String returnValue = "&lt;p&gt;&lt;a href=&quot;default.asp&quot;&gt;HTML Tutorial&lt;/a&gt; This is a link to a page on this website.&lt;/p&gt";
        StringUtilityService stringUtils = mock(StringUtilityService.class);
        when(stringUtils.escapeXML(stringValue)).thenReturn(returnValue);
        // Run
        MetaExpression result = AmpersandEncodeConstruct.process(string, stringUtils);

        // Verify
        verify(stringUtils, times(1)).escapeXML(stringValue);

        // Assert
        Assert.assertEquals(result.getStringValue(), returnValue);
    }
}
