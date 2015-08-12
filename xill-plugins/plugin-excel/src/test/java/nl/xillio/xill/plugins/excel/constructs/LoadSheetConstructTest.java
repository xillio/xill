package nl.xillio.xill.plugins.excel.constructs;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.excel.dataStructures.XillSheet;
import nl.xillio.xill.plugins.excel.dataStructures.XillWorkbook;
import nl.xillio.xill.plugins.excel.services.ExcelService;
import nl.xillio.xill.services.inject.InjectorUtils;
import org.apache.poi.ss.usermodel.Sheet;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static nl.xillio.xill.api.construct.ExpressionBuilderHelper.fromValue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

/**
 * Created by Daan Knoope on 11-8-2015.
 */
public class LoadSheetConstructTest {

	@BeforeClass
	public void initializeInjector(){
		InjectorUtils.getGlobalInjector();
	}

	@Test(expectedExceptions = RobotRuntimeException.class, expectedExceptionsMessageRegExp = "Expected parameter 'workbook' to be a result of loadWorkbook or createWorkbook")
	public void testProcessNoWorkbook() throws Exception {
		XillWorkbook workbook = null;//mock(XillWorkbook.class);
		MetaExpression input = fromValue("workbook object");
		input.storeMeta(XillWorkbook.class, workbook);
		ExcelService service = mock(ExcelService.class);
		LoadSheetConstruct.process(service, input, fromValue("Sheet"));
	}

	@Test(expectedExceptions = RobotRuntimeException.class, expectedExceptionsMessageRegExp = "Sheet can not be found in the supplied workbook")
	public void testProcessNoSheet() throws Exception{
		XillWorkbook workbook = mock(XillWorkbook.class);//mock(XillWorkbook.class);
		XillSheet sheet = null;
		when(workbook.getSheet(anyString())).thenReturn(sheet);
		MetaExpression input = fromValue("workbook object");
		input.storeMeta(XillWorkbook.class, workbook);
		ExcelService service = mock(ExcelService.class);
		LoadSheetConstruct.process(service, input, fromValue("Sheet"));
	}


	@Test
	public void testProcessReturnsCorrectly() throws Exception{
		XillWorkbook workbook = mock(XillWorkbook.class);//mock(XillWorkbook.class);
		XillSheet sheet = mock(XillSheet.class);
		when(workbook.getSheet(anyString())).thenReturn(sheet);
		when(sheet.getName()).thenReturn("SheetName");
		when(sheet.getRowLength()).thenReturn(3);
		when(sheet.getColumnLength()).thenReturn(5);
		MetaExpression input = fromValue("workbook object");
		input.storeMeta(XillWorkbook.class, workbook);
		ExcelService service = mock(ExcelService.class);
		MetaExpression result = LoadSheetConstruct.process(service, input, fromValue("Sheet"));
		assertEquals(result.getStringValue(), "{\"Sheet name\":\"SheetName\",\"Rows\":3,\"Columns\":5}");
		XillSheet resultSheet = result.getMeta(XillSheet.class);
		assertEquals(resultSheet, sheet);
	}

}
