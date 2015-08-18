package nl.xillio.xill.plugins.excel.datastructures;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.testng.annotations.Test;

import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

/**
 * Created by Daan Knoope on 18-8-2015.
 */
public class XillSheetTest {

	@Test
	public void testGetColumnLength() throws Exception {
		Sheet sheet = mock(Sheet.class);
		when(sheet.getLastRowNum()).thenReturn(1);
		Row row1 = mock(Row.class);
		Row row2 = mock(Row.class);
		when(sheet.getRow(0)).thenReturn(row1);
		when(sheet.getRow(1)).thenReturn(row2);
		when(row1.getLastCellNum()).thenReturn((short)82);
		when(row2.getLastCellNum()).thenReturn((short) 81);
		XillSheet testSheet = new XillSheet(sheet, false);
		assertEquals(83, testSheet.getColumnLength());
	}

	@Test
	public void testGetRowLength() throws Exception {
		Sheet sheet = mock(Sheet.class);
		when(sheet.getLastRowNum()).thenReturn(3);
		XillSheet testSheet = new XillSheet(sheet, false);
		assertEquals(4, testSheet.getRowLength());
	}

	@Test
	public void testGetName() throws Exception {
		Sheet sheet = mock(Sheet.class);
		when(sheet.getSheetName()).thenReturn("sheet");
		XillSheet testsheet = new XillSheet(sheet,false);
		assertEquals("sheet", testsheet.getName());
	}

	@Test
	public void testGetCellValue() throws Exception {
		XillRow row = mock(XillRow.class);
		Sheet sheet = mock(Sheet.class);
		XillSheet xillSheet = spy(new XillSheet(sheet, false));
		doReturn(row).when(xillSheet).getRow(any(Integer.class));
		XillCell cell = mock(XillCell.class);
		when(cell.getValue()).thenReturn(true);
		when(row.getCell(any(Integer.class))).thenReturn(cell);
		assertTrue((boolean) xillSheet.getCellValue(new XillCellRef(1, 1)));
	}

	@Test
	public void testGetCellValueEmpty() throws Exception{
		Sheet sheet = mock(Sheet.class);
		XillSheet xillSheet = spy(new XillSheet(sheet, false));
		XillRow row = mock(XillRow.class);
		doReturn(row).when(xillSheet).getRow(any(Integer.class));
		when(row.isNull()).thenReturn(true);
		assertEquals("[EMPTY]", xillSheet.getCellValue(new XillCellRef(1, 1)));
	}

	@Test
	public void getCell() throws Exception {
		XillCellRef cellRef = new XillCellRef(3,3);
		XillSheet sheet = spy(new XillSheet(mock(Sheet.class), false));
		XillRow row = mock(XillRow.class);
		doReturn(row).when(sheet).getRow(4);
		when(row.isNull()).thenReturn(false);
		XillCell cell = mock(XillCell.class);
		when(row.getCell(4)).thenReturn(cell);
		when(cell.isNull()).thenReturn(false);
		assertTrue(sheet.getCell(cellRef).equals(cell));
	}

	@Test
	public void getCellDoesNotExist() throws Exception{
		XillCellRef cellRef = new XillCellRef(4,3);
		Sheet sheet = mock(Sheet.class);
		XillSheet testSheet = spy(new XillSheet(sheet, false));

		XillRow emptyRow = mock(XillRow.class);
		when(emptyRow.isNull()).thenReturn(true);

		XillRow newRow = mock(XillRow.class);
		XillCell cell = mock(XillCell.class);
		when(newRow.getCell(5)).thenReturn(cell);
		when(cell.isNull()).thenReturn(true);
		when(newRow.createCell(5)).thenReturn(cell);

		doReturn(emptyRow).when(testSheet).getRow(4);
		doReturn(newRow).when(testSheet).createRow(4);

		assertTrue(cell.equals(testSheet.getCell(cellRef)));
	}

	@Test
	public void testSetCellValue() throws Exception {
		XillCellRef cellRef = new XillCellRef(5,3);
		String value = "val";
		Sheet sheet = mock(Sheet.class);
		XillSheet testSheet = spy(new XillSheet(sheet, false));
		XillCell cell = mock(XillCell.class);
		doReturn(cell).when(testSheet).getCell(any(XillCellRef.class));

		testSheet.setCellValue(cellRef, value);
		testSheet.setCellValue(cellRef, 3d);
		testSheet.setCellValue(cellRef, false);
		verify(testSheet.getCell(cellRef), times(3));
	}

	@Test
	public void testIsReadonly() throws Exception {
		Sheet sheet = mock(Sheet.class);
		XillSheet testSheet = new XillSheet(sheet, true);
		assertTrue(testSheet.isReadonly());
		testSheet = new XillSheet(sheet, false);
		assertFalse(testSheet.isReadonly());
	}
}
