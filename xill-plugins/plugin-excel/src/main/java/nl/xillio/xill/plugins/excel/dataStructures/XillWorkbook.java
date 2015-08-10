package nl.xillio.xill.plugins.excel.dataStructures;

import nl.xillio.xill.api.components.MetadataExpression;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Daan Knoope on 7-8-2015.
 */
public class XillWorkbook implements MetadataExpression{
	Workbook workbook;
	Sheet sheet;

	public XillWorkbook(){

	}

	public XillWorkbook(boolean legacy){
		if(legacy)
			workbook = new HSSFWorkbook();
		else
			workbook = new XSSFWorkbook();
	}

	public String loadWorkbook(String path, File file) throws IOException {
		Workbook workbook = null; //if loading fails, null will be added to result
		InputStream fileStream = new FileInputStream(file);
		if(path.endsWith(".xls"))
			workbook = new HSSFWorkbook(fileStream);
		else if (path.endsWith(".xlsx"))
			workbook = new XSSFWorkbook(fileStream);
		this.workbook = workbook;
		return "Excel Workbook [" + getFilePath(file) + "]";
	}

	public String getFilePath(File file) throws IOException{
		return file.getCanonicalPath().toString();
	}

	public int rowSize(Sheet sheet){
		return sheet.getLastRowNum() + 1; //Added one because POI is zero indexed
	}

	public int columnSize(Sheet sheet){
		int maxColumnSize = -1; //Initialized to -1 because 1 will be added at return and minimum is zero
		for(int i = 0; i < rowSize(sheet); i++)
			if(maxColumnSize < sheet.getRow(i).getLastCellNum())
				maxColumnSize = sheet.getRow(i).getFirstCellNum();
		return maxColumnSize + 1; // Added one because POI is zero index
	}

	public XillSheet getSheet(String sheetName) {
		return new XillSheet(workbook.getSheet(sheetName));
	}

	public String name(Sheet sheet){
		return sheet.getSheetName();
	}
}
