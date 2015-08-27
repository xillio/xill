package nl.xillio.xill.plugins.excel.constructs;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.components.RobotID;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.excel.datastructurez.XillWorkbook;
import nl.xillio.xill.plugins.excel.services.ExcelService;
import nl.xillio.xill.services.inject.InjectorUtils;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;

import static nl.xillio.xill.api.construct.ExpressionBuilderHelper.fromValue;
import static org.mockito.Mockito.*;
import static org.testng.Assert.assertEquals;

/**
 * Unit tests for the CreateWorkbook construct
 *
 * @author Daan Knoope
 */
public class CreateWorkbookConstructTest {

	@BeforeClass
	public void initializeInjector() {
		InjectorUtils.getGlobalInjector();
	}

	/**
	 * Checks if the construct throws a RobotRuntimeException when the file already exists
	 */
	@Test(expectedExceptions = RobotRuntimeException.class, expectedExceptionsMessageRegExp = "File already exists: no new workbook has been created.")
	public void testProcessFileAlreadyExists() throws Exception {

		//Basic vars
		ExcelService service = mock(ExcelService.class);
		ConstructContext context = mock(ConstructContext.class);

		//Mocking file
		RobotID id = mock(RobotID.class);
		when(id.getPath()).thenReturn(new File("."));
		when(context.getRobotID()).thenReturn(id);

		//Throw exception
		when(service.createWorkbook(any(File.class))).thenThrow(new FileAlreadyExistsException("File already exists: no new workbook has been created."));

		//Executing test
		CreateWorkbookConstruct.process(service, context, fromValue("."));
	}

	/**
	 * Checks if a RobotRuntimeException is thrown when there was a write error
	 */
	@Test(expectedExceptions = RobotRuntimeException.class, expectedExceptionsMessageRegExp = "Cannot write to the supplied path")
	public void testProcessCannotWriteToPath() throws Exception {

		//Basic vars
		ExcelService service = mock(ExcelService.class);
		ConstructContext context = mock(ConstructContext.class);

		//Mocking file
		RobotID id = mock(RobotID.class);
		when(id.getPath()).thenReturn(new File("."));
		when(context.getRobotID()).thenReturn(id);

		//Throw exception
		when(service.createWorkbook(any(File.class))).thenThrow(new IOException());

		//Executing test
		CreateWorkbookConstruct.process(service, context, fromValue("."));
	}

	/**
	 * Checks if the returned result contains a XillWorkbook in the MetaExpression
	 */
	@Test
	public void testProcessContainsWorkbookInMeta() throws Exception {

		//Basic vars
		ExcelService service = mock(ExcelService.class);
		ConstructContext context = mock(ConstructContext.class);
		XillWorkbook workbook = mock(XillWorkbook.class);

		//Mock filesystem
		RobotID id = mock(RobotID.class);
		when(id.getPath()).thenReturn(new File("."));
		when(context.getRobotID()).thenReturn(id);
		when(service.createWorkbook(any(File.class))).thenReturn(workbook);
		when(workbook.getFileString()).thenReturn("string");

		//Get result
		MetaExpression returned = CreateWorkbookConstruct.process(service, context, fromValue("."));

		//Checks result
		assertEquals(returned.getMeta(XillWorkbook.class), workbook);
	}
}
