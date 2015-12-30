package nl.xillio.xill.plugins.codec.encode.constructs;

import nl.xillio.xill.TestUtils;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.codec.encode.services.EncoderService;
import nl.xillio.xill.plugins.codec.encode.services.EncoderServiceImpl;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.UnsupportedEncodingException;

import static org.mockito.Mockito.*;

/**
 * Test the {@link ToPercentConstruct}.
 */
public class ToPercentConstructTest extends TestUtils {

    /**
     * Test the process method under normal circumstances.
     */
    @Test
    public void processNormalUsage() throws UnsupportedEncodingException {
        // Mock
        String inputValue = "this+-is / test";
        String resultValue = "this%2B-is%20%2F%20test";

        MetaExpression textVar = fromValue(inputValue);
        MetaExpression xWwwFormVar = NULL;

        EncoderService stringService = new EncoderServiceImpl(null, null);
        ToPercentConstruct construct = new ToPercentConstruct(stringService);

        // Run
        MetaExpression result = construct.process(textVar, xWwwFormVar);

        // Assert
        Assert.assertEquals(result.getStringValue(), resultValue);
    }

    /**
     * Test the process method when URL encoding fails.
     */
    @Test(expectedExceptions = RobotRuntimeException.class, expectedExceptionsMessageRegExp = "Cannot URL encode the string")
    public void processUnsupportedEncodingException() throws UnsupportedEncodingException {
        // Mock
        String inputValue = "this+-is / test";
        String resultValue = "this%2B-is%20%2F%20test";

        MetaExpression textVar = mock(MetaExpression.class);
        when(textVar.isNull()).thenReturn(false);
        when(textVar.getStringValue()).thenReturn(inputValue);

        MetaExpression xWwwFormVar = mock(MetaExpression.class);
        when(xWwwFormVar.isNull()).thenReturn(true);

        EncoderService stringService = mock(EncoderService.class);
        when(stringService.urlEncode(inputValue, false)).thenThrow(new UnsupportedEncodingException());
        ToPercentConstruct construct = new ToPercentConstruct(stringService);
        // Run
        construct.process(textVar, xWwwFormVar);
    }
}
