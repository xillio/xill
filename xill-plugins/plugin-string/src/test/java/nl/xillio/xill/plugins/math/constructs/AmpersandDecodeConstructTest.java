package nl.xillio.xill.plugins.math.constructs;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.ExpressionBuilderHelper;
import nl.xillio.xill.plugins.string.constructs.AmpersandDecodeConstruct;
import nl.xillio.xill.plugins.string.services.string.RegexService;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Test the {@link AmpersandDecodeConstruct}.
 */
public class AmpersandDecodeConstructTest extends ExpressionBuilderHelper {

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
		RegexService regexService = mock(RegexService.class);
		when(regexService.unescapeXML(stringValue, passesValue)).thenReturn(returnValue);
		// Run
		MetaExpression result = AmpersandDecodeConstruct.process(string, passes, regexService);

		// Verify
		verify(regexService, times(1)).unescapeXML(stringValue, passesValue);

		// Assert
		Assert.assertEquals(result.getStringValue(), returnValue);
	}
}
