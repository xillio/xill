package nl.xillio.xill.plugins.excel.services;

import nl.xillio.xill.plugins.excel.datastructures.XillSheet;
import nl.xillio.xill.plugins.excel.datastructures.XillWorkbook;
import nl.xillio.xill.plugins.excel.datastructures.XillWorkbookFactory;
import org.testng.annotations.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.testng.Assert.assertEquals;

/**
 * Created by Daan Knoope on 11-8-2015.
 */
public class ExcelServiceImplTest {

	private ExcelService createService(XillWorkbookFactory factory) {
		ExcelService service = new ExcelServiceImpl(factory);
		return service;
	}

	private File createFile(boolean exists, boolean correctExtension) throws IOException {
		File file = mock(File.class);
		when(file.exists()).thenReturn(exists);

		if (correctExtension)
			when(file.getCanonicalPath()).thenReturn("abc.xls");
		else
			when(file.getCanonicalPath()).thenReturn("xls.abc");

		return file;
	}

	private XillWorkbook createWorkbook(boolean isReadOnly) {
		XillWorkbook workbook = mock(XillWorkbook.class);
		when(workbook.isReadonly()).thenReturn(isReadOnly);
		return workbook;
	}

	@Test(expectedExceptions = FileNotFoundException.class,
					expectedExceptionsMessageRegExp = "There is no file at the given path")
	public void testLoadWorkbookNoFileExists() throws Exception {
		File file = createFile(false, true);
		createService(null).loadWorkbook(file);
	}

	@Test(expectedExceptions = IllegalArgumentException.class,
					expectedExceptionsMessageRegExp = "Path does not lead to an xls or xlsx Microsoft Excel file")
	public void testLoadWorkbookIncorrectExtension() throws Exception {
		File file = createFile(true, false);
		ExcelService service = createService(null);
		service.loadWorkbook(file);
	}

	@Test(expectedExceptions = FileAlreadyExistsException.class,
					expectedExceptionsMessageRegExp = "File already exists: no new workbook has been created.")
	public void testCreateWorkbookFileAlreadyExists() throws Exception {
		File file = createFile(true, true);
		ExcelService service = createService(null);
		service.createWorkbook(file);
	}

	@Test
	public void loadWorkbookReturnsWorkbook() throws Exception {
		File file = createFile(true, true);

		XillWorkbookFactory factory = mock(XillWorkbookFactory.class);
		XillWorkbook workbook = mock(XillWorkbook.class);
		when(factory.loadWorkbook(any(File.class))).thenReturn(workbook);

		ExcelService service = createService(factory);

		assertEquals(service.loadWorkbook(file), workbook);
	}

	@Test
	public void createWorkbookReturnsWorkbook() throws Exception {
		File file = createFile(false, true);

		XillWorkbookFactory factory = mock(XillWorkbookFactory.class);
		XillWorkbook workbook = mock(XillWorkbook.class);
		when(factory.createWorkbook(any(File.class))).thenReturn(workbook);

		ExcelService service = createService(factory);
		assertEquals(service.createWorkbook(file), workbook);
	}

	@Test(expectedExceptions = NullPointerException.class,
					expectedExceptionsMessageRegExp = "The provided workbook is invalid.")
	public void testCreateSheetWorkbookNull() throws Exception {
		ExcelService service = createService(null);
		service.createSheet(null, "");
	}

	@Test(expectedExceptions = IllegalArgumentException.class,
					expectedExceptionsMessageRegExp = "No name was supplied: sheet names must be at least one character long.")
	public void testCreateSheetNoName() throws Exception {
		ExcelService service = createService(null);
		service.createSheet(createWorkbook(false), "");
	}

	@Test(expectedExceptions = IllegalArgumentException.class,
					expectedExceptionsMessageRegExp = "No name was supplied: sheet names must be at least one character long.")
	public void testCreateSheetNameNull() throws Exception {
		ExcelService service = createService(null);
		service.createSheet(createWorkbook(false), null);
	}

	@Test(expectedExceptions = IllegalArgumentException.class,
					expectedExceptionsMessageRegExp = "Sheet name is too long: must be less than 32 characters.")
	public void testCreateSheetNameTooLong() throws Exception {
		ExcelService service = createService(null);
		service.createSheet(createWorkbook(false), "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"); //Exactly 32 a's
	}

	@Test(expectedExceptions = IllegalArgumentException.class,
					expectedExceptionsMessageRegExp = "Workbook is read-only")
	public void testCreateSheetReadOnly() throws Exception {
		ExcelService service = createService(null);
		service.createSheet(createWorkbook(true), "a");
	}

	@Test
	public void testCreateSheet() throws Exception {
		ExcelService service = createService(null);
		XillWorkbook workbook = createWorkbook(false);
		XillSheet sheet = mock(XillSheet.class);
		when(workbook.makeSheet(anyString())).thenReturn(sheet);
		assertEquals(sheet, service.createSheet(workbook, "bla"));
	}


