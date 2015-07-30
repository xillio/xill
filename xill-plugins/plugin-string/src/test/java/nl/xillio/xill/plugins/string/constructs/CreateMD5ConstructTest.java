package nl.xillio.xill.plugins.string.constructs;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.string.services.string.StringUtilityService;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.security.NoSuchAlgorithmException;

import static org.mockito.Mockito.*;

/**
 * Test the {@link CreateMD5Construct}.
 */
public class CreateMD5ConstructTest {

    /**
     * Test the process method under normal circumstances.
     *
     * @throws NoSuchAlgorithmException
     */
    @Test
    public void processNormalUsage() throws NoSuchAlgorithmException {
        // Mock
        String text = "string";
        MetaExpression value = mock(MetaExpression.class);
        when(value.getStringValue()).thenReturn(text);

        String returnValue = "b45cffe084dd3d20d928bee85e7b0f21";
        StringUtilityService stringUtils = mock(StringUtilityService.class);
        when(stringUtils.createMD5Construct(text)).thenReturn(returnValue);

        // Run
        MetaExpression result = CreateMD5Construct.process(value, stringUtils);

        // Verify

        verify(stringUtils, times(1)).createMD5Construct(text);

        // Assert
        Assert.assertEquals(result.getStringValue(), returnValue);
    }

    /**
     * Test the process when it throws an error.
     *
     * @throws NoSuchAlgorithmException
     */
    @Test(expectedExceptions = RobotRuntimeException.class, expectedExceptionsMessageRegExp = "No such algorithm")
    public void processNoAlgorithmError() throws NoSuchAlgorithmException {
        // Mock
        String text = "string";
        MetaExpression value = mock(MetaExpression.class);
        when(value.getStringValue()).thenReturn(text);

        NoSuchAlgorithmException returnValue = new NoSuchAlgorithmException();
        StringUtilityService stringUtils = mock(StringUtilityService.class);
        when(stringUtils.createMD5Construct(text)).thenThrow(returnValue);

        // Run
        CreateMD5Construct.process(value, stringUtils);

        // Verify
        verify(stringUtils, times(1)).createMD5Construct(text);
    }
}
