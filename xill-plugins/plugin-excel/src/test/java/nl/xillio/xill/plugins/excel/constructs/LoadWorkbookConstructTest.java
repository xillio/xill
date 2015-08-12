package nl.xillio.xill.plugins.excel.constructs;

import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;
import nl.xillio.xill.api.Xill;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.components.RobotID;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.excel.dataStructures.XillWorkbook;
import nl.xillio.xill.plugins.excel.services.ExcelService;
import nl.xillio.xill.plugins.excel.services.ExcelServiceImpl;
import nl.xillio.xill.services.inject.InjectorUtils;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.mockito.ArgumentCaptor;
import org.testng.annotations.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Field;
import java.text.ParseException;

import static nl.xillio.xill.api.construct.ExpressionBuilderHelper.fromValue;
import static org.mockito.Mockito.*;

import static org.testng.Assert.*;

/**
 * Created by Daan Knoope on 10-8-2015.
 */
public class LoadWorkbookConstructTest {


	@Test(expectedExceptions = RobotRuntimeException.class, expectedExceptionsMessageRegExp = "Path does not lead to an xls or xlsx Microsoft Excel file")
	public void testProcessIllegalArgumentException() throws Exception{
		InjectorUtils.getGlobalInjector();
		ExcelService service = mock(ExcelService.class);
		ConstructContext context = mock(ConstructContext.class);
		RobotID id = mock(RobotID.class);
		when(id.getPath()).thenReturn(new File("."));
		when(context.getRobotID()).thenReturn(id);
		when(service.loadWorkbook(any(File.class))).thenThrow(new IllegalArgumentException());
		LoadWorkbookConstruct.process(service,context,fromValue("."));
	}

	@Test(expectedExceptions = RobotRuntimeException.class, expectedExceptionsMessageRegExp = "File could not be read")
	public void testProcessIOException() throws Exception {
			InjectorUtils.getGlobalInjector();
			ExcelService service = mock(ExcelService.class);
			ConstructContext context = mock(ConstructContext.class);
			XillWorkbook workbook = mock(XillWorkbook.class);
			RobotID id = mock(RobotID.class);
			when(id.getPath()).thenReturn(new File("."));
			when(context.getRobotID()).thenReturn(id);
			when(service.loadWorkbook(any(File.class))).thenThrow(new IOException());
			LoadWorkbookConstruct.process(service, context, fromValue("."));
		}

		@Test(expectedExceptions = RobotRuntimeException.class, expectedExceptionsMessageRegExp = "There is no file at the given path")
		public void testProcessFileNotFound ()throws Exception {
			InjectorUtils.getGlobalInjector();
			ExcelService service = mock(ExcelService.class);
			ConstructContext context = mock(ConstructContext.class);
			RobotID id = mock(RobotID.class);
			when(id.getPath()).thenReturn(new File("."));
			when(context.getRobotID()).thenReturn(id);
			when(service.loadWorkbook(any(File.class))).thenThrow(new FileNotFoundException());
			LoadWorkbookConstruct.process(service, context, fromValue("."));
		}

		@Test(expectedExceptions = RobotRuntimeException.class, expectedExceptionsMessageRegExp = "File cannot be opened as Excel Workbook")
		public void testProcessParseException ()throws Exception {
			InjectorUtils.getGlobalInjector();
			ExcelService service = mock(ExcelService.class);
			ConstructContext context = mock(ConstructContext.class);
			XillWorkbook workbook = mock(XillWorkbook.class);
			RobotID id = mock(RobotID.class);
			when(id.getPath()).thenReturn(new File("."));
			when(context.getRobotID()).thenReturn(id);
			when(service.loadWorkbook(any(File.class))).thenThrow(new ParseException("blabla", 0));
			LoadWorkbookConstruct.process(service, context, fromValue("."));
		}

		@Test
		public void testProcessReadOnlyThrowsWarning ()throws Exception {
			InjectorUtils.getGlobalInjector();
			ExcelService service = mock(ExcelService.class);
			ConstructContext context = mock(ConstructContext.class);
			XillWorkbook workbook = mock(XillWorkbook.class);
			Logger logger = mock(Logger.class);
			RobotID id = mock(RobotID.class);
			when(id.getPath()).thenReturn(new File("."));
			when(service.loadWorkbook(any(File.class))).thenReturn(workbook);
			when(context.getRobotID()).thenReturn(id);
			when(workbook.isReadonly()).thenReturn(true);
			when(context.getRootLogger()).thenReturn(logger);
			LoadWorkbookConstruct.process(service, context, fromValue("."));
			ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
			verify(logger).warn(captor.capture());
			assertEquals(captor.getValue(), "Opened in read-only mode.");

		}

		@Test
		public void testProcessWriteAccessThrowsNoReadOnlyWarning ()throws Exception {
			InjectorUtils.getGlobalInjector();
			ExcelService service = mock(ExcelService.class);
			ConstructContext context = mock(ConstructContext.class);
			XillWorkbook workbook = mock(XillWorkbook.class);
			Logger logger = mock(Logger.class);
			RobotID id = mock(RobotID.class);
			when(id.getPath()).thenReturn(new File("."));
			when(service.loadWorkbook(any(File.class))).thenReturn(workbook);
			when(context.getRobotID()).thenReturn(id);
			when(workbook.isReadonly()).thenReturn(false);
			when(context.getRootLogger()).thenReturn(logger);
			LoadWorkbookConstruct.process(service, context, fromValue("."));
			verify(logger, never()).warn(anyString());

		}

		@Test
		public void testProcessResultContainsMeta ()throws Exception {
			InjectorUtils.getGlobalInjector();
			ExcelService service = mock(ExcelService.class);
			ConstructContext context = mock(ConstructContext.class);
			XillWorkbook workbook = mock(XillWorkbook.class);
			Logger logger = mock(Logger.class);
			RobotID id = mock(RobotID.class);
			when(id.getPath()).thenReturn(new File("."));
			when(service.loadWorkbook(any(File.class))).thenReturn(workbook);
			when(context.getRobotID()).thenReturn(id);
			MetaExpression expr = LoadWorkbookConstruct.process(service, context, fromValue("."));
			assertEquals(expr.getMeta(XillWorkbook.class), workbook);
		}

}
