package nl.xillio.xill.plugins.excel.constructs;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.excel.datastructurez.XillSheet;
import nl.xillio.xill.plugins.excel.datastructurez.XillWorkbook;
import nl.xillio.xill.plugins.excel.services.ExcelService;
import nl.xillio.xill.services.inject.InjectorUtils;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static nl.xillio.xill.api.construct.ExpressionBuilderHelper.fromValue;
import static org.mockito.Mockito.*;
import static org.testng.Assert.assertEquals;

/**
 * Unit tests for the CreateSheet Construct
 *
 * @author Daan Knoope
 */
public class CreateSheetConstructTest {

	@BeforeClass
	public void initializeInjector() {
		InjectorUtils.getGlobalInjector();
	}

	/**
	 * Checks if a new {@link RobotRuntimeException} is thrown when a null pointer has occurred while
	 * creating a new sheet.
	 */
	@Test(expectedExceptions = RobotRuntimeException.class, expectedExceptionsMessageRegExp = "Sheet name contains illegal characters: cannot contain 0x0000, 0x0003")
	public void testProcessNullPointerException() throws Exception {

		//Basic vars
		ExcelService service = mock(ExcelService.class);

		//Mocking workbook
		MetaExpression workbookInput = fromValue("workbook");
		XillWorkbook workbook = mock(XillWorkbook.class);
		workbookInput.storeMeta(XillWorkbook.class, workbook);

		//Throw exception
		when(service.createSheet(any(XillWorkbook.class), anyString())).thenThrow(new NullPointerException("Sheet name contains illegal characters: cannot contain 0x0000, 0x0003"));

		//Executing test
		CreateSheetConstruct.process(service, workbookInput, fromValue("naam"));
	}

	/**
	 * Checks if a new {@link RobotRuntimeException} is thrown when an
	 * {@link IllegalArgumentException} is raised during the creation of a sheet
	 */
	@Test(expectedExceptions = RobotRuntimeException.class, expectedExceptionsMessageRegExp = "^illegal$")
	public void testProcessIllegalArgumentException() throws Exception {
		//Basic vars
		ExcelService service = mock(ExcelService.class);
		;

		//Mocking workbook
		MetaExpression workbookInput = fromValue("workbook");
		XillWorkbook workbook = mock(XillWorkbook.class);
		workbookInput.storeMeta(XillWorkbook.class, workbook);

		//Throw Exception
		when(service.createSheet(workbook, "name")).thenThrow(new IllegalArgumentException("illegal"));

		//Executing method
		CreateSheetConstruct.process(service, workbookInput, fromValue("name"));
	}

	/**
	 * Checks if, after everything has completed successfully, a correct MetaExpression containing
	 * a XillSheet is returned.
	 */
	@Test
	public void testProcessReturnsSheetInMeta() throws Exception {

		//Instantiating basic vars
		ExcelService service = mock(ExcelService.class);
		MetaExpression workbookInput = fromValue("workbook");
		XillWorkbook workbook = mock(XillWorkbook.class);
		workbookInput.storeMeta(XillWorkbook.class, workbook);

		//Mocking the sheet object
		XillSheet sheet = mock(XillSheet.class);
		when(service.createSheet(workbook, "name")).thenReturn(sheet);
		when(sheet.getName()).thenReturn("name");
		when(sheet.getRowLength()).thenReturn(0);
		when(sheet.getColumnLength()).thenReturn(0);

		//Result
		MetaExpression result = CreateSheetConstruct.process(service, workbookInput, fromValue("name"));

		//Assertions
		assertEquals(result.getStringValue(), "{\"Name\":\"name\",\"Rows\":0,\"Columns\":0}");
		assertEquals(result.getMeta(XillSheet.class), sheet);
	}

}
