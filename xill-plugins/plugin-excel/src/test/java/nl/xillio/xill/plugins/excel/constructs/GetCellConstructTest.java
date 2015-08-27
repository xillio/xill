package nl.xillio.xill.plugins.excel.constructs;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.plugins.excel.datastructurez.XillCellRef;
import nl.xillio.xill.plugins.excel.datastructurez.XillSheet;
import nl.xillio.xill.services.inject.InjectorUtils;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static nl.xillio.xill.api.construct.ExpressionBuilderHelper.fromValue;
import static org.mockito.Mockito.*;
import static org.testng.Assert.assertEquals;

/**
 * Unit tests for the GetCell construct
 *
 * @author Daan Knoope
 */
public class GetCellConstructTest {

	@BeforeClass
	public void initializeInjector() {
		InjectorUtils.getGlobalInjector();
	}

	/**
	 * Creates a mock for a XillSheet that will return the same for each cell in the sheet
	 *
	 * @param cellContent the string which every cell should return
	 * @return a {@link MetaExpression} containing the mocked {@link XillSheet}
	 */
	private MetaExpression createSheetInput(String cellContent) {
		XillSheet sheet = mock(XillSheet.class);
		MetaExpression sheetInput = fromValue("Sheet");
		sheetInput.storeMeta(XillSheet.class, sheet);
		when(sheet.getCellValue(any(XillCellRef.class))).thenReturn(cellContent);
		return sheetInput;
	}

	/**
	 * Checks if GetCell returns false when the cell required is null
	 */
	@Test
	public void testProcessReturnsNull() throws Exception {
		MetaExpression sheetInput = createSheetInput(null);
		MetaExpression result = GetCellConstruct.process(sheetInput, fromValue(1), fromValue(1));

		assertEquals(false, result.getBooleanValue());
	}

	/**
	 * Checks if GetCell reads numeric notation and returns the cell value
	 */
	@Test
	public void testProcessReturnsValueNumericNotation() throws Exception {
		MetaExpression sheetInput = createSheetInput("INFO");
		MetaExpression result = GetCellConstruct.process(sheetInput, fromValue(14), fromValue(19));

		assertEquals(true, result.getBooleanValue());
	}

	/**
	 * Checks if GetCell reads alphabetic notation and returns the cell value
	 */
	@Test
	public void testProcessReturnsValueAlphabeticNotation() throws Exception {
		MetaExpression sheetInput = createSheetInput("INFO");
		MetaExpression result = GetCellConstruct.process(sheetInput, fromValue("AB"), fromValue(19));
		assertEquals(true, result.getBooleanValue());
	}

}
