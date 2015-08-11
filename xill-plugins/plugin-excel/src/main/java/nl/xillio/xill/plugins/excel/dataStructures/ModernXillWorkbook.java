package nl.xillio.xill.plugins.excel.dataStructures;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.IOException;

public class ModernXillWorkbook extends XillWorkbook{
	public ModernXillWorkbook(XSSFWorkbook workbook, File file) throws IOException {
		super(workbook, file);
	}
}
