package nl.xillio.xill.plugins.excel.dataStructures;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * Created by Daan Knoope on 5-8-2015.
 */

//This workbookwriter can be used for exporting to xlsx
public class ModernWorkbookWriter extends WorkbookWriter {
	public ModernWorkbookWriter() {
		super();
		super.workbook = new XSSFWorkbook();
		super.extension = ".xlsx";
	}
}
