package nl.xillio.xill.plugins.excel.services;

import nl.xillio.xill.plugins.excel.dataStructures.XillSheet;
import nl.xillio.xill.plugins.excel.dataStructures.XillWorkbook;
import nl.xillio.xill.plugins.excel.dataStructures.XillWorkbookFactory;
import org.testng.annotations.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;

import static org.mockito.Mockito.*;
import static org.testng.Assert.assertEquals;

/**
 * Created by Daan Knoope on 11-8-2015.
 */
public class ExcelServiceImplTest {

	private ExcelService createService(XillWorkbookFactory factory){
		ExcelService service = new ExcelServiceImpl(factory);
		return service;
	}

	private File createFile(boolean exists, boolean correctExtension) throws IOException {
		File file = mock(File.class);
		when(file.exists()).thenReturn(exists);

		if(correctExtension)
			when(file.getCanonicalPath()).thenReturn("abc.xls");
		else
			when(file.getCanonicalPath()).thenReturn("xls.abc");

		return file;
	}

	private XillWorkbook createWorkbook(boolean isReadOnly){
		XillWorkbook workbook = mock(XillWorkbook.class);
		when(workbook.isReadonly()).thenReturn(isReadOnly);
		return workbook;
	}

	@Test (expectedExceptions = FileNotFoundException.class,
					expectedExceptionsMessageRegExp = "There is no file at the given path")
	public void testLoadWorkbookNoFileExists() throws Exception {
		File file = createFile(false, true);
		createService(null).loadWorkbook(file);
	}

	@Test(expectedExceptions = IllegalArgumentException.class,
					expectedExceptionsMessageRegExp = "Path does not lead to an xls or xlsx Microsoft Excel file")
	public void testLoadWorkbookIncorrectExtension() throws Exception{
		File file = createFile(true,false);
		ExcelService service = createService(null);
		service.loadWorkbook(file);
	}

	@Test (expectedExceptions = FileAlreadyExistsException.class,
					expectedExceptionsMessageRegExp = "File already exists: no new workbook has been created.")
	public void testCreateWorkbookFileAlreadyExists() throws Exception {
		File file = createFile(true,true);
		ExcelService service = createService(null);
		service.createWorkbook(file);
	}

	@Test
	public void loadWorkbookReturnsWorkbook() throws Exception{
		File file = createFile(true,true);

		XillWorkbookFactory factory = mock(XillWorkbookFactory.class);
		XillWorkbook workbook = mock(XillWorkbook.class);
		when(factory.loadWorkbook(any(File.class))).thenReturn(workbook);

		ExcelService service = createService(factory);

		assertEquals(service.loadWorkbook(file), workbook);
	}

	@Test
	public void createWorkbookReturnsWorkbook() throws Exception{
		File file = createFile(false, true);

		XillWorkbookFactory factory = mock(XillWorkbookFactory.class);
		XillWorkbook workbook = mock(XillWorkbook.class);
		when(factory.createWorkbook(any(File.class))).thenReturn(workbook);

		ExcelService service = createService(factory);
		assertEquals(service.createWorkbook(file), workbook);
	}

	@Test (expectedExceptions = NullPointerException.class,
					expectedExceptionsMessageRegExp = "The provided workbook is invalid.")
	public void testCreateSheetWorkbookNull() throws Exception{
		ExcelService service = createService(null);
		service.createSheet(null, "");
	}

	@Test(expectedExceptions = IllegalArgumentException.class,
					expectedExceptionsMessageRegExp = "No name was supplied: sheet names must be at least one character long.")
	public void testCreateSheetNoName() throws Exception{
		ExcelService service = createService(null);
		service.createSheet(createWorkbook(false),"");
	}

	@Test(expectedExceptions = IllegalArgumentException.class,
					expectedExceptionsMessageRegExp = "No name was supplied: sheet names must be at least one character long.")
	public void testCreateSheetNameNull() throws Exception{
		ExcelService service = createService(null);
		service.createSheet(createWorkbook(false),null);
	}

	@Test(expectedExceptions = IllegalArgumentException.class,
					expectedExceptionsMessageRegExp = "Sheet name is too long: must be less than 32 characters.")
	public void testCreateSheetNameTooLong() throws Exception{
		ExcelService service = createService(null);
		service.createSheet(createWorkbook(false),"aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"); //Exactly 32 a's
	}

	@Test(expectedExceptions = IllegalArgumentException.class,
					expectedExceptionsMessageRegExp = "Workbook is read-only")
	public void testCreateSheetReadOnly() throws Exception{
		ExcelService service = createService(null);
		service.createSheet(createWorkbook(true),"a");
	}

	@Test(expectedExceptions = IllegalArgumentException.class, expectedExceptionsMessageRegExp = "Sheet name contains illegal characters: cannot contain 0x0000, 0x0003, \\\\, \\*, \\?, \\/, \\[, \\] and start or end with a single quote.")
	public void testCreateSheetIllegalName() throws Exception{
		ExcelService service = createService(null);
		service.createSheet(createWorkbook(false),"'bla");
	}


	@Test(expectedExceptions = IllegalArgumentException.class, expectedExceptionsMessageRegExp = "There already exists a sheet with that name in the provided workbook")
	public void testCreateSheetSameName() throws Exception{
		ExcelService service = createService(null);
		XillWorkbook workbook = createWorkbook(false);
		when(workbook.makeSheet(anyString())).thenThrow(new IllegalArgumentException());
		service.createSheet(workbook,"bla");
	}

	@Test
	public void testCreateSheet() throws Exception{
		ExcelService service = createService(null);
		XillWorkbook workbook = createWorkbook(false);
		XillSheet sheet = mock(XillSheet.class);
		when(workbook.makeSheet(anyString())).thenReturn(sheet);
		assertEquals(sheet, service.createSheet(workbook, "bla"));
	}

}
