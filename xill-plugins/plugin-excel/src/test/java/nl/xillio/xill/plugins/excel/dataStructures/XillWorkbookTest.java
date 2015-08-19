package nl.xillio.xill.plugins.excel.datastructures;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.testng.annotations.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * Created by Daan Knoope on 18-8-2015.
 */
public class XillWorkbookTest {

	public File createFile(String path, boolean readonly) throws Exception {
		File file = mock(File.class);
		when(file.getCanonicalPath()).thenReturn(path);
		when(file.canWrite()).thenReturn(!readonly);
		return file;
	}

	@Test(expectedExceptions = IllegalArgumentException.class,
					expectedExceptionsMessageRegExp = "Sheet cannot be found in the supplied workbook")
	public void testGetSheetThatDoesNotExist() throws Exception {
		Workbook workbook = mock(Workbook.class);
		File file = createFile("path", false);
		when(workbook.getSheetIndex(anyString())).thenReturn(-1);
		XillWorkbook testWorkbook = new XillWorkbook(workbook, file);
		testWorkbook.getSheet("sheet");
	}

	@Test
	public void testGetSheet() throws Exception {
		Workbook workbook = mock(Workbook.class);
		File file = createFile("path", false);
		XillWorkbook testWorkbook = new XillWorkbook(workbook, file);
		when(workbook.getSheetIndex(anyString())).thenReturn(2);
		Sheet sheet = mock(Sheet.class);
		when(workbook.getSheet("sheet")).thenReturn(sheet);
		when(sheet.getSheetName()).thenReturn("sheet");
		assertEquals("sheet", testWorkbook.getSheet("sheet").getName());
		assertEquals("Excel Workbook [path]", testWorkbook.getFileString());
	}

	@Test
	public void testMakeSheet() throws Exception {
		Workbook workbook = mock(Workbook.class);
		File file = createFile("path", false);
		XillWorkbook testWorkbook = new XillWorkbook(workbook, file);
		Sheet sheet = mock(Sheet.class);
		when(workbook.createSheet("name")).thenReturn(sheet);
		when(sheet.getSheetName()).thenReturn("name");
		XillSheet result = testWorkbook.makeSheet("name");
		verify(workbook, times(1)).createSheet("name");
		assertEquals(result.getName(), "name");
	}

	@Test
	public void testGetSheetNamesEmpty() throws Exception {
		Workbook workbook = mock(Workbook.class);
		File file = createFile("path", false);
		when(workbook.getNumberOfSheets()).thenReturn(0);
		XillWorkbook testWorkbook = new XillWorkbook(workbook, file);
		assertTrue(testWorkbook.getSheetNames().isEmpty());
	}

	@Test
	public void testGetSheetNames() throws Exception {
		List<String> sheetNames = Arrays.asList("Apple", "Bee", "Cow");

		Workbook workbook = mock(Workbook.class);
		File file = createFile("path", false);
		when(workbook.getNumberOfSheets()).thenReturn(sheetNames.size());

		Sheet[] sheets = new Sheet[3];
		for (int i = 0; i < sheets.length; i++) {
			sheets[i] = mock(Sheet.class);
			when(sheets[i].getSheetName()).thenReturn(sheetNames.get(i));
			when(workbook.getSheetAt(i)).thenReturn(sheets[i]);
		}
		XillWorkbook testWorkbook = new XillWorkbook(workbook, file);
		assertTrue(testWorkbook.getSheetNames().equals(sheetNames));
	}

	@Test
	public void testIsReadonly() throws Exception {
		Workbook workbook = mock(Workbook.class);
		File file = createFile("path", true);
		XillWorkbook testWorkbook = new XillWorkbook(workbook, file);
		assertTrue(testWorkbook.isReadonly());
	}

	@Test
	public void testGetLocation() throws Exception {
		Workbook workbook = mock(Workbook.class);
		File file = createFile("path", false);
		XillWorkbook testWorkbook = new XillWorkbook(workbook, file);
		assertEquals("path", testWorkbook.getLocation());
	}

	@Test
	public void testRemoveSheet() throws Exception {
		Workbook workbook = mock(Workbook.class);
		File file = createFile("path", false);
		XillWorkbook testWorkbook = new XillWorkbook(workbook, file);
		when(workbook.getSheetIndex("sheet")).thenReturn(3);
		assertTrue(testWorkbook.removeSheet("sheet"));
		verify(workbook, times(1)).removeSheetAt(3);
	}

	@Test(expectedExceptions = IllegalArgumentException.class, expectedExceptionsMessageRegExp = "Sheet sheet does not exist in this workbook")
	public void testRemoveSheetDoesNotExist() throws Exception {
		Workbook workbook = mock(Workbook.class);
		File file = createFile("path", false);
		XillWorkbook testWorkbook = new XillWorkbook(workbook, file);
		when(workbook.getSheetIndex(anyString())).thenReturn(-1);
		testWorkbook.removeSheet("sheet");
	}

	@Test
	public void testSave() throws Exception {
		Workbook workbook = mock(Workbook.class);
		File file = createFile("path", false);
		XillWorkbook testWorkbook = spy(new XillWorkbook(workbook, file));
		FileOutputStream stream = mock(FileOutputStream.class);
		doReturn(stream).when(testWorkbook).getOuputStream();
		doReturn(stream).when(testWorkbook).getOutputStream(file);
		testWorkbook.save();
		testWorkbook.save(file);
	}

	@Test(expectedExceptions = IOException.class,
					expectedExceptionsMessageRegExp = "Could not write to this file")
	public void testSaveException() throws Exception {
		Workbook workbook = mock(Workbook.class);
		File file = createFile("path", false);
		XillWorkbook testWorkbook = spy(new XillWorkbook(workbook, file));
		doThrow(new IOException()).when(workbook).write(any(OutputStream.class));
		testWorkbook.save();
	}

	@Test(expectedExceptions = IOException.class,
					expectedExceptionsMessageRegExp = "Could not write to this file")
	public void testSaveWithFileException() throws Exception {
		Workbook workbook = mock(Workbook.class);
		File file = createFile("path", false);
		XillWorkbook testWorkbook = spy(new XillWorkbook(workbook, file));
		FileOutputStream stream = mock(FileOutputStream.class);
		doReturn(stream).when(testWorkbook).getOutputStream(file);
		doThrow(new IOException()).when(workbook).write(any(OutputStream.class));
		testWorkbook.save(file);
	}
}
