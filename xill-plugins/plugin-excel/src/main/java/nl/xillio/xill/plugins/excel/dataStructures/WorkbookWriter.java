package nl.xillio.xill.plugins.excel.dataStructures;

import nl.xillio.xill.api.errors.NotImplementedException;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import static nl.xillio.xill.plugins.excel.dataStructures.WorkbookType.*;

/**
 * Created by Daan Knoope on 5-8-2015.
 */
public class WorkbookWriter {

	private Workbook workbook;
	private WorkbookType workbookType;

	//Constructor
	public WorkbookWriter(WorkbookType type){
		workbookType = type;
		workbook = createWorkbook(type);
	}

	private String[] getSheetNames(){
		String[] sheetNames = new String[workbook.getNumberOfSheets()];
		for(int i = 0; i < workbook.getNumberOfSheets(); i++){
			sheetNames[i] = workbook.getSheetName(i);
		}
		return sheetNames;
	}

	private Workbook createWorkbook(WorkbookType type){
		Workbook workbook;
		switch(type){
			case xls:
				workbook = new HSSFWorkbook();
				break;
			case xlsx:
				workbook = new XSSFWorkbook();
				break;
			default:
				throw new NotImplementedException("The given workbooktype has not been implemented");
		}

		return workbook;
	}

	//Creates the cells in the POI system to write to
	public void createWorkSheet(String name, int rows, int columns){
		Sheet sheet = workbook.createSheet(name);
		for(int i = 0; i < rows; i++)
		{
			Row row = sheet.createRow(i);
			for(int j = 0; j < columns; j++) {
				row.createCell(j);
			}
		}
	}

	//Finds max size
	public void createWorkSheet(String name, List<CellData> CellDataList){
		short maxColumn = 0;
		short maxRow 		= 0;
		for(CellData data : CellDataList){
			CellCoordinates coordinates = data.getCellCoordinates();
			if(maxColumn < coordinates.getColumn())
				maxColumn = coordinates.getColumn();
			if(maxRow < coordinates.getRow())
				maxRow = coordinates.getRow();
		}
		createWorkSheet(name, maxRow, maxColumn);
	}

	//Finds the cell to write to and changes its value
	private void setCellValue(Sheet sheet, CellCoordinates cellCoordinates, String value){
		sheet.getRow(cellCoordinates.getRow() - 1).getCell(cellCoordinates.getColumn() - 1).setCellValue(workbook.getCreationHelper().createRichTextString(value));
	}

	public void setCellValue(String sheet, CellCoordinates cellCoordinates, String value){
		setCellValue(workbook.getSheet(sheet), cellCoordinates, value);
	}

	//Sets cell values for multiple cells
	public void setCellValue(Sheet sheet, List<CellData> CellDataList){
		for(CellData data : CellDataList){
			setCellValue(sheet,data.getCellCoordinates(), data.getValue());
		}
	}

	public boolean writeToFS(String filename){
		filename += "." + workbookType.toString(); //adds extension to filename
		FileOutputStream fileOut;
		try {
			fileOut = new FileOutputStream(filename);
		} catch (FileNotFoundException e) {
			return false;
		}
		try {
			workbook.write(fileOut);
		} catch (IOException e) {
			return false;
		}
		try {
			fileOut.close();
		} catch (IOException e) { //What situation could trigger this error?
			return false;
		}
		return true;
	}

	public Workbook getWorkbook() {
		return workbook;
	}
}
