package nl.xillio.xill.plugins.excel.constructs;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.excel.datastructures.XillSheet;
import nl.xillio.xill.plugins.excel.datastructures.XillWorkbook;
import nl.xillio.xill.plugins.excel.services.ExcelService;
import nl.xillio.xill.services.inject.InjectorUtils;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static nl.xillio.xill.api.construct.ExpressionBuilderHelper.fromValue;
import static org.mockito.Mockito.*;
import static org.testng.Assert.assertEquals;

/**
 * Created by Daan Knoope on 12-8-2015.
 */
public class CreateSheetConstructTest {

	@BeforeClass
	public void initializeInjector() {
		InjectorUtils.getGlobalInjector();
	}

	@Test(expectedExceptions = RobotRuntimeException.class, expectedExceptionsMessageRegExp = "Sheet name contains illegal characters: cannot contain 0x0000, 0x0003")
	public void testProcessNullPointerException() throws Exception {
		ExcelService service = mock(ExcelService.class);
		MetaExpression workbookInput = fromValue("workbook");
		XillWorkbook workbook = mock(XillWorkbook.class);
		workbookInput.storeMeta(XillWorkbook.class, workbook);
		when(service.createSheet(any(XillWorkbook.class), anyString())).thenThrow(new NullPointerException("Sheet name contains illegal characters: cannot contain 0x0000, 0x0003"));
		CreateSheetConstruct.process(service, workbookInput, fromValue("naam"));
	}

	@Test(expectedExceptions = RobotRuntimeException.class)
	public void testProcessIllegalArgumentException2() throws Exception{
		ExcelService service = mock(ExcelService.class);
		MetaExpression input = fromValue("workbook");
		XillWorkbook workbook = mock(XillWorkbook.class);
		input.storeMeta(XillWorkbook.class, workbook);
		when(service.createSheet(any(XillWorkbook.class), anyString())).thenThrow(new IllegalArgumentException());
		CreateSheetConstruct.process(service,input,fromValue("name"));
	}

	@Test(expectedExceptions = RobotRuntimeException.class, expectedExceptionsMessageRegExp = "^illegal$")
	public void testProcessIllegalArgumentException() throws Exception {
		ExcelService service = mock(ExcelService.class);
		MetaExpression workbookInput = fromValue("workbook");
		XillWorkbook workbook = mock(XillWorkbook.class);
		workbookInput.storeMeta(XillWorkbook.class, workbook);
		when(service.createSheet(any(XillWorkbook.class), anyString())).thenThrow(new IllegalArgumentException("illegal"));
		CreateSheetConstruct.process(service, workbookInput, fromValue("naam"));
	}

	@Test
	public void testProcessReturnsSheetInMeta() throws Exception {
		ExcelService service = mock(ExcelService.class);
		MetaExpression workbookInput = fromValue("workbook");
		XillWorkbook workbook = mock(XillWorkbook.class);
		workbookInput.storeMeta(XillWorkbook.class, workbook);
		XillSheet sheet = mock(XillSheet.class);
		when(service.createSheet(any(XillWorkbook.class), anyString())).thenReturn(sheet);
		when(sheet.getName()).thenReturn("name");
		when(sheet.getRowLength()).thenReturn(0);
		when(sheet.getColumnLength()).thenReturn(0);
		MetaExpression result = CreateSheetConstruct.process(service, workbookInput, fromValue("name"));
		assertEquals(result.getStringValue(), "{\"Name\":\"name\",\"Rows\":0,\"Columns\":0}");
		assertEquals(result.getMeta(XillSheet.class), sheet);
	}

}
