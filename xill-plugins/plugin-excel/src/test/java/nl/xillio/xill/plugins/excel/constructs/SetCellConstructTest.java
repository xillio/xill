package nl.xillio.xill.plugins.excel.constructs;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.excel.datastructures.XillCellRef;
import nl.xillio.xill.plugins.excel.datastructures.XillSheet;
import org.mockito.ArgumentCaptor;
import org.testng.annotations.Test;

import static nl.xillio.xill.api.construct.ExpressionBuilderHelper.fromValue;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

/**
 * Unit tests for the SetCell construct
 *
 * @author Daan Knoope
 */
public class SetCellConstructTest {

	/**
	 * Tests if the {@link SetCellConstruct#isNumeric(MetaExpression)} method returns false for non-numeric values.
	 */
	@Test
	public void testIsNumericFalse() throws Exception {
		String[] testValues = {"A", "AB", "AB2", "2AB", "A2B", "2222A"};
		for (String test : testValues)
			assertFalse(SetCellConstruct.isNumeric(fromValue(test)));
	}

	/**
	 * Tests if the {@link SetCellConstruct#isNumeric(MetaExpression)} method returns true for numeric values.
	 */
	@Test
	public void testIsNumericTrue() throws Exception {
		Double[] testValues = {11.4, 12d, 0d, 19d, 16.39, 612534.82, 1d, 10d, 12d, 2d};
		for (Double d : testValues)
			assertTrue(SetCellConstruct.isNumeric(fromValue(d)));
	}

	/**
	 * Tests if the {@link SetCellConstruct#isNumericXORAlphabetic(MetaExpression)} returns false for all values
	 * that are not either numeric or alphabetic (including alphanumeric).
	 */
	@Test
	public void testIsNumericXORAlphabeticFalse() throws Exception {
		String[] testValues = {"A2", "@", "34231R", "r3", "rR9", "0A", "0x0003", ":"};
		for (String test : testValues)
			assertFalse(SetCellConstruct.isNumericXORAlphabetic(fromValue(test)));
	}

	/**
	 * Tests if the {@link SetCellConstruct#isNumericXORAlphabetic(MetaExpression)} returns true for all values that
	 * are either numeric or alphabetic.
	 */
	@Test
	public void testIsNumericXORAlphabeticTrue() throws Exception {
		String[] testValues = {"A", "a", "3", "33", "Aa", "BAA", "38481", "0", "1", "ZA", "z"};
		for (String test : testValues)
			assertTrue(SetCellConstruct.isNumericXORAlphabetic(fromValue(test)));
	}

	/**
	 * Tests if {@link SetCellConstruct#setValue(XillSheet, XillCellRef, MetaExpression)} uses the numeric method
	 * when the input is numeric.
	 */
	@Test
	public void testSetValueNumeric() throws Exception {
		ArgumentCaptor<Double> captor = ArgumentCaptor.forClass(Double.class);
		XillSheet sheet = mock(XillSheet.class);
		XillCellRef cellRef = mock(XillCellRef.class);

		SetCellConstruct.setValue(sheet, cellRef, fromValue(3));

		verify(sheet, times(1)).setCellValue(any(XillCellRef.class), captor.capture());
		assertEquals(captor.getValue(), 3d);
	}

	/**
	 * Tests if {@link SetCellConstruct#setValue(XillSheet, XillCellRef, MetaExpression)} uses the boolean method
	 * when the input is boolean
	 */
	@Test
	public void testSetValueBool() throws Exception {
		ArgumentCaptor<Boolean> captor = ArgumentCaptor.forClass(Boolean.class);
		XillSheet sheet = mock(XillSheet.class);
		XillCellRef cellRef = mock(XillCellRef.class);

		SetCellConstruct.setValue(sheet, cellRef, fromValue(true));

		verify(sheet, times(1)).setCellValue(any(XillCellRef.class), captor.capture());
		assertTrue(captor.getValue());
	}

	/**
	 * Tests if {@link SetCellConstruct#setValue(XillSheet, XillCellRef, MetaExpression)} uses the String method when the input is
	 * a string value
	 */
	@Test
	public void testSetValueString() throws Exception {
		ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
		XillSheet sheet = mock(XillSheet.class);
		XillCellRef cellRef = mock(XillCellRef.class);

		SetCellConstruct.setValue(sheet, cellRef, fromValue("inputString"));

		verify(sheet, times(1)).setCellValue(any(XillCellRef.class), captor.capture());
		assertEquals(captor.getValue(), "inputString");
	}

