package nl.xillio.xill.plugins.excel.datastructures;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.testng.annotations.Test;

import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

/**
 * Unit tests for the XillSheet data structure
 *
 * @author Daan Knoope
 */
public class XillSheetTest {

	/**
	 * Tests if the width of a sheet is correctly calculated
	 */
	@Test
	public void testGetColumnLength() throws Exception {
		Sheet sheet = mock(Sheet.class);
		when(sheet.getLastRowNum()).thenReturn(1);

		Row row1 = mock(Row.class);
		Row row2 = mock(Row.class);

		when(sheet.getRow(0)).thenReturn(row1);
		when(sheet.getRow(1)).thenReturn(row2);

		when(row1.getLastCellNum()).thenReturn((short) 82);
		when(row2.getLastCellNum()).thenReturn((short) 81);

		XillSheet testSheet = new XillSheet(sheet, false);
		assertEquals(82, testSheet.getColumnLength());
	}

	/**
	 * Tests if the correct number of rows is returned
	 */
	@Test
	public void testGetRowLength() throws Exception {
		Sheet sheet = mock(Sheet.class);

		when(sheet.getLastRowNum()).thenReturn(3);
		XillSheet testSheet = new XillSheet(sheet, false);

		assertEquals(4, testSheet.getRowLength());
	}

	/**
	 * Tests if the correct name of a sheet is returned
	 */
	@Test
	public void testGetName() throws Exception {
		Sheet sheet = mock(Sheet.class);
		when(sheet.getSheetName()).thenReturn("sheet");
		XillSheet testsheet = new XillSheet(sheet, false);
		assertEquals("sheet", testsheet.getName());
	}

	/**
	 * Tests that the GetCellValue method returns the correct value from a cell
	 */
	@Test
	public void testGetCellValue() throws Exception {
		XillRow row = mock(XillRow.class);
		Sheet sheet = mock(Sheet.class);
		XillSheet xillSheet = spy(new XillSheet(sheet, false));
		XillCell cell = mock(XillCell.class);

		doReturn(row).when(xillSheet).getRow(any(Integer.class));
		when(cell.getValue()).thenReturn(true);
		when(row.getCell(any(Integer.class))).thenReturn(cell);

		assertTrue((boolean) xillSheet.getCellValue(new XillCellRef(1, 1)));
	}

	/**
	 * Tests that "[EMPTY]" is returned when a row (where the cell should be in) is null
	 */
	@Test
	public void testGetCellValueEmpty() throws Exception {
		Sheet sheet = mock(Sheet.class);
		XillSheet xillSheet = spy(new XillSheet(sheet, false));
		XillRow row = mock(XillRow.class);

		doReturn(row).when(xillSheet).getRow(any(Integer.class));
		when(row.isNull()).thenReturn(true);

		assertEquals("[EMPTY]", xillSheet.getCellValue(new XillCellRef(1, 1)));
	}

	/**
	 * Tests that the correct value is returned when a cell is called by a XillCellRef
	 */
	@Test
	public void getCell() throws Exception {
		XillCellRef cellRef = new XillCellRef(3, 3);
		XillSheet sheet = spy(new XillSheet(mock(Sheet.class), false));
		XillRow row = mock(XillRow.class);

		doReturn(row).when(sheet).getRow(cellRef.getRow());
		when(row.isNull()).thenReturn(false);
		XillCell cell = mock(XillCell.class);
		when(row.getCell(cellRef.getRow())).thenReturn(cell);
		when(cell.isNull()).thenReturn(false);

		assertTrue(sheet.getCell(cellRef).equals(cell));
	}

	/**
	 * Tests that a new cell is created when it is required and does not exist yet
	 */
	@Test
	public void getCellDoesNotExist() throws Exception {
		XillCellRef cellRef = new XillCellRef(4, 3);
		Sheet sheet = mock(Sheet.class);
		XillSheet testSheet = spy(new XillSheet(sheet, false));

		XillRow emptyRow = mock(XillRow.class);
		when(emptyRow.isNull()).thenReturn(true);

		XillRow newRow = mock(XillRow.class);
		XillCell cell = mock(XillCell.class);
		when(newRow.getCell(cellRef.getColumn())).thenReturn(cell);
		when(cell.isNull()).thenReturn(true);
		when(newRow.createCell(cellRef.getColumn())).thenReturn(cell);

		doReturn(emptyRow).when(testSheet).getRow(cellRef.getRow());
		doReturn(newRow).when(testSheet).createRow(cellRef.getRow());

		assertTrue(cell.equals(testSheet.getCell(cellRef)));
	}

	/**
	 * Tests that a cell can be overwritten and that all three of the
	 * signatures work for SetCellValue
	 */
	@Test
	public void testSetCellValue() throws Exception {
		XillCellRef cellRef = new XillCellRef(5, 3);
		String value = "val";
		Sheet sheet = mock(Sheet.class);
		XillSheet testSheet = spy(new XillSheet(sheet, false));
		XillCell cell = mock(XillCell.class);
		doReturn(cell).when(testSheet).getCell(any(XillCellRef.class));

		testSheet.setCellValue(cellRef, value);
		testSheet.setCellValue(cellRef, 3d);
		testSheet.setCellValue(cellRef, false);

		verify(testSheet, times(3)).getCell(cellRef);
	}

	/**
	 * Tests that a sheet returns if it is read-only
	 */
	@Test
	public void testIsReadonly() throws Exception {
		Sheet sheet = mock(Sheet.class);
		XillSheet testSheet = new XillSheet(sheet, true);
		assertTrue(testSheet.isReadonly());
		testSheet = new XillSheet(sheet, false);
		assertFalse(testSheet.isReadonly());
	}
}
