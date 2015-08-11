package nl.xillio.xill.plugins.excel.dataStructures;

import nl.xillio.xill.api.components.MetadataExpression;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;

/**
 * Created by Daan Knoope on 7-8-2015.
 */
public class XillWorkbook implements MetadataExpression{
	private Workbook workbook;
	private File file;
	private boolean readonly;

	public XillWorkbook(){}

	public XillWorkbook(boolean legacy){
		if(legacy)
			workbook = new HSSFWorkbook();
		else
			workbook = new XSSFWorkbook();
	}

	public void loadWorkbook(String filePath, File file) throws ParseException, IOException {
		Workbook workbook = null;
		this.file = file;
		InputStream fileStream;
		try {
			fileStream = new FileInputStream(file);
		}catch(Exception e){
			throw new IOException("File cannot be opened");
		}
		if(filePath.endsWith(".xls")) {
			try{
				workbook = new HSSFWorkbook(fileStream);
			}catch(IOException e){
				throw new ParseException("File cannot be opened as Excel Workbook",0);
			}
		}
		else if (filePath.endsWith(".xlsx")){
			try {
				workbook = new XSSFWorkbook(fileStream);
			}catch(IOException e){
				throw new ParseException("File cannot be opened as Excel Workbook",0);
			}
		}
		this.workbook = workbook;
	}

	public String getFilePath() throws IOException{
		return file.getCanonicalPath().toString();
	}

	public String getFileString() throws IOException{
		return "Excel Workbook [" + getFilePath() + "]";
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

	public boolean isReadonly() {
		return readonly;
	}

	public void setReadonly(boolean readonly) {
		this.readonly = readonly;
	}
}
