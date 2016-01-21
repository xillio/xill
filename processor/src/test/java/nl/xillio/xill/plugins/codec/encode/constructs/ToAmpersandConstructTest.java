package nl.xillio.xill.plugins.codec.encode.constructs;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.plugins.codec.decode.constructs.FromAmpersandConstruct;
import nl.xillio.xill.plugins.codec.encode.services.EncoderService;
import nl.xillio.xill.plugins.codec.encode.services.EncoderServiceImpl;
import org.testng.Assert;
import org.testng.annotations.Test;

import static org.mockito.Mockito.*;

/**
 * Test the {@link FromAmpersandConstruct}.
 */
public class ToAmpersandConstructTest {

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
        EncoderService encoderService = mock(EncoderServiceImpl.class);
        ToAmpersandConstruct construct = new ToAmpersandConstruct(encoderService);
        when(encoderService.escapeXML(stringValue)).thenReturn(returnValue);
        // Run
        MetaExpression result = construct.process(string);

        // Verify
        verify(encoderService, times(1)).escapeXML(stringValue);

        // Assert
        Assert.assertEquals(result.getStringValue(), returnValue);
    }
}
