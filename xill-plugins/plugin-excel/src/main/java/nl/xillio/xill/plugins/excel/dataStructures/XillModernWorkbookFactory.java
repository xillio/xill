package nl.xillio.xill.plugins.excel.dataStructures;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class XillModernWorkbookFactory extends XillWorkbookFactory{

	@Override public XillWorkbook createWorkbook(File file) throws IOException {
		XSSFWorkbook workbook = new XSSFWorkbook();
		return new ModernXillWorkbook(workbook, file);
	}

	@Override public XillWorkbook loadWorkbook(InputStream stream, boolean readonly, File file) throws IOException {
		XSSFWorkbook workbook = new XSSFWorkbook(stream);
		ModernXillWorkbook modernXillWorkbook = new ModernXillWorkbook(workbook, file);
		modernXillWorkbook.setReadonly(readonly);
		modernXillWorkbook.file = file;
		return modernXillWorkbook;
	}
}
