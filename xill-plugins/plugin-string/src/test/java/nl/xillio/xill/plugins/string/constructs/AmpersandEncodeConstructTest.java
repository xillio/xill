package nl.xillio.xill.plugins.string.constructs;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.plugins.string.services.string.RegexService;

import org.testng.Assert;
import org.testng.annotations.Test;

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
		RegexService regexService = mock(RegexService.class);
		when(regexService.escapeXML(stringValue)).thenReturn(returnValue);
		// Run
		MetaExpression result = AmpersandEncodeConstruct.process(string, regexService);

		// Verify
		verify(regexService, times(1)).escapeXML(stringValue);

		// Assert
		Assert.assertEquals(result.getStringValue(), returnValue);
	}
}
