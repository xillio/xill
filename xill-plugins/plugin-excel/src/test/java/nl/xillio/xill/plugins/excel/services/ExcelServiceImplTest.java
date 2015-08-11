package nl.xillio.xill.plugins.excel.services;

import nl.xillio.xill.api.Xill;
import nl.xillio.xill.plugins.excel.dataStructures.XillWorkbook;
import org.testng.annotations.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;

import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

/**
 * Created by Daan Knoope on 11-8-2015.
 */
public class ExcelServiceImplTest {

	@Test (expectedExceptions = FileNotFoundException.class, expectedExceptionsMessageRegExp = "There is no file at the given path")
	public void testLoadWorkbookNoFileExists() throws Exception {
		File file = mock(File.class);
		when(file.exists()).thenReturn(false);
		ExcelService service = new ExcelServiceImpl();
		service.loadWorkbook(file);
	}

	@Test(expectedExceptions = IllegalArgumentException.class, expectedExceptionsMessageRegExp = "Path does not lead to an xls or xlsx Microsoft Excel file")
	public void testLoadWorkbookIncorrectExtension() throws Exception{
		File file = mock(File.class);
		when(file.exists()).thenReturn(true); //Uninterested in this
		when(file.getCanonicalPath()).thenReturn("abc.xyz");
		ExcelService service = new ExcelServiceImpl();
		service.loadWorkbook(file);
	}

	@Test (expectedExceptions = FileAlreadyExistsException.class, expectedExceptionsMessageRegExp = "File already exists: no new workbook has been created.")
	public void testCreateWorkbookFileAlreadyExists() throws Exception {
		File file = mock(File.class);
		when(file.exists()).thenReturn(true);
		ExcelService service = new ExcelServiceImpl();
		service.createWorkbook(file);
	}
}
