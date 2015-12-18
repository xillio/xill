package nl.xillio.xill.plugins.hash.constructs;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.errors.RobotRuntimeException;

import nl.xillio.xill.plugins.hash.services.HashService;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import static org.mockito.Mockito.*;

/**
 * Test the {@link Md5Construct}.
 */
public class Md5ConstructTest {

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

        MetaExpression fromFile = mock(MetaExpression.class);
        when(fromFile.isNull()).thenReturn(false);
        when(fromFile.getBooleanValue()).thenReturn(false);

        String returnValue = "b45cffe084dd3d20d928bee85e7b0f21";
        HashService hashService = mock(HashService.class);
        when(hashService.md5(text, false)).thenReturn(returnValue);

        // Run
        MetaExpression result = Md5Construct.process(value, fromFile, hashService);

        // Verify

        verify(hashService, times(1)).md5(text, false);

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

        MetaExpression fromFile = mock(MetaExpression.class);
        when(fromFile.isNull()).thenReturn(false);
        when(fromFile.getBooleanValue()).thenReturn(false);

        HashService hashService = mock(HashService.class);
        when(hashService.md5(text, false)).thenThrow(new NoSuchAlgorithmException("Error occurred"));

        // Run
        Md5Construct.process(value, fromFile, hashService);
    }

    /**
     * Test the process when it throws an error.
     *
     * @throws NoSuchAlgorithmException
     * @throws IOException
     */
    @Test(expectedExceptions = RobotRuntimeException.class, expectedExceptionsMessageRegExp = "Cannot do md5 hash: Cannot open file")
    public void processIOException() throws IOException, NoSuchAlgorithmException {
        // Mock
        String text = "string";
        MetaExpression value = mock(MetaExpression.class);
        when(value.getStringValue()).thenReturn(text);

        MetaExpression fromFile = mock(MetaExpression.class);
        when(fromFile.isNull()).thenReturn(false);
        when(fromFile.getBooleanValue()).thenReturn(false);

        HashService hashService = mock(HashService.class);
        when(hashService.md5(text, false)).thenThrow(new IOException("Cannot open file"));

        // Run
        Md5Construct.process(value, fromFile, hashService);
    }
}
