package nl.xillio.xill.plugins.excel.dataStructures;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.File;
import java.io.IOException;

public class LegacyXillWorkbook extends XillWorkbook{
	public LegacyXillWorkbook(HSSFWorkbook workbook, File file) throws IOException {
		super(workbook, file);
	}
}
