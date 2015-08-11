package nl.xillio.xill.plugins.web.constructs;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.web.data.OptionsFactory;
import nl.xillio.xill.plugins.web.data.PageVariable;
import nl.xillio.xill.plugins.web.services.web.FileService;
import nl.xillio.xill.plugins.web.services.web.WebService;

import org.testng.annotations.Test;

/**
 * test the {@link StringToPageConstruct}.
 *
 */
public class StringToPageConstructTest {

	/**
	 * Test the process under normal circumstances.
	 *
	 * @throws IOException
	 */
	@Test
	public void testProcessNormalUsage() throws IOException {
		// mock
		WebService webService = mock(WebService.class);
		FileService fileService = mock(FileService.class);
		OptionsFactory optionsFactory = mock(OptionsFactory.class);

		// the content variable
		String contentValue = "This is the content";
		MetaExpression content = mock(MetaExpression.class);
		when(content.getStringValue()).thenReturn(contentValue);

		// the file variable
		File file = mock(File.class);

		// the process
		PageVariable pageVariable = mock(PageVariable.class);
		when(fileService.createTempFile(anyString(), anyString())).thenReturn(file);
		when(fileService.getAbsolutePath(file)).thenReturn("file");
		when(webService.getPageFromPool(any(), any())).thenReturn(pageVariable);

		// run
		StringToPageConstruct.process(content, optionsFactory, fileService, webService);

		// verify
		verify(fileService, times(1)).createTempFile(anyString(), anyString());
		verify(fileService, times(1)).getAbsolutePath(file);
		verify(webService, times(1)).getPageFromPool(any(), any());
	}

	/**
	 * Test the process when an IO exception is thrown
	 *
	 * @throws IOException
	 */
	@Test(expectedExceptions = RobotRuntimeException.class, expectedExceptionsMessageRegExp = "An IO error occurred.")
	public void testProcessIOException() throws IOException {
		// mock
		WebService webService = mock(WebService.class);
		FileService fileService = mock(FileService.class);
		OptionsFactory optionsFactory = mock(OptionsFactory.class);

		// the content variable
		String contentValue = "This is the content";
		MetaExpression content = mock(MetaExpression.class);
		when(content.getStringValue()).thenReturn(contentValue);

		// the process
		when(fileService.createTempFile(anyString(), anyString())).thenThrow(new IOException());

		// run
		StringToPageConstruct.process(content, optionsFactory, fileService, webService);
	}

	/**
	 * Test the process when another exception is thrown.
	 *
	 * @throws Exception
	 */
	@Test(expectedExceptions = RobotRuntimeException.class, expectedExceptionsMessageRegExp = "Failed to load the generated page.")
	public void testProcessExceptionThrown() throws Exception {
		// mock
		WebService webService = mock(WebService.class);
		FileService fileService = mock(FileService.class);
		OptionsFactory optionsFactory = mock(OptionsFactory.class);

		// the content variable
		String contentValue = "This is the content";
		MetaExpression content = mock(MetaExpression.class);
		when(content.getStringValue()).thenReturn(contentValue);

		// the process
		when(fileService.createTempFile(anyString(), anyString())).thenThrow(new RobotRuntimeException("error"));

		// run
		StringToPageConstruct.process(content, optionsFactory, fileService, webService);
	}
}
