package nl.xillio.xill.plugins.excel.dataStructures;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;

public class LegacyXillWorkbook extends XillWorkbook{
	public LegacyXillWorkbook(HSSFWorkbook workbook){
		super.workbook = workbook;
	}
}
