package nl.xillio.xill.plugins.excel.constructs;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.plugins.excel.dataStructures.XillWorkbook;

import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static nl.xillio.xill.api.construct.ExpressionBuilderHelper.fromValue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;

/**
 * Unit tests for the GetSheetNames construct
 *
 * @author Daan Knoope
 */
public class GetSheetNamesConstructTest {

	/**
	 * Creates a XillWorkbook on which the test operations will be performed
	 *
	 * @param sheetNames a list of names which the sheets have
	 * @return a mocked XillWorkbook
	 */
	private MetaExpression createWorkbookInput(List<String> sheetNames) {
		XillWorkbook workbook = mock(XillWorkbook.class);
		MetaExpression workbookInput = fromValue("workbook");
		workbookInput.storeMeta(XillWorkbook.class, workbook);
		when(workbook.getSheetNames()).thenReturn(sheetNames);
		return workbookInput;
	}

	/**
	 * Checks if an empty list is returned when no sheets exist
	 */
	@Test
	public void testProcessReturnsEmptyList() throws Exception {
		MetaExpression workbookInput = createWorkbookInput(new ArrayList<>());
		String result = GetSheetNamesConstruct.process(workbookInput).getStringValue();
		assertEquals(result, "[]");
	}

	/**
	 * Checks if a correct list is returned when there are sheets in the workbook
	 */
	@Test
	public void testProcessReturnsList() throws Exception {
		MetaExpression workbookInput = createWorkbookInput(Arrays.asList("Sheet1", "Sheet2"));
		String result = GetSheetNamesConstruct.process(workbookInput).getStringValue();
		assertEquals(result, "[\"Sheet1\",\"Sheet2\"]");
	}
}
