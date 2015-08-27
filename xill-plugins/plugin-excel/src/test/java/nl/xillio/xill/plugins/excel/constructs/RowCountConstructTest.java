package nl.xillio.xill.plugins.excel.constructs;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.excel.dataStructures.XillSheet;

import org.testng.annotations.Test;

import java.util.LinkedHashMap;

import static nl.xillio.xill.api.construct.ExpressionBuilderHelper.fromValue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;

/**
 * Unit tests for the RowCountConstruct.
 *
 * @author Daan Knoope
 */
public class RowCountConstructTest {

	/**
	 * Tests if the row count construct returns the right number
	 *
	 * @throws Exception
	 */
	@Test
	public void testProcess() throws Exception {
		XillSheet sheet = mock(XillSheet.class);
		when(sheet.getRowLength()).thenReturn(11);
		LinkedHashMap<String, MetaExpression> sheetObject = new LinkedHashMap<>();
		MetaExpression sheetInput = fromValue(sheetObject);
		sheetInput.storeMeta(XillSheet.class, sheet);
		MetaExpression result = RowCountConstruct.process(sheetInput);
		assertEquals(11, result.getNumberValue().intValue());
	}

	/**
	 * Tests if a RobotRuntimeException is thrown when no valid sheet is supplied
	 *
	 * @throws Exception
	 */
	@Test(expectedExceptions = RobotRuntimeException.class, expectedExceptionsMessageRegExp = "Expected parameter 'sheet' to be a result of loadSheet or createSheet")
	public void testProcessThrowsException() throws Exception {
		LinkedHashMap<String, MetaExpression> sheetObject = new LinkedHashMap<>();
		MetaExpression sheetInput = fromValue(sheetObject);
		sheetInput.storeMeta(XillSheet.class, null);
		RowCountConstruct.process(fromValue(sheetObject));
	}
}
