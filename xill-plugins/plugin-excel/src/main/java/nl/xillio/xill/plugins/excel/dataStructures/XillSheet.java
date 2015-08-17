package nl.xillio.xill.plugins.excel.dataStructures;

import nl.xillio.xill.api.components.MetadataExpression;
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

	public XillSheet(Sheet sheet, boolean readonly) {
		this.readonly = readonly;
		this.sheet = sheet;
		rowLength = sheet.getLastRowNum() + 1; //Added one because POI is zero indexed
		columnLength = calculateColumnLength();
		name = sheet.getSheetName();
		int i = 0;
	}

	private int calculateColumnLength() {
		//CPU intensive, use only once, then use the columnLength property
		int maxColumnSize = 0; //Initialized to -1 because 1 will be added at return and minimum is zero
		for (int i = 0; i < rowLength; i++)
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

	public Object getCellValue(XillCellRef cellRef) {
		XillRow row = getRow(cellRef.getCellReference().getRow());
		XillCell cell = null;
		if (!row.isNull())
			cell = row.getCell(cellRef.getCellReference().getCol());
		return cell.getValue();
	}

	XillRow getRow(int rowNr) {
		return new XillRow(sheet.getRow(rowNr));
	}

	void createRow(int rowNr) {
		sheet.createRow(rowNr);
	}

	private XillCell getCell(XillCellRef cellRef) {
		int columnNr = cellRef.getColumn();
		int rowNr = cellRef.getRow();

		XillRow row = getRow(rowNr);
		if (row.isNull()) {
			createRow(rowNr);
			row = getRow(rowNr);
		}
		XillCell cell = row.getCell(columnNr);
		if (cell.isNull()) {
			row.createCell(columnNr);
			cell = row.getCell(columnNr);
		}

		return cell;
	}

	public void setCellValue(XillCellRef cellRef, String value) {
		getCell(cellRef).setCellValue(value);
	}

	public void setCellValue(XillCellRef cellRef, Double value) {
		getCell(cellRef).setCellValue(value);
	}

	public void setCellValue(XillCellRef cellRef, boolean value) {
		getCell(cellRef).setCellValue(value);
	}

	public boolean isReadonly() {
		return readonly;
	}

}