	@Test
	public void testRemovesheet() throws Exception {
		XillWorkbook workbook = mock(XillWorkbook.class);
		ExcelServiceImpl service = new ExcelServiceImpl(null);
		when(workbook.isReadonly()).thenReturn(false);
		when(workbook.fileExists()).thenReturn(true);
		service.removeSheet(workbook, "name");
		verify(workbook, times(1)).removeSheet(anyString());
	}

	@Test
	public void testNotInWorkbook() throws Exception {
		ExcelServiceImpl service = new ExcelServiceImpl(null);

		List<String> existingSheetNames = Arrays.asList("foo", "bar");
		XillWorkbook workbook = mock(XillWorkbook.class);
		when(workbook.getSheetNames()).thenReturn(existingSheetNames);

		List<String> notInWorkbook = Arrays.asList("Oak", "Willow", "Maple");
		List<String> partiallyInWorkbook = Arrays.asList("foo", "Oak");
		List<String> singletonInWorbook = Arrays.asList("foo");
		List<String> exactlyInWorkbook = Arrays.asList("foo", "bar");

		assertEquals(service.notInWorkbook(notInWorkbook, workbook), "Sheet(s) [Oak,Willow,Maple] do not exist in the current workbook; they could not be deleted.");
		assertEquals(service.notInWorkbook(partiallyInWorkbook, workbook), "Sheet(s) [Oak] do not exist in the current workbook; they could not be deleted.");
		verify(workbook, times(1)).removeSheet("foo");
		assertEquals(service.notInWorkbook(singletonInWorbook, workbook), "");
		verify(workbook, times(2)).removeSheet("foo");
		assertEquals(service.notInWorkbook(exactlyInWorkbook, workbook), "");
		verify(workbook, times(3)).removeSheet("foo");
		verify(workbook, times(1)).removeSheet("bar");
		verify(workbook, times(4)).removeSheet(anyString());
	}

	@Test(expectedExceptions = IllegalArgumentException.class, expectedExceptionsMessageRegExp = ".* is read-only")
	public void removeSheetsReadonly() throws Exception {
		XillWorkbook workbook = mock(XillWorkbook.class);
		when(workbook.isReadonly()).thenReturn(true);
		ExcelServiceImpl service = new ExcelServiceImpl(null);
		service.removeSheets(workbook, null);
	}


	@Test(expectedExceptions = IllegalArgumentException.class, expectedExceptionsMessageRegExp =
					"Sheet\\(s\\) \\[Apple\\] do not exist in the current workbook\\; they could not be deleted\\.")
	public void removeSheetsInvalidNames() throws Exception {
		XillWorkbook workbook = mock(XillWorkbook.class);
		when(workbook.isReadonly()).thenReturn(false);
		when(workbook.fileExists()).thenReturn(true);
		when(workbook.getSheetNames()).thenReturn(Arrays.asList("foo", "Pear"));
		ExcelServiceImpl service = new ExcelServiceImpl(null);
		service.removeSheets(workbook, Arrays.asList("foo", "Apple"));
	}

	@Test
	public void removeSheets() throws Exception {
		XillWorkbook workbook = mock(XillWorkbook.class);
		when(workbook.isReadonly()).thenReturn(false);
		when(workbook.fileExists()).thenReturn(true);
		when(workbook.getSheetNames()).thenReturn(Arrays.asList("Pear", "Banana"));
		ExcelServiceImpl service = new ExcelServiceImpl(null);
		service.removeSheets(workbook, Arrays.asList("Pear", "Banana"));
	}

	@Test(expectedExceptions = IllegalArgumentException.class, expectedExceptionsMessageRegExp = "Cannot write to this file: read-only")
	public void saveReadOnly() throws Exception {
		XillWorkbook workbook = mock(XillWorkbook.class);
		ExcelService service = new ExcelServiceImpl(null);
		when(workbook.isReadonly()).thenReturn(true);
		service.save(mock(File.class), workbook);
	}

	@Test
	public void save() throws Exception {
		ExcelService service = new ExcelServiceImpl(null);
		XillWorkbook workbook = mock(XillWorkbook.class);
		when(workbook.isReadonly()).thenReturn(false);
		File file = mock(File.class);
		when(file.getCanonicalPath()).thenReturn("thispath");
		assertEquals(service.save(file, workbook), "Saved [thispath]");
		verify(workbook, times(1)).save(file);
	}

	@Test(expectedExceptions = IllegalArgumentException.class, expectedExceptionsMessageRegExp = "Cannot write to this file: read-only")
	public void saveOverrideReadOnly() throws Exception {
		ExcelService service = new ExcelServiceImpl(null);
		XillWorkbook workbook = mock(XillWorkbook.class);
		when(workbook.isReadonly()).thenReturn(true);
		service.save(workbook);
	}

	@Test
	public void saveOverride() throws Exception {
		ExcelService service = new ExcelServiceImpl(null);
		XillWorkbook workbook = mock(XillWorkbook.class);
		when(workbook.isReadonly()).thenReturn(false);
		when(workbook.getLocation()).thenReturn("location");
		assertEquals(service.save(workbook), "Saved [location]");
		verify(workbook, times(1)).save();
	}

}
