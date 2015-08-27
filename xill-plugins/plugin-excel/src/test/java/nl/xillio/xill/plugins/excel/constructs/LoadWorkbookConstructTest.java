package nl.xillio.xill.plugins.excel.constructs;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.components.RobotID;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.excel.datastructurez.XillWorkbook;
import nl.xillio.xill.plugins.excel.services.ExcelService;
import nl.xillio.xill.services.inject.InjectorUtils;
import org.apache.logging.log4j.Logger;
import org.mockito.ArgumentCaptor;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InvalidObjectException;

import static nl.xillio.xill.api.construct.ExpressionBuilderHelper.fromValue;
import static org.mockito.Mockito.*;
import static org.testng.Assert.assertEquals;

/**
 * Unit tests for the LoadWorkbook construct
 *
 * @author Daan Knoope
 */
public class LoadWorkbookConstructTest {

	@BeforeClass
	public void initializeInjector() {
		InjectorUtils.getGlobalInjector();
	}

	/**
	 * Mocks the RobotID
	 *
	 * @return a mocked RobotID
	 */
	private RobotID createRobotID() {
		RobotID id = mock(RobotID.class);
		File file = new File(".");
		when(id.getPath()).thenReturn(file);
		return id;
	}

	/**
	 * Checks if correct RobotRuntimeException is thrown when an incorrect path is provided
	 */
	@Test(expectedExceptions = RobotRuntimeException.class, expectedExceptionsMessageRegExp = "Path does not lead to an xls or xlsx Microsoft Excel file")
	public void testProcessIllegalArgumentException() throws Exception {
		//Basic vars
		ExcelService service = mock(ExcelService.class);
		ConstructContext context = mock(ConstructContext.class);

		//Mock RobotID
		doReturn(createRobotID()).when(context).getRobotID();

		//Throw exception
		when(service.loadWorkbook(any(File.class))).thenThrow(new IllegalArgumentException());

		//Execute test
		LoadWorkbookConstruct.process(service, context, fromValue("."));
	}

	/**
	 * Checks if correct RobotRuntimeException is thrown when file could not be opened
	 */
	@Test(expectedExceptions = RobotRuntimeException.class, expectedExceptionsMessageRegExp = "File could not be opened")
	public void testProcessIOException() throws Exception {
		//Basic vars
		ExcelService service = mock(ExcelService.class);
		ConstructContext context = mock(ConstructContext.class);

		//Mock filesystem
		doReturn(createRobotID()).when(context).getRobotID();

		//Throw exception
		when(service.loadWorkbook(any(File.class))).thenThrow(new IOException());

		//Execute test
		LoadWorkbookConstruct.process(service, context, fromValue("."));
	}

	/**
	 * Unit test to check if a RobotRuntimeException is thrown when no file can be found at the provided path
	 */
	@Test(expectedExceptions = RobotRuntimeException.class, expectedExceptionsMessageRegExp = "There is no file at the given path")
	public void testProcessFileNotFound() throws Exception {
		//Basic vars
		ExcelService service = mock(ExcelService.class);
		ConstructContext context = mock(ConstructContext.class);

		//Create RobotID
		RobotID id = createRobotID();
		when(context.getRobotID()).thenReturn(id);

		//Throw exception
		when(service.loadWorkbook(any(File.class))).thenThrow(new FileNotFoundException());

		//Execute test
		LoadWorkbookConstruct.process(service, context, fromValue("."));
	}

	/**
	 * Checks if a RobotRuntimeException is thrown when the file cannot be opened as an Excel workbook
	 *
	 * @throws Exception
	 */
	@Test(expectedExceptions = RobotRuntimeException.class, expectedExceptionsMessageRegExp = "File cannot be opened as Excel Workbook")
	public void testProcessInvalidObjectException() throws Exception {
		//Basic vars
		ExcelService service = mock(ExcelService.class);
		ConstructContext context = mock(ConstructContext.class);
		doReturn(createRobotID()).when(context).getRobotID();

		//Throw exception
		when(service.loadWorkbook(any(File.class))).thenThrow(new InvalidObjectException("File cannot be opened as Excel Workbook"));

		//Execute test
		LoadWorkbookConstruct.process(service, context, fromValue("."));
	}

	/**
	 * Checks if a warning is written to the logger when a read-only file has been opened
	 */
	@Test
	public void testProcessReadOnlyThrowsWarning() throws Exception {

		//Basic vars
		ExcelService service = mock(ExcelService.class);
		ConstructContext context = mock(ConstructContext.class);
		XillWorkbook workbook = mock(XillWorkbook.class);

		//Mock workbook
		when(service.loadWorkbook(any(File.class))).thenReturn(workbook);
		when(workbook.isReadonly()).thenReturn(true);

		//Create robot logger
		Logger logger = mock(Logger.class);
		when(context.getRootLogger()).thenReturn(logger);

		//mock robot id
		doReturn(createRobotID()).when(context).getRobotID();

		//Executing test
		LoadWorkbookConstruct.process(service, context, fromValue("."));

		//Setting up argument captor
		ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
		verify(logger).warn(captor.capture());

		//Verifying
		assertEquals(captor.getValue(), "Opened in read-only mode.");

	}

	/**
	 * Checks if no warning is written to the logger when the loaded file is not read-only
	 */
	@Test
	public void testProcessWriteAccessThrowsNoReadOnlyWarning() throws Exception {

		//Basic vars
		ExcelService service = mock(ExcelService.class);
		ConstructContext context = mock(ConstructContext.class);
		XillWorkbook workbook = mock(XillWorkbook.class);

		//Mock xill logger
		Logger logger = mock(Logger.class);
		when(context.getRootLogger()).thenReturn(logger);

		//Mock workbook
		when(service.loadWorkbook(any(File.class))).thenReturn(workbook);
		when(workbook.isReadonly()).thenReturn(false);

		//Mock robot id
		doReturn(createRobotID()).when(context).getRobotID();

		//Execute
		LoadWorkbookConstruct.process(service, context, fromValue("."));

		//Verify
		verify(logger, never()).warn(anyString());
	}

	/**
	 * Unit test to check if the return value of the construct is correct and contains a {@link XillWorkbook} in the
	 * returned MetaExpression
	 */
	@Test
	public void testProcessResultContainsMeta() throws Exception {

		//Basic vars
		ExcelService service = mock(ExcelService.class);
		ConstructContext context = mock(ConstructContext.class);
		XillWorkbook workbook = mock(XillWorkbook.class);
		when(service.loadWorkbook(any(File.class))).thenReturn(workbook);

		//Mock RobotID
		doReturn(createRobotID()).when(context).getRobotID();

		//Get the result
		MetaExpression result = LoadWorkbookConstruct.process(service, context, fromValue("."));

		//Verify
		assertEquals(result.getMeta(XillWorkbook.class), workbook);
	}

}
