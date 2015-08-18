package nl.xillio.xill.plugins.excel.datastructures;

import nl.xillio.xill.api.errors.NotImplementedException;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.testng.annotations.Test;

import java.io.File;
import java.io.FileInputStream;

import static org.mockito.Mockito.*;

/**
 * Created by Daan Knoope on 18-8-2015.
 */
public class XillWorkbookFactoryTest {

	@Test(expectedExceptions = NotImplementedException.class,
					expectedExceptionsMessageRegExp = "This extension is not supported as Excel workbook\\.")
	public void testLoadWorkbook() throws Exception {
		File file = mock(File.class);
		when(file.getName()).thenReturn("file.xls");
		XillWorkbookFactory factory = spy(new XillWorkbookFactory());
		FileInputStream stream = mock(FileInputStream.class);
		doReturn(stream).when(factory).getStream(file);
		Workbook workbook = mock(Workbook.class);
		doReturn(workbook).when(factory).makeLegacyWorkbook(stream);
		factory.loadWorkbook(file);
		verify(factory, times(1)).makeLegacyWorkbook(stream);

		when(file.getName()).thenReturn("file.xlsx");
		workbook = mock(XSSFWorkbook.class);
		doReturn(workbook).when(factory).makeModernWorkbook(stream);
		factory.loadWorkbook(file);
		verify(factory, times(1)).makeModernWorkbook(stream);

		when(file.getName()).thenReturn("file.xlz");
		factory.loadWorkbook(file);
	}

}
