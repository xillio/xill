package nl.xillio.xill.plugins.excel.constructs;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.plugins.excel.dataStructures.XillCellRef;
import nl.xillio.xill.plugins.excel.dataStructures.XillSheet;
import nl.xillio.xill.services.inject.InjectorUtils;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static nl.xillio.xill.api.construct.ExpressionBuilderHelper.fromValue;
import static org.mockito.Mockito.*;
import static org.testng.Assert.assertEquals;

/**
 * Created by Daan Knoope on 13-8-2015.
 */
public class GetCellConstructTest {

	@BeforeClass
	public void initializeInjector(){
		InjectorUtils.getGlobalInjector();
	}

	private MetaExpression createSheetInput(String cellContent){
		XillSheet sheet = mock(XillSheet.class);
		MetaExpression sheetInput = fromValue("Sheet");
		sheetInput.storeMeta(XillSheet.class, sheet);
		String column;
		when(sheet.getCellValue(any(XillCellRef.class))).thenReturn(cellContent);
		return sheetInput;
	}


	@Test
	public void testProcessReturnsNull() throws Exception {
		MetaExpression sheetInput = createSheetInput(null);
		MetaExpression result = GetCellConstruct.process(sheetInput, fromValue(1), fromValue(1));
		assertEquals(false, result.getBooleanValue());
	}

	@Test
	public void testProcessReturnsValueNumericNotation() throws Exception{
		MetaExpression sheetInput = createSheetInput("INFO");
		MetaExpression result = GetCellConstruct.process(sheetInput, fromValue(14), fromValue(19));
		assertEquals(true, result.getBooleanValue());
	}

	@Test
	public void testProcessReturnsValueAlphabeticNotation() throws Exception{
		MetaExpression sheetInput = createSheetInput("INFO");
		MetaExpression result = GetCellConstruct.process(sheetInput, fromValue("AB"), fromValue(19));
		assertEquals(true, result.getBooleanValue());
	}

}
