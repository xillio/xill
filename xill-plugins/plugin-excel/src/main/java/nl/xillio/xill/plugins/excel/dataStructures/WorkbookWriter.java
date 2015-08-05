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

/**
 * Created by Daan Knoope on 5-8-2015.
 */
public abstract class WorkbookWriter {

	protected Workbook workbook;
	protected String extension;

	//Constructor
	public WorkbookWriter(){}

	//Creates the cells in the POI system to write to
	private void createWorkSheet(String name, int rows, int columns){
		Sheet sheet = workbook.createSheet(name);
		for(int i = 0; i < rows; i++)
		{
			Row row = sheet.createRow(i);
			for(int j = 0; j < columns; j++) {
				row.createCell(j);
			}
		}
	}

	//Finds max size and create cells in the POI system
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

	public void createWorkSheet(String name, CellData cellData){
		createWorkSheet(name,cellData.getCellCoordinates().getRow(), cellData.getCellCoordinates().getColumn());
	}

	//Finds the cell to write to and changes its value
	private void setCellValue(Sheet sheet, CellCoordinates cellCoordinates, String value){
		sheet.getRow(cellCoordinates.getRow() - 1).getCell(cellCoordinates.getColumn() - 1).setCellValue(workbook.getCreationHelper().createRichTextString(value));
		int i = 0;
	}

	//Sets cell values for multiple cells
	private void setCellValue(Sheet sheet, List<CellData> CellDataList){
		for(CellData data : CellDataList){
			setCellValue(sheet,data.getCellCoordinates(), data.getValue());
		}
	}

	public void createSheetAndSetValues(String Sheet, List<CellData> cellDataList){
		createWorkSheet(Sheet, cellDataList);
		Sheet sheet = workbook.getSheet(Sheet);
		setCellValue(sheet,cellDataList);
	}

	public boolean writeToFS(String filename){
		filename += extension; //adds extension to filename
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
