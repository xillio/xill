package nl.xillio.xill.plugins.excel.datastructures;

import nl.xillio.xill.api.components.MetadataExpression;
import org.apache.poi.ss.usermodel.Sheet;

import java.util.Date;

/**
 * Representation of an Excel sheet.
 * Wrapper for the Apache POI {@link Sheet} class.
 *
 * @author Daan Knoope
 */
public class XillSheet implements MetadataExpression {
	private Sheet sheet;
	private String name;
	private int columnLength;
	private int rowLength;
	private boolean readonly;

	/**
	 * Constructor for the XillSheet class.
	 *
	 * @param sheet    an Apache POI {@link Sheet} object
	 * @param readonly boolean representing if the sheet's workbook is read-only
	 */
	public XillSheet(Sheet sheet, boolean readonly) {
		this.readonly = readonly;
		this.sheet = sheet;

		calculateRowLength();
		columnLength = calculateColumnLength();
		name = sheet.getSheetName();
		int i = 0;
	}

	int calculateRowLength() {
		if (sheet.getLastRowNum() == 0 && sheet.getPhysicalNumberOfRows() == 0)
			rowLength = 0;
		else
			rowLength = sheet.getLastRowNum() + 1;
		return rowLength;
	}

	/**
	 * Calculates the width of the spreadsheet.
	 * Use {@link #getColumnLength()} to get the
	 * width of the spreadsheet: this method should
	 * only be called by the constructor since it is
	 * very CPU intensive.
	 *
	 * @return an integer representing the width of the spreadsheet.
	 */
	int calculateColumnLength() {
		//CPU intensive, use only once, then use the columnLength property
		int maxColumnSize = 0;
		for (int i = 0; i < rowLength; i++)
			if (sheet.getRow(i) != null &&
							maxColumnSize < getRow(i).getLastCellNum()) {
				maxColumnSize = getRow(i).getLastCellNum();
			}
		return maxColumnSize;
	}

	/**
	 * Gets the width of the spreadsheet.
	 *
	 * @return the highest column size as integer
	 */
	public int getColumnLength() {
		return columnLength;
	}

	/**
	 * Gets the height of the spreadsheet.
	 *
	 * @return
	 */
	public int getRowLength() {
		return rowLength;
	}

	/**
	 * Gets the name of the current sheet.
	 *
	 * @return name as string
	 */
	public String getName() {
		return name;
	}

	/**
	 * Gets the value of a cell.
	 *
	 * @param cellRef a {@link XillCellRef} pointing to the required cell
	 * @return the value of the cell referred to by the {@link XillCellRef}.
	 * Can be: {@link String}, {@link Boolean}, {@link Double} or
	 * {@link Date}
	 */
	public Object getCellValue(XillCellRef cellRef) {
		XillRow row = getRow(cellRef.getRow());
		XillCell cell = null;
		if (!row.isNull())
			cell = row.getCell(cellRef.getColumn());
		return cell == null ? new XillCell(null).getValue() : cell.getValue();
	}

	/**
	 * Gets a {@link XillRow}.
	 *
	 * @param rowNr the number of the row which should be retrieved
	 * @return the {@link XillRow} which was requested
	 */
	XillRow getRow(int rowNr) {
		return new XillRow(sheet.getRow(rowNr));
	}

	/**
	 * Creates a new row or overwrites it when one already exists.
	 *
	 * @param rowNr the y-coordinate where the new row should be created
	 * @return the new {@link XillRow}
	 */
	XillRow createRow(int rowNr) {
		return new XillRow(sheet.createRow(rowNr));
	}

	/**
	 * Gets the {@link XillCell} object when the cell already exists
	 * and otherwise creates a new one at the given location.
	 *
	 * @param cellRef a reference to the cell which should be returned
	 * @return the {@link XillCell} which should be returned
	 */
	XillCell getCell(XillCellRef cellRef) {
		int columnNr = cellRef.getColumn();
		int rowNr = cellRef.getRow();

		XillRow row = getRow(rowNr);
		if (row.isNull()) //create new row if non-existent
			row = createRow(rowNr);
		XillCell cell = row.getCell(columnNr);
		if (cell.isNull()) //create new cell if non-existent
			cell = row.createCell(columnNr);

		return cell;
	}

	/**
	 * Sets the value of the cell.
	 *
	 * @param cellRef reference to the cell which should be changed
	 * @param value   string value (could be formula) which should be put in the cell
	 */
	public void setCellValue(XillCellRef cellRef, String value) {
		getCell(cellRef).setCellValue(value);
		calculateRowLength();
		if (cellRef.getColumn() > columnLength)
			columnLength = cellRef.getColumn();

	}

	/**
	 * Sets the value of the cell.
	 *
	 * @param cellRef reference to the cell which should be changed
	 * @param value   double value which should be put in the cell
	 */
	public void setCellValue(XillCellRef cellRef, Double value) {
		getCell(cellRef).setCellValue(value);
		calculateRowLength();
		if (cellRef.getColumn() > columnLength)
			columnLength = cellRef.getColumn();

	}

	/**
	 * Sets the value of the cell.
	 *
	 * @param cellRef reference to the cell which should be changed
	 * @param value   boolean value which should be put in the cell
	 */
	public void setCellValue(XillCellRef cellRef, boolean value) {
		getCell(cellRef).setCellValue(value);
		calculateRowLength();
		if (cellRef.getColumn() > columnLength)
			columnLength = cellRef.getColumn();

	}

	/**
	 * Returns if sheet is in a read-only workbook.
	 *
	 * @return {@code true} if the sheet is in a read-only
	 * workbook, else {@code false}
	 */
	public boolean isReadonly() {
		return readonly;
	}

}