	/**
	 * Tests if a numeric cell reference is correctly made.
	 */
	@Test
	public void testGetCellRefNumericColumn() throws Exception {
		XillCellRef cellRef = new XillCellRef(3, 4);
		assertTrue(cellRef.equals(SetCellConstruct.getCellRef(fromValue(3), fromValue(4))));
	}

	/**
	 * Tests if an alphabetic cell reference is correctly made.
	 */
	@Test
	public void testGetCellRefAlphabeticColumn() throws Exception {
		XillCellRef cellRef = new XillCellRef("A", 5);
		assertTrue(cellRef.equals(SetCellConstruct.getCellRef(fromValue("A"), fromValue(5))));
	}

	/**
	 * Tests if a RobotRuntimeException is thrown when the input MetaExpression does not contain a (valid) sheet
	 */
	@Test(expectedExceptions = RobotRuntimeException.class, expectedExceptionsMessageRegExp = "Expected parameter 'sheet' to be a result of loadSheet or createSheet")
	public void testProcessIncorrectSheet() throws Exception {
		MetaExpression sheetInput = fromValue("sheetinput");
		SetCellConstruct.process(sheetInput, fromValue("A"), fromValue("B"), fromValue("value"));
	}

	/**
	 * Tests if an exception is thrown when the input sheet is read-only.
	 *
	 * @throws Exception
	 */
	@Test(expectedExceptions = RobotRuntimeException.class, expectedExceptionsMessageRegExp = "Cannot write on sheet: sheet is read-only. First save as new file.")
	public void testProcessReadOnly() throws Exception {
		MetaExpression sheetInput = fromValue("sheetinput");
		XillSheet sheet = mock(XillSheet.class);
		sheetInput.storeMeta(sheet);

		when(sheet.isReadonly()).thenReturn(true);
		SetCellConstruct.process(sheetInput, fromValue("A"), fromValue("B"), fromValue("value"));
	}

	/**
	 * Tests if an exception is thrown when a wrong notation has been used for the row
	 */
	@Test(expectedExceptions = RobotRuntimeException.class, expectedExceptionsMessageRegExp = "Wrong notation for row \"A\", should be numeric \\(e\\.g\\. 12\\)")
	public void testProcessWrongRowNotation() throws Exception {
		MetaExpression sheetInput = fromValue("sheetinput");
		XillSheet sheet = mock(XillSheet.class);
		when(sheet.isReadonly()).thenReturn(false);
		sheetInput.storeMeta(sheet);
		SetCellConstruct.process(sheetInput, fromValue("AB"), fromValue("A"), fromValue("value"));
	}

	/**
	 * Tests if an exception is thrown for when a wrong notation has been used for the column
	 *
	 * @throws Exception
	 */
	@Test(expectedExceptions = RobotRuntimeException.class, expectedExceptionsMessageRegExp = "Wrong notation for column \"AB2\", should be numerical or alphabetical notation \\(e\\.g\\. AB or 12\\)")
	public void testProcessWrongColumnNotation() throws Exception {
		MetaExpression sheetInput = fromValue("sheetinput");
		XillSheet sheet = mock(XillSheet.class);
		when(sheet.isReadonly()).thenReturn(false);
		sheetInput.storeMeta(sheet);
		SetCellConstruct.process(sheetInput, fromValue("AB2"), fromValue(3), fromValue("value"));
	}

	/**
	 * Tests if the construct returns {@code true} when the cell has succesfully been set to the
	 * provided new value
	 */
	@Test
	public void testProcessReturnsCorrectly() throws Exception {
		MetaExpression sheetInput = fromValue("sheetInput");
		XillSheet sheet = mock(XillSheet.class);
		when(sheet.isReadonly()).thenReturn(false);
		sheetInput.storeMeta(sheet);
		assertTrue(SetCellConstruct.process(sheetInput, fromValue("AB"), fromValue(5), fromValue("value")).getBooleanValue());
	}

}
