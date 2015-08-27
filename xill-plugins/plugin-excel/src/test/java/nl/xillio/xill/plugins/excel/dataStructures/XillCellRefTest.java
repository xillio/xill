package nl.xillio.xill.plugins.excel.dataStructures;

import nl.xillio.xill.plugins.excel.dataStructures.XillCellRef;

import org.testng.annotations.Test;

/**
 * Unit tests for the XillCellRef data structure
 *
 * @author Daan Knoope
 */
public class XillCellRefTest {

	/**
	 * Throws IllegalArgumentException when column < 1
	 *
	 * @throws Exception
	 */
	@Test(expectedExceptions = IllegalArgumentException.class,
					expectedExceptionsMessageRegExp = "The row number must be one or higher \\(0 was used\\)")
	public void constructorTestInvalidRow() throws Exception {
		new XillCellRef(1, 0);
	}

	/**
	 * Throws IllegalArgumentExcepiton when row < 1
	 *
	 * @throws Exception
	 */
	@Test(expectedExceptions = IllegalArgumentException.class,
					expectedExceptionsMessageRegExp = "The column number must be one or higher \\(0 was used\\)")
	public void constructorTestInvalidColumn() throws Exception {
		new XillCellRef(0, 1);
	}

	/**
	 * Tests if constructor accepts correct values
	 *
	 * @throws Exception
	 */
	@Test
	public void constructorXillCellRef() throws Exception {
		new XillCellRef(1, 1);
		new XillCellRef("A", 1);
		new XillCellRef("ZA", 92);
	}
}
