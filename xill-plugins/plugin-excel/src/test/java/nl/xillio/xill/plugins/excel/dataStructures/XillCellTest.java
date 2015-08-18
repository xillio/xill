package nl.xillio.xill.plugins.excel.datastructures;

import nl.xillio.xill.api.errors.NotImplementedException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.RichTextString;
import org.testng.annotations.Test;

import java.util.Date;

import static org.mockito.Mockito.*;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * Created by Daan Knoope on 17-8-2015.
 */
public class XillCellTest {

	@Test(expectedExceptions = NotImplementedException.class, expectedExceptionsMessageRegExp
					= "A cell format that has been used in the Excel file is currently unsupported.")
	public void testGetValue() throws Exception {
		Cell cell = mock(Cell.class);
		XillCell testCell = spy(new XillCell(cell));

		when(cell.getCellType()).thenReturn(cell.CELL_TYPE_STRING);
		RichTextString rich = mock(RichTextString.class);
		when(cell.getRichStringCellValue()).thenReturn(rich);
		when(rich.getString()).thenReturn("string");
		assertEquals(testCell.getValue(), "string");

		when(cell.getCellType()).thenReturn(cell.CELL_TYPE_BOOLEAN);
		when(cell.getBooleanCellValue()).thenReturn(true);
		assertTrue((boolean) testCell.getValue());

		when(cell.getCellType()).thenReturn(cell.CELL_TYPE_FORMULA);
		when(cell.getCellFormula()).thenReturn("=3+3");
		assertEquals(testCell.getValue(), "=3+3");

		when(cell.getCellType()).thenReturn(cell.CELL_TYPE_NUMERIC);
		doReturn(true).when(testCell).isDateFormatted();
		Date d = new Date();
		when(cell.getDateCellValue()).thenReturn(d);
		assertEquals(testCell.getValue(), d);

		doReturn(false).when(testCell).isDateFormatted();
		Double num = 2d;
		when(cell.getNumericCellValue()).thenReturn(num);
		assertEquals(testCell.getValue(), num);

		doReturn(true).when(testCell).isNull();
		assertEquals(testCell.getValue(), "[EMPTY]");

		doReturn(false).when(testCell).isNull();
		when(cell.getCellType()).thenReturn(Cell.CELL_TYPE_BLANK);
		testCell.getValue();
	}

	@Test
	public void setCellValue() throws Exception {
		Cell cell = mock(Cell.class);
		XillCell testCell = new XillCell(cell);
		testCell.setCellValue("=1");
		verify(cell, times(1)).setCellFormula("=1");
		testCell.setCellValue("a");
		verify(cell, times(1)).setCellValue("a");
		testCell.setCellValue(2d);
		verify(cell, times(1)).setCellValue(2.0);
		testCell.setCellValue(true);
		verify(cell, times(1)).setCellValue(true);
	}

}
