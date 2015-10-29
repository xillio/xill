package nl.xillio.xill.plugins.excel.datastructures;

import org.apache.poi.ss.usermodel.Row;

/**
 * Representation for an Excel Row.
 * Wrapper for the Apache POI {@link Row} class.
 *
 * @author Daan Knoope
 */
public class XillRow {
	private Row row;

	/**
	 * Constructor for the {@link XillRow} class.
	 *
	 * @param row an Apache POI {@link Row} object
	 */
	public XillRow(Row row) {
		this.row = row;
	}

	/**
	 * Gets a {@link XillCell} from this row.
	 *
	 * @param columnNr the number of the column where the cell is located in this row
     * @param sheet the {@link XillSheet} to which this row belongs
	 * @return the {@link XillCell} on the provided column
	 */
	public XillCell getCell(int columnNr, XillSheet sheet) {
		return new XillCell(row.getCell(columnNr), sheet);
	}

	public boolean isNull() {
		return row == null;
	}

	/**
	 * Creates a cell given the column number where it should be.
	 * Creates a new cell at the same location (overwriting the old cell) if one already exists.
	 *
	 * @param columnNr the number of the column where the new cell should be located
	 * @return the newly created {@link XillCell}
	 * @throws IllegalArgumentException if columnIndex &lt; 0 or greater
	 *                                  than the maximum number of supported columns (255 for *.xls, 1048576
	 *                                  for *.xlsx)
	 */
	public XillCell createCell(int columnNr, XillSheet sheet) {
		return new XillCell(row.createCell(columnNr), sheet);
	}

	/**
	 * Gets the column number of the last cell in this row.
	 *
	 * @return the column number of the last cell in this row
	 */
	public int getLastCellNum() {
		return row.getLastCellNum();
	}
}
