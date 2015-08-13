package nl.xillio.xill.plugins.excel.dataStructures;

import nl.xillio.xill.api.components.MetadataExpression;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Daan Knoope on 7-8-2015.
 */
public class XillWorkbook implements MetadataExpression{
	protected Workbook workbook;
	private File file;
	private boolean readonly = false;
	private String location;

	public XillWorkbook(Workbook workbook, File file) throws IOException {
		this.workbook = workbook;
		this.file = file;
		location = file.getCanonicalPath();
		readonly = !file.canWrite();
	}

	public String getFileString(){
		return "Excel Workbook [" + location + "]";
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

	public XillSheet makeSheet(String sheetName){
		return new XillSheet(workbook.createSheet(sheetName));
	}

	public List<String> getSheetNames() {
		List<String> sheetnames = new ArrayList<>();
		for(int i = 0; i < workbook.getNumberOfSheets(); i++)
			sheetnames.add(workbook.getSheetAt(i).getSheetName());
		return sheetnames;
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

