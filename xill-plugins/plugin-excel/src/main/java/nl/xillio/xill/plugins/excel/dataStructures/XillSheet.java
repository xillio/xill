package nl.xillio.xill.plugins.excel.dataStructures;

import nl.xillio.xill.api.components.MetadataExpression;
import nl.xillio.xill.api.errors.NotImplementedException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

/**
 * Created by Daan Knoope on 7-8-2015.
 */
public class XillSheet implements MetadataExpression {
	private Sheet sheet;
	private String name;
	private int columnLength;
	private int rowLength;
	private boolean readonly;

	public XillSheet(Sheet sheet, boolean readonly){
		this.readonly = readonly;
		this.sheet = sheet;
		rowLength = sheet.getLastRowNum() + 1; //Added one because POI is zero indexed
		columnLength = calculateColumnLength();
		name = sheet.getSheetName();
		int i = 0;
	}

	private int calculateColumnLength(){
		//CPU intensive, use only once, then use the columnLength property
		int maxColumnSize = 0; //Initialized to -1 because 1 will be added at return and minimum is zero
		for(int i = 0; i < rowLength; i++)
			if (sheet.getRow(i) != null &&
							maxColumnSize < sheet.getRow(i).getLastCellNum()) {
				maxColumnSize = sheet.getRow(i).getLastCellNum();
			}
		return maxColumnSize;
	}

	public int getColumnLength() {
		return columnLength;
	}

	public int getRowLength() {
		return rowLength;
	}

	public String getName() {
		return name;
	}

	public Object getCellValue(XillCellRef cellRef){
		Row row = sheet.getRow(cellRef.getCellReference().getRow());
		Cell cell = null;
		if(row != null){
			cell = row.getCell(cellRef.getCellReference().getCol());
		}
		return getValueFromCell(cell);
	}

	private Cell getCell(XillCellRef cellRef){
		int columnNr = cellRef.getColumn();
		int rowNr = cellRef.getRow();
		//TODO implement sepperately
		Row row = sheet.getRow(rowNr);
		if(row == null) {
			sheet.createRow(rowNr);
			row = sheet.getRow(rowNr);
		}
		Cell cell = row.getCell(columnNr);
		if(cell == null) {
			row.createCell(columnNr);
			cell = row.getCell(columnNr);
		}
		return cell;
	}

	public void setCellValue(XillCellRef cellRef, String value){
		if(value.startsWith("="))
			getCell(cellRef).setCellFormula(value);
		else
			getCell(cellRef).setCellValue(value);
	}

	public void setCellValue(XillCellRef cellRef, Double value){
		getCell(cellRef).setCellValue(value);
	}

	public void setCellValue(XillCellRef cellRef, boolean value){
		getCell(cellRef).setCellValue(value);
	}

	private Object getValueFromCell(Cell cell){
		Object toReturn;
		if(cell !=null){
		switch(cell.getCellType()){
			case Cell.CELL_TYPE_STRING:
				toReturn = cell.getRichStringCellValue().getString();
				break;
			case Cell.CELL_TYPE_BOOLEAN:
				toReturn = cell.getBooleanCellValue();
				break;
			case Cell.CELL_TYPE_FORMULA:
				toReturn = cell.getCellFormula();
				break;
			case Cell.CELL_TYPE_NUMERIC:
				if(DateUtil.isCellDateFormatted(cell)){
					toReturn = cell.getDateCellValue();
				}
				else{
					toReturn = cell.getNumericCellValue();
				}
				break;
			default:
				throw new NotImplementedException("A cell formats tha has been used in the Excel file is currently unsupported.");
			}
		}
		else{
			toReturn = "[EMPTY]";
		}
		return toReturn;
	}

	public boolean isReadonly() {
		return readonly;
	}

}
