package nl.xillio.xill.plugins.excel.constructs;

import nl.xillio.xill.api.components.ExpressionDataType;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.excel.datastructures.XillWorkbook;
import nl.xillio.xill.plugins.excel.services.ExcelService;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;

import static nl.xillio.xill.api.construct.ExpressionBuilderHelper.fromValue;
import static org.mockito.Mockito.*;
import static org.testng.Assert.assertTrue;

/**
 * Created by Daan Knoope on 17-8-2015.
 */
public class RemoveSheetConstructTest {

	private MetaExpression createWorkbook() {
		MetaExpression workbookInput = fromValue("Workbook");
		XillWorkbook workbook = mock(XillWorkbook.class);
		workbookInput.storeMeta(XillWorkbook.class, workbook);
		return workbookInput;
	}

	@Test(expectedExceptions = RobotRuntimeException.class, expectedExceptionsMessageRegExp = "No valid \\(list of\\) sheetnames provided")
	public void testProcessThrowsExceptionAtObject() throws Exception {
		MetaExpression inputWorkbook = createWorkbook();
		MetaExpression sheetName = mock(MetaExpression.class);
		when(sheetName.getType()).thenReturn(ExpressionDataType.OBJECT);
		RemoveSheetConstruct.process(mock(ExcelService.class), inputWorkbook, sheetName);
	}

	@Test
	public void testProcessSucceedsWithSingle() throws Exception {
		ExcelService service = mock(ExcelService.class);
		MetaExpression inputWorkbook = createWorkbook();
		MetaExpression sheetName = mock(MetaExpression.class);
		when(sheetName.getType()).thenReturn(ExpressionDataType.ATOMIC);
		assertTrue(RemoveSheetConstruct.process(service, inputWorkbook, sheetName).getBooleanValue());
	}

	@Test
	public void testProcessSucceedsWithMultiple() throws Exception {
		ExcelService service = mock(ExcelService.class);
		MetaExpression inputWorkbook = createWorkbook();
		List<MetaExpression> inputSheets = Arrays.asList(fromValue("sheet1"), fromValue("sheet2"));
		assertTrue(RemoveSheetConstruct.process(service, inputWorkbook, fromValue(inputSheets)).getBooleanValue());
	}

	@Test
	public void testProcessSingleRemovesSheet() throws Exception {
		ExcelService service = mock(ExcelService.class);
		XillWorkbook workbook = mock(XillWorkbook.class);
		MetaExpression sheetName = fromValue("sheet");
		boolean result = RemoveSheetConstruct.processSingle(service, workbook, sheetName).getBooleanValue();

		verify(service, times(1)).removeSheet(workbook, "sheet");
		assertTrue(result);
	}

	@Test(expectedExceptions = RobotRuntimeException.class)
	public void testProcessSingleThrowsException() throws Exception {
		ExcelService service = mock(ExcelService.class);
		doThrow(new IllegalArgumentException()).when(service).removeSheet(any(XillWorkbook.class), anyString());
		RemoveSheetConstruct.processSingle(service, mock(XillWorkbook.class), fromValue("sheet"));
	}

	@Test(expectedExceptions = RobotRuntimeException.class)
	public void testProcessMultipleThrowsException() throws Exception {
		ExcelService service = mock(ExcelService.class);
		List<MetaExpression> inputSheets = Arrays.asList(fromValue("sheet1"), fromValue("sheet2"));
		doThrow(new IllegalArgumentException()).when(service).removeSheets(any(XillWorkbook.class), anyList());
		RemoveSheetConstruct.processMultiple(service, mock(XillWorkbook.class), fromValue(inputSheets));
	}

	@Test
	public void testProcessMultipleSucceeds() throws Exception {
		ExcelService service = mock(ExcelService.class);
		XillWorkbook workbook = mock(XillWorkbook.class);
		List<MetaExpression> inputSheets = Arrays.asList(fromValue("sheet1"), fromValue("sheet2"));
		assertTrue(RemoveSheetConstruct.processMultiple(service, workbook, fromValue(inputSheets)).getBooleanValue());

	}
}
