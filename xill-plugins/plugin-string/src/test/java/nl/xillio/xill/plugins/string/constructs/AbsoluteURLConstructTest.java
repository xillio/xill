package nl.xillio.xill.plugins.string.constructs;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.string.services.string.UrlService;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Test the {@link AbsoluteURLConstruct}.
 */
public class AbsoluteURLConstructTest {

	/**
	 * Test the process method under normal circumstances.
	 */
	@Test
	public void processNormalUsage() {
		// Mock
		String pageUrlValue = "http://www.xillio.nl/calendar/";
		MetaExpression pageUrl = mock(MetaExpression.class);
		when(pageUrl.getStringValue()).thenReturn(pageUrlValue);

		String relativeUrlValue = "../";
		MetaExpression relativeUrl = mock(MetaExpression.class);
		when(relativeUrl.getStringValue()).thenReturn(relativeUrlValue);

		String UrlReturnValue = "http://www.xillio.nl/";
		UrlService url = mock(UrlService.class);
		when(url.tryConvert(pageUrlValue, relativeUrlValue)).thenReturn(UrlReturnValue);

		// Run
		MetaExpression result = AbsoluteURLConstruct.process(pageUrl, relativeUrl, url);

		// Verify
		verify(url, times(1)).tryConvert(pageUrlValue, relativeUrlValue);

		// Assert
		Assert.assertEquals(result.getStringValue(), UrlReturnValue);
	}

	/**
	 * Tests the process when an empty relativeUrl is handed, in which case the last slash is deleted and the url is cleaned.
	 */
	@Test
	public void processEmptyRelativeUrl() {
		// Mock
		String pageUrlValue = "http://www.xillio.nl/calendar/";
		MetaExpression pageUrl = mock(MetaExpression.class);
		when(pageUrl.getStringValue()).thenReturn(pageUrlValue);

		String relativeUrlValue = "";
		MetaExpression relativeUrl = mock(MetaExpression.class);
		when(relativeUrl.getStringValue()).thenReturn(relativeUrlValue);

		String UrlReturnValue = "http://www.xillio.nl/calendar";
		UrlService url = mock(UrlService.class);
		when(url.tryConvert(pageUrlValue, relativeUrlValue)).thenReturn(UrlReturnValue);
		when(url.cleanupUrl(UrlReturnValue)).thenReturn(UrlReturnValue);

		// Run
		MetaExpression result = AbsoluteURLConstruct.process(pageUrl, relativeUrl, url);

		// Verify
		verify(url, times(0)).tryConvert(any(), any());
		verify(url, times(1)).cleanupUrl(UrlReturnValue);

		// Assert
		Assert.assertEquals(result.getStringValue(), UrlReturnValue);
	}

	/**
	 * Tests the process when it fails to return a value.
	 */
	@Test(expectedExceptions = RobotRuntimeException.class, expectedExceptionsMessageRegExp = "The page url is invalid.")
	public void processFailureToConvert()
	{
		// Mock
		String pageUrlValue = "http://www.xillio.nl/calendar/";
		MetaExpression pageUrl = mock(MetaExpression.class);
		when(pageUrl.getStringValue()).thenReturn(pageUrlValue);

		String relativeUrlValue = "../";
		MetaExpression relativeUrl = mock(MetaExpression.class);
		when(relativeUrl.getStringValue()).thenReturn(relativeUrlValue);

		UrlService url = mock(UrlService.class);
		when(url.tryConvert(pageUrlValue, relativeUrlValue)).thenReturn(null);

		// Run
		AbsoluteURLConstruct.process(pageUrl, relativeUrl, url);

		// Verify
		verify(url, times(1)).tryConvert(pageUrlValue, relativeUrlValue);
	}

	/**
	 * Tests the process when it fails to return a value.
	 */
	@Test(expectedExceptions = RobotRuntimeException.class, expectedExceptionsMessageRegExp = "Illegal argument was handed to the matcher when trying to convert the URL")
	public void processErrorOnConvert()
	{
		// Mock
		String pageUrlValue = "http://www.xillio.nl/calendar/";
		MetaExpression pageUrl = mock(MetaExpression.class);
		when(pageUrl.getStringValue()).thenReturn(pageUrlValue);

		String relativeUrlValue = "../";
		MetaExpression relativeUrl = mock(MetaExpression.class);
		when(relativeUrl.getStringValue()).thenReturn(relativeUrlValue);

		UrlService url = mock(UrlService.class);
		when(url.tryConvert(pageUrlValue, relativeUrlValue)).thenThrow(new IllegalArgumentException());

		// Run
		AbsoluteURLConstruct.process(pageUrl, relativeUrl, url);

		// Verify
		verify(url, times(1)).tryConvert(pageUrlValue, relativeUrlValue);
	}
}
