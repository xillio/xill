package nl.xillio.xill.plugins.web.constructs;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.ExpressionBuilderHelper;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.web.data.PageVariable;
import nl.xillio.xill.plugins.web.services.web.FileService;
import nl.xillio.xill.plugins.web.services.web.WebService;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Test the {@link ScreenShotConstruct}.
 */
public class ScreenshotConstructTest extends ExpressionBuilderHelper {

	/**
	 * test the process with normal usage.
	 *
	 * @throws IOException
	 */
	@Test
	public void testProcessNormalUsage() throws IOException {
		// mock
		WebService webService = mock(WebService.class);
		FileService fileService = mock(FileService.class);

		// The page
		PageVariable pageVariable = mock(PageVariable.class);
		MetaExpression page = mock(MetaExpression.class);
		when(page.getMeta(PageVariable.class)).thenReturn(pageVariable);

		// The name
		String nameValue = "Tony";
		MetaExpression name = mock(MetaExpression.class);
		when(name.getStringValue()).thenReturn(nameValue);

		// The files
		File srcFile = mock(File.class);
		File desFile = mock(File.class);

		// The process
		when(webService.getScreenshotAsFile(pageVariable)).thenReturn(srcFile);
		when(fileService.makeFile(nameValue)).thenReturn(desFile);

		// run
		MetaExpression output = ScreenshotConstruct.process(page, name, fileService, webService);

		// Wheter we parse the pageVariable only once
		verify(page, times(2)).getMeta(PageVariable.class);

		// We make one screenshot and store it once
		verify(webService, times(1)).getScreenshotAsFile(pageVariable);
		verify(fileService, times(1)).makeFile(nameValue);
		verify(fileService, times(1)).copyFile(srcFile, desFile);

		// assert
		Assert.assertEquals(output, NULL);
	}

	/**
	 * Test the process with null page given.
	 */
	@Test
	public void testNullInput() {
		// mock
		WebService webService = mock(WebService.class);
		FileService fileService = mock(FileService.class);
		MetaExpression input = mock(MetaExpression.class);
		MetaExpression fileName = mock(MetaExpression.class);
		when(input.isNull()).thenReturn(true);

		// run
		MetaExpression output = ScreenshotConstruct.process(input, fileName, fileService, webService);

		// assert
		Assert.assertEquals(output, NULL);
	}

	/**
	 * Test the process when the webService breaks
	 */
	@Test(expectedExceptions = RobotRuntimeException.class, expectedExceptionsMessageRegExp = "Failed to access page without errors.")
	public void testProcessWhenWebServiceBreaks() {
		// mock
		WebService webService = mock(WebService.class);
		FileService fileService = mock(FileService.class);

		// The page
		PageVariable pageVariable = mock(PageVariable.class);
		MetaExpression page = mock(MetaExpression.class);
		when(page.getMeta(PageVariable.class)).thenReturn(pageVariable);

		// The name
		String nameValue = "Tony";
		MetaExpression name = mock(MetaExpression.class);
		when(name.getStringValue()).thenReturn(nameValue);

		// The process
		when(webService.getScreenshotAsFile(pageVariable)).thenThrow(new RobotRuntimeException(""));

		// run
		ScreenshotConstruct.process(page, name, fileService, webService);
	}

	/**
	 * Test the process when the fileService breaks
	 *
	 * @throws IOException
	 */
	@Test(expectedExceptions = RobotRuntimeException.class, expectedExceptionsMessageRegExp = "Failed to copy to: Tony")
	public void testProcessWhenFileServiceBreaks() throws IOException {
		// mock
		WebService webService = mock(WebService.class);
		FileService fileService = mock(FileService.class);

		// The page
		PageVariable pageVariable = mock(PageVariable.class);
		MetaExpression page = mock(MetaExpression.class);
		when(page.getMeta(PageVariable.class)).thenReturn(pageVariable);

		// The name
		String nameValue = "Tony";
		MetaExpression name = mock(MetaExpression.class);
		when(name.getStringValue()).thenReturn(nameValue);

		// The files
		File srcFile = mock(File.class);
		File desFile = mock(File.class);

		// The process
		when(webService.getScreenshotAsFile(pageVariable)).thenReturn(srcFile);
		when(fileService.makeFile(nameValue)).thenReturn(desFile);
		doThrow(new IOException()).when(fileService).copyFile(srcFile, desFile);

		// run
		MetaExpression output = ScreenshotConstruct.process(page, name, fileService, webService);

		// Wheter we parse the pageVariable only once
		verify(page, times(2)).getMeta(PageVariable.class);

		// We make one screenshot and store it once
		verify(webService, times(1)).getScreenshotAsFile(pageVariable);
		verify(fileService, times(1)).makeFile(nameValue);
		verify(fileService, times(1)).copyFile(srcFile, desFile);

		// assert
		Assert.assertEquals(output, NULL);

	}

	/**
	 * Test the process when no page is given.
	 */
	@Test(expectedExceptions = RobotRuntimeException.class, expectedExceptionsMessageRegExp = "Invalid variable type. Node PAGE type expected!")
	public void testProcessNoPageGiven() {
		// mock
		WebService webService = mock(WebService.class);
		FileService fileService = mock(FileService.class);

		// The page
		MetaExpression page = mock(MetaExpression.class);
		when(page.getMeta(PageVariable.class)).thenReturn(null);

		// The name
		String nameValue = "Tony";
		MetaExpression name = mock(MetaExpression.class);
		when(name.getStringValue()).thenReturn(nameValue);

		// run
		ScreenshotConstruct.process(page, name, fileService, webService);
	}

	/**
	 * Test the process when no filename is given.
	 */
	@Test(expectedExceptions = RobotRuntimeException.class, expectedExceptionsMessageRegExp = "Invalid variable value. Filename is empty!")
	public void testProcessNoNameGiven() {
		// mock
		WebService webService = mock(WebService.class);
		FileService fileService = mock(FileService.class);

		// The name
		String nameValue = "";
		MetaExpression name = mock(MetaExpression.class);
		when(name.getStringValue()).thenReturn(nameValue);
		// The page
		MetaExpression page = mock(MetaExpression.class);

		// run
		ScreenshotConstruct.process(page, name, fileService, webService);
	}

}
