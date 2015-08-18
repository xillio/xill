package nl.xillio.xill.plugins.excel.constructs;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.components.RobotID;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.excel.datastructures.XillWorkbook;
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
 * Created by Daan Knoope on 11-8-2015.
 */
public class CreateWorkbookConstructTest {

	@BeforeClass
	public void initializeInjector() {
		InjectorUtils.getGlobalInjector();
	}

	@Test(expectedExceptions = RobotRuntimeException.class, expectedExceptionsMessageRegExp = "File already exists: no new workbook has been created.")
	public void testProcessFileAlreadyExists() throws Exception {
		ExcelService service = mock(ExcelService.class);
		ConstructContext context = mock(ConstructContext.class);
		RobotID id = mock(RobotID.class);
		when(id.getPath()).thenReturn(new File("."));
		when(context.getRobotID()).thenReturn(id);
		when(service.createWorkbook(any(File.class))).thenThrow(new FileAlreadyExistsException("File already exists: no new workbook has been created."));
		CreateWorkbookConstruct.process(service, context, fromValue("."));
	}

	@Test(expectedExceptions = RobotRuntimeException.class, expectedExceptionsMessageRegExp = "Cannot write to the supplied path")
	public void testProcessCannotWriteToPath() throws Exception {
		ExcelService service = mock(ExcelService.class);
		ConstructContext context = mock(ConstructContext.class);
		RobotID id = mock(RobotID.class);
		when(id.getPath()).thenReturn(new File("."));
		when(context.getRobotID()).thenReturn(id);
		when(service.createWorkbook(any(File.class))).thenThrow(new IOException());
		CreateWorkbookConstruct.process(service, context, fromValue("."));
	}

	@Test
	public void testProcessContainsWorkbookInMeta() throws Exception {
		ExcelService service = mock(ExcelService.class);
		ConstructContext context = mock(ConstructContext.class);
		XillWorkbook workbook = mock(XillWorkbook.class);
		RobotID id = mock(RobotID.class);
		when(id.getPath()).thenReturn(new File("."));
		when(context.getRobotID()).thenReturn(id);
		when(service.createWorkbook(any(File.class))).thenReturn(workbook);
		when(workbook.getFileString()).thenReturn("string");
		MetaExpression returned = CreateWorkbookConstruct.process(service, context, fromValue("."));
		assertEquals(returned.getMeta(XillWorkbook.class), workbook);
	}
}
