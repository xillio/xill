package nl.xillio.xill.plugins.string.constructs;

import nl.xillio.xill.TestUtils;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.plugins.string.services.string.RegexService;
import nl.xillio.xill.plugins.string.services.string.StringUtilityService;
import org.testng.annotations.Test;

import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.testng.Assert.assertEquals;

/**
 * Tests for the {@link RegexEscapeConstruct}
 *
 * @author Geert Konijnendijk
 */
public class RegexEscapeConstructTest extends TestUtils {


    public static final String UNESCAPED = "unescaped", ESCAPED = "escaped";

    @Test
    public void testNormalUsage() {
        //mock
        RegexService regexService = mock(RegexService.class);
        when(regexService.escapeRegex(anyString())).thenReturn(ESCAPED);

        MetaExpression toEscape = mockExpression(ATOMIC, true, Double.NaN, UNESCAPED);

        //run
        MetaExpression result = RegexEscapeConstruct.process(toEscape, regexService);

        //verify
        verify(regexService).escapeRegex(eq(UNESCAPED));

        //assert
        assertEquals(result.getStringValue(), ESCAPED, "Escaped value does not match expected value");
    }

}
