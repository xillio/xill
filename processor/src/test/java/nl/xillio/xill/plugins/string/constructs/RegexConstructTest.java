package nl.xillio.xill.plugins.string.constructs;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.ExpressionBuilderHelper;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.string.exceptions.FailedToGetMatcherException;
import nl.xillio.xill.plugins.string.services.string.RegexService;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;
import java.util.regex.PatternSyntaxException;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 * Test the {@link RegexConstruct}.
 */
public class RegexConstructTest extends ExpressionBuilderHelper {

    /**
     * Test the process method with an ATOMIC value given.
     *
     * @throws FailedToGetMatcherException
     * @throws IllegalArgumentException
     * @throws PatternSyntaxException
     */
    @Test
    public void processStandardInput() throws IllegalArgumentException, FailedToGetMatcherException {
        // Mock
        String valueValue = "I need a doctor";
        MetaExpression value = mock(MetaExpression.class);
        when(value.getStringValue()).thenReturn(valueValue);

        String regexValue = ".*doctor*.";
        MetaExpression regex = mock(MetaExpression.class);
        when(regex.getStringValue()).thenReturn(regexValue);

        int timeoutValue = 10;
        MetaExpression timeout = mock(MetaExpression.class);
        when(timeout.getNumberValue()).thenReturn(timeoutValue);

        List<String> resultValue = Arrays.asList("a", "b", "c");
        RegexService regexService = mock(RegexService.class);
        when(regexService.matches(any())).thenReturn(true);
        when(regexService.tryMatchElseNull(any())).thenReturn(resultValue);
        // Run
        MetaExpression result = RegexConstruct.process(value, regex, timeout, regexService);

        // Verify
        verify(regexService, times(1)).matches(any());
        verify(regexService, times(1)).tryMatchElseNull(any());
        verify(regexService, times(1)).getMatcher(regexValue, valueValue, timeoutValue * 1000);

        // Assert
        Assert.assertEquals(result.getType(), LIST);
        @SuppressWarnings("unchecked")
        List<MetaExpression> resultAsList = (List<MetaExpression>) result.getValue();
        Assert.assertEquals(resultAsList.get(0).getStringValue(), "a");
        Assert.assertEquals(resultAsList.get(1).getStringValue(), "b");
        Assert.assertEquals(resultAsList.get(2).getStringValue(), "c");
    }

    /**
     * Test the process method for when it throws an error.
     *
     * @throws FailedToGetMatcherException
     * @throws IllegalArgumentException
     * @throws PatternSyntaxException
     */
    @Test(expectedExceptions = RobotRuntimeException.class, expectedExceptionsMessageRegExp = "Invalid pattern in regex\\(\\)")
    public void processInvalidPattern() throws IllegalArgumentException, FailedToGetMatcherException {
        // Mock
        String valueValue = "I need a doctor";
        MetaExpression value = mock(MetaExpression.class);
        when(value.getStringValue()).thenReturn(valueValue);

        String regexValue = ".*doctor*.";
        MetaExpression regex = mock(MetaExpression.class);
        when(regex.getStringValue()).thenReturn(regexValue);

        int timeoutValue = 10;
        MetaExpression timeout = mock(MetaExpression.class);
        when(timeout.getNumberValue()).thenReturn(timeoutValue);

        Exception returnValue = new PatternSyntaxException(regexValue, regexValue, timeoutValue);
        RegexService regexService = mock(RegexService.class);
        when(regexService.getMatcher(regexValue, valueValue, timeoutValue * 1000)).thenThrow(returnValue);
        RegexConstruct.process(value, regex, timeout, regexService);

        // Verify
        verify(regexService, times(1)).matches(any());
        verify(regexService, times(1)).getMatcher(regexValue, valueValue, timeoutValue * 1000);
    }

    /**
     * Test the process method for when it throws an error.
     *
     * @throws FailedToGetMatcherException
     * @throws IllegalArgumentException
     * @throws PatternSyntaxException
     */
    @Test(expectedExceptions = RobotRuntimeException.class, expectedExceptionsMessageRegExp = "Error while executing the regex")
    public void processIllegalArgument() throws IllegalArgumentException, FailedToGetMatcherException {
        // Mock
        String valueValue = "I need a doctor";
        MetaExpression value = mock(MetaExpression.class);
        when(value.getStringValue()).thenReturn(valueValue);

        String regexValue = ".*doctor*.";
        MetaExpression regex = mock(MetaExpression.class);
        when(regex.getStringValue()).thenReturn(regexValue);

        int timeoutValue = 10;
        MetaExpression timeout = mock(MetaExpression.class);
        when(timeout.getNumberValue()).thenReturn(timeoutValue);

        Exception returnValue = new IllegalArgumentException();
        RegexService regexService = mock(RegexService.class);
        when(regexService.getMatcher(regexValue, valueValue, timeoutValue * 1000)).thenThrow(returnValue);
        RegexConstruct.process(value, regex, timeout, regexService);

        // Verify
        verify(regexService, times(1)).matches(any());
        verify(regexService, times(1)).getMatcher(regexValue, valueValue, timeoutValue * 1000);
    }

    /**
     * Tests if the process returns NULL if no matches are found.
     *
     * @throws FailedToGetMatcherException
     * @throws IllegalArgumentException
     * @throws PatternSyntaxException
     */
    @Test
    public void processNoMatches() throws IllegalArgumentException, FailedToGetMatcherException {
        // Mock
        String valueValue = "I need a doctor";
        MetaExpression value = mock(MetaExpression.class);
        when(value.getStringValue()).thenReturn(valueValue);

        String regexValue = ".*doctor*.";
        MetaExpression regex = mock(MetaExpression.class);
        when(regex.getStringValue()).thenReturn(regexValue);

        int timeoutValue = 10;
        MetaExpression timeout = mock(MetaExpression.class);
        when(timeout.getNumberValue()).thenReturn(timeoutValue);

        List<String> resultValue = Arrays.asList("a", "b", "c");
        RegexService regexService = mock(RegexService.class);
        when(regexService.matches(any())).thenReturn(false);
        when(regexService.tryMatchElseNull(any())).thenReturn(resultValue);
        // Run
        MetaExpression result = RegexConstruct.process(value, regex, timeout, regexService);

        // Verify
        verify(regexService, times(1)).matches(any());
        verify(regexService, times(0)).tryMatchElseNull(any());
        verify(regexService, times(1)).getMatcher(regexValue, valueValue, timeoutValue * 1000);

        // Assert
        Assert.assertEquals(result, NULL);
    }
}
