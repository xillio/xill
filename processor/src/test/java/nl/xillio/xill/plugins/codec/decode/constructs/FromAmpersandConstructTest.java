package nl.xillio.xill.plugins.codec.decode.constructs;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.plugins.codec.decode.services.DecoderService;
import nl.xillio.xill.plugins.codec.decode.services.DecoderServiceImpl;
import org.testng.Assert;
import org.testng.annotations.Test;

import static org.mockito.Mockito.*;

/**
 * Test the {@link FromAmpersandConstruct}.
 */
public class FromAmpersandConstructTest {

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
        DecoderService decoderService = mock(DecoderServiceImpl.class);
        FromAmpersandConstruct construct = new FromAmpersandConstruct(decoderService);
        when(decoderService.unescapeXML(stringValue, passesValue)).thenReturn(returnValue);
        // Run
        MetaExpression result = construct.process(string, passes);

        // Verify
        verify(decoderService, times(1)).unescapeXML(stringValue, passesValue);

        // Assert
        Assert.assertEquals(result.getStringValue(), returnValue);
    }
}
