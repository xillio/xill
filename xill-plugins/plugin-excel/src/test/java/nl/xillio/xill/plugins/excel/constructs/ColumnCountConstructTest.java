package nl.xillio.xill.plugins.excel.constructs;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.excel.datastructures.XillSheet;
import org.testng.annotations.Test;

import java.util.LinkedHashMap;

import static nl.xillio.xill.api.construct.ExpressionBuilderHelper.fromValue;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

/**
 * Unit tests for the columnCount construct.
 * @author Daan Knoope
 */
public class ColumnCountConstructTest {

	/**
	 * Tests if the result of the construct contains the right number
	 * @throws Exception
	 */
	@Test
	public void testProcess() throws Exception {
		XillSheet sheet = mock(XillSheet.class);
		when(sheet.getColumnLength()).thenReturn(11);
		LinkedHashMap<String, MetaExpression> sheetObject = new LinkedHashMap<>();
		MetaExpression sheetInput = fromValue(sheetObject);
		sheetInput.storeMeta(XillSheet.class, sheet);
		MetaExpression result = ColumnCountConstruct.process(sheetInput);
		assertEquals(11, result.getNumberValue().intValue());
	}

	/**
	 * Tests if a RobotRuntimeException is thrown when no valid sheet is supplied
	 * @throws Exception
	 */
	@Test(expectedExceptions = RobotRuntimeException.class, expectedExceptionsMessageRegExp = "Expected parameter 'sheet' to be a result of loadSheet or createSheet")
	public void testProcessThrowsException() throws Exception{
		LinkedHashMap<String, MetaExpression> sheetObject = new LinkedHashMap<>();
		MetaExpression sheetInput = fromValue(sheetObject);
		sheetInput.storeMeta(XillSheet.class, null);
		ColumnCountConstruct.process(fromValue(sheetObject));
	}
}
