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
import nl.xillio.xill.plugins.web.Options;
import nl.xillio.xill.plugins.web.OptionsFactory;
import nl.xillio.xill.plugins.web.PageVariable;
import nl.xillio.xill.plugins.web.services.web.WebService;

import org.testng.Assert;
import org.testng.annotations.Test;

public class LoadPageConstructTest extends ExpressionBuilderHelper {

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

	@Test(expectedExceptions = RobotRuntimeException.class, expectedExceptionsMessageRegExp = "Failed to convert LoadPage options.")
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
		when(optionsFactory.processOptions(options)).thenThrow(new RobotRuntimeException("Failed to parse the options"));
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
		MetaExpression output = LoadPageConstruct.process(url, options, optionsFactory, webService);

		// verify
		verify(optionsFactory, times(1)).processOptions(options);
		verify(webService, times(1)).getPageFromPool(any(), any());
		verify(webService, times(1)).getCurrentUrl(pageVariable);

		// assert
		Assert.assertEquals(output.getStringValue(), urlValue);
	}

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
