package nl.xillio.xill.plugins.excel.constructs;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.plugins.excel.datastructures.XillWorkbook;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static nl.xillio.xill.api.construct.ExpressionBuilderHelper.fromValue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;

/**
 * Created by Daan Knoope on 13-8-2015.
 */
public class GetSheetNamesConstructTest {

	private MetaExpression createWorkbookInput(List<String> sheetNames) {
		XillWorkbook workbook = mock(XillWorkbook.class);
		MetaExpression workbookInput = fromValue("workbook");
		workbookInput.storeMeta(XillWorkbook.class, workbook);
		when(workbook.getSheetNames()).thenReturn(sheetNames);
		return workbookInput;
	}

	@Test
	public void testProcessReturnsEmptyList() throws Exception {
		MetaExpression workbookInput = createWorkbookInput(new ArrayList<>());
		String result = GetSheetNamesConstruct.process(workbookInput).getStringValue();
		assertEquals(result, "[]");
	}

	@Test
	public void testProcessReturnsList() throws Exception {
		MetaExpression workbookInput = createWorkbookInput(Arrays.asList("Sheet1", "Sheet2"));
		String result = GetSheetNamesConstruct.process(workbookInput).getStringValue();
		assertEquals(result, "[\"Sheet1\",\"Sheet2\"]");
	}
}
