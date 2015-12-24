package nl.xillio.xill.plugins.codec.hash.constructs;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.codec.hash.services.HashService;
import nl.xillio.xill.plugins.codec.hash.services.HashServiceImpl;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.*;

/**
 * Test the {@link StringToMD5Construct}
 */
public class StringToMD5ConstructTest {
    /**
     * Test the process method under normal circumstances.
     *
     * @throws NoSuchAlgorithmException
     * @throws IOException
     */
    @Test
    public void processNormalUsage() throws IOException, NoSuchAlgorithmException {
        // Mock
        String text = "string";
        MetaExpression value = mock(MetaExpression.class);
        when(value.getStringValue()).thenReturn(text);

        String returnValue = "b45cffe084dd3d20d928bee85e7b0f21";
        HashService hashService = new HashServiceImpl();

        StringToMD5Construct construct = new StringToMD5Construct(hashService);

        // Run
        MetaExpression result = construct.process(value);

        // Assert
        Assert.assertEquals(result.getStringValue(), returnValue);
    }

    /**
     * Test the process when it throws an error.
     *
     * @throws NoSuchAlgorithmException
     * @throws IOException
     */
    @Test(expectedExceptions = RobotRuntimeException.class, expectedExceptionsMessageRegExp = "Cannot do md5 hash: Error occurred")
    public void processNoSuchAlgorithmException() throws IOException, NoSuchAlgorithmException {
        // Mock
        String text = "string";
        MetaExpression value = mock(MetaExpression.class);
        when(value.getStringValue()).thenReturn(text);

        HashService hashService = mock(HashService.class);
        when(hashService.stringToMD5(text)).thenThrow(new NoSuchAlgorithmException("Error occurred"));

        StringToMD5Construct construct = new StringToMD5Construct(hashService);

        // Run
        construct.process(value);
    }

}