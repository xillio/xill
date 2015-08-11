package nl.xillio.xill.plugins.excel.dataStructures;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ModernXillWorkbook extends XillWorkbook{
	public ModernXillWorkbook(XSSFWorkbook workbook){
		super.workbook = workbook;
	}
}
