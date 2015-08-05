package nl.xillio.xill.plugins.excel.dataStructures;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;

/**
 * Created by Daan Knoope on 5-8-2015.
 */

//This workbookwriter can be used for exporting to xls
public class LegacyWorkbookWriter extends WorkbookWriter {
	public LegacyWorkbookWriter() {
		super();
		super.workbook = new HSSFWorkbook();
		super.extension = ".xls";
	}
}
