package nl.xillio.xill.plugins.web.constructs;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.net.MalformedURLException;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.ExpressionBuilderHelper;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.web.data.Options;
import nl.xillio.xill.plugins.web.data.OptionsFactory;
import nl.xillio.xill.plugins.web.data.PageVariable;
import nl.xillio.xill.plugins.web.services.web.WebService;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Test the {@link LoadPageConstruct}.
 *
 */
public class LoadPageConstructTest extends ExpressionBuilderHelper {

	/**
	 * Test the process under normal circumstances.
	 *
	 * @throws Exception
	 */
	@Test
	public void testNormalUsage() throws Exception {

		// mock
		WebService webService = mock(WebService.class);
		OptionsFactory optionsFactory = mock(OptionsFactory.class);
		Options returnedOptions = mock(Options.class);

		// The url
		String urlValue = "This is an url";
		MetaExpression url = mock(MetaExpression.class);
		when(url.getStringValue()).thenReturn(urlValue);

		// The options
		MetaExpression options = mock(MetaExpression.class);

		// the process
		// There is currently no other way to do this; we have to sort out the functions one day.
		when(optionsFactory.processOptions(options)).thenReturn(returnedOptions);
		PageVariable pageVariable = mock(PageVariable.class);
		when(webService.getPageFromPool(any(), any())).thenReturn(pageVariable);
		when(webService.getCurrentUrl(pageVariable)).thenReturn(urlValue);

		// run
		MetaExpression output = LoadPageConstruct.process(url, options, optionsFactory, webService);

		// verify
		verify(optionsFactory, times(1)).processOptions(options);
		verify(webService, times(1)).getPageFromPool(any(), any());
		verify(webService, times(1)).getCurrentUrl(pageVariable);

		// assert
		Assert.assertEquals(output.getStringValue(), urlValue);
	}

	/**
	 * Test the error handling when we {@link OptionsFactory} fails to process the options.
	 *
	 * @throws Exception
	 */
	@Test(expectedExceptions = RobotRuntimeException.class, expectedExceptionsMessageRegExp = "Failed to parse the options.")
	public void testFailedToParseOptions() throws Exception {

		// mock
		WebService webService = mock(WebService.class);
		OptionsFactory optionsFactory = mock(OptionsFactory.class);
		mock(Options.class);

		// The url
		String urlValue = "This is an url";
		MetaExpression url = mock(MetaExpression.class);
		when(url.getStringValue()).thenReturn(urlValue);

		// The options
		MetaExpression options = mock(MetaExpression.class);

		// the process
		// There is currently no other way to do this; we have to sort out the functions one day.
		when(optionsFactory.processOptions(options)).thenThrow(new RobotRuntimeException("Failed to parse the options."));
		PageVariable pageVariable = mock(PageVariable.class);
		when(webService.getPageFromPool(any(), any())).thenReturn(pageVariable);
		when(webService.getCurrentUrl(pageVariable)).thenReturn(urlValue);

		// run
		MetaExpression output = LoadPageConstruct.process(url, options, optionsFactory, webService);

		// verify
		verify(optionsFactory, times(1)).processOptions(options);
		verify(webService, times(1)).getPageFromPool(any(), any());
		verify(webService, times(1)).getCurrentUrl(pageVariable);

		// assert
		Assert.assertEquals(output.getStringValue(), urlValue);
	}

	/**
	 * Test the error handling when httpGet returns a MalFormedURLException.
	 *
	 * @throws Exception
	 */
	@Test(expectedExceptions = RobotRuntimeException.class, expectedExceptionsMessageRegExp = "Malformed URL during httpGet.")
	public void testMalFormedURL() throws Exception {

		// mock
		WebService webService = mock(WebService.class);
		OptionsFactory optionsFactory = mock(OptionsFactory.class);
		Options returnedOptions = mock(Options.class);

		// The url
		String urlValue = "This is an url";
		MetaExpression url = mock(MetaExpression.class);
		when(url.getStringValue()).thenReturn(urlValue);

		// The options
		MetaExpression options = mock(MetaExpression.class);

		// the process
		// There is currently no other way to do this; we have to sort out the functions one day.
		when(optionsFactory.processOptions(options)).thenReturn(returnedOptions);;
		PageVariable pageVariable = mock(PageVariable.class);
		when(webService.getPageFromPool(any(), any())).thenReturn(pageVariable);
		when(webService.getCurrentUrl(pageVariable)).thenReturn(urlValue);
		doThrow(new MalformedURLException()).when(webService).httpGet(pageVariable, urlValue);

		// run
		LoadPageConstruct.process(url, options, optionsFactory, webService);
	}

	/**
	 * Tests the process when we have a full PhantomJSpool.
	 *
	 * @throws Exception
	 */
	@Test(expectedExceptions = RobotRuntimeException.class, expectedExceptionsMessageRegExp = "Loadpage error - PhantomJS pool is fully used and cannot provide another instance!")
	public void testFullPhantomPool() throws Exception {
		// mock
		WebService webService = mock(WebService.class);
		OptionsFactory optionsFactory = mock(OptionsFactory.class);
		Options returnedOptions = mock(Options.class);

		// The url
		String urlValue = "This is an url";
		MetaExpression url = mock(MetaExpression.class);
		when(url.getStringValue()).thenReturn(urlValue);

		// The options
		MetaExpression options = mock(MetaExpression.class);

		// the process
		// There is currently no other way to do this; we have to sort out the functions one day.
		when(optionsFactory.processOptions(options)).thenReturn(returnedOptions);;
		PageVariable pageVariable = mock(PageVariable.class);
		when(webService.getPageFromPool(any(), any())).thenReturn(null);
		when(webService.getCurrentUrl(pageVariable)).thenReturn(urlValue);
		doThrow(new MalformedURLException()).when(webService).httpGet(pageVariable, urlValue);

		// run
		LoadPageConstruct.process(url, options, optionsFactory, webService);

	}

	/**
	 * Tests the process when a classCast exception is thrown.
	 * Note that this is not allowed to ever happen.
	 *
	 * @throws Exception
	 */
	@Test(expectedExceptions = RobotRuntimeException.class, expectedExceptionsMessageRegExp = "Failed to execute httpGet.")
	public void testClassCastException() throws Exception {

		// mock
		WebService webService = mock(WebService.class);
		OptionsFactory optionsFactory = mock(OptionsFactory.class);
		Options returnedOptions = mock(Options.class);

		// The url
		String urlValue = "This is an url";
		MetaExpression url = mock(MetaExpression.class);
		when(url.getStringValue()).thenReturn(urlValue);

		// The options
		MetaExpression options = mock(MetaExpression.class);

		// the process
		// There is currently no other way to do this; we have to sort out the functions one day.
		when(optionsFactory.processOptions(options)).thenReturn(returnedOptions);;
		PageVariable pageVariable = mock(PageVariable.class);
		when(webService.getPageFromPool(any(), any())).thenReturn(pageVariable);
		when(webService.getCurrentUrl(pageVariable)).thenReturn(urlValue);
		doThrow(new ClassCastException()).when(webService).httpGet(pageVariable, urlValue);

		// run
		MetaExpression output = LoadPageConstruct.process(url, options, optionsFactory, webService);

		// verify
		verify(optionsFactory, times(1)).processOptions(options);
		verify(webService, times(1)).getPageFromPool(any(), any());
		verify(webService, times(1)).getCurrentUrl(pageVariable);

		// assert
		Assert.assertEquals(output.getStringValue(), urlValue);
	}
}
