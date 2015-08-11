package nl.xillio.xill.plugins.excel.dataStructures;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class XillLegacyWorkbookFactory extends XillWorkbookFactory{

	@Override public XillWorkbook createWorkbook() {
		HSSFWorkbook workbook = new HSSFWorkbook();
		return new LegacyXillWorkbook(workbook);
	}

	@Override public XillWorkbook loadWorkbook(InputStream stream, boolean readonly,File file) throws IOException {
		HSSFWorkbook workbook = new HSSFWorkbook(stream);
		LegacyXillWorkbook legacyXillWorkbook = new LegacyXillWorkbook(workbook);
		legacyXillWorkbook.setReadonly(readonly);
		legacyXillWorkbook.file = file;
		return legacyXillWorkbook;
	}
}
