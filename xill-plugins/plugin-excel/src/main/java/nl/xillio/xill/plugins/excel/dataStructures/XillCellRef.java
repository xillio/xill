package nl.xillio.xill.plugins.excel.datastructures;

import nl.xillio.xill.api.components.MetadataExpression;
import org.apache.poi.hssf.util.CellReference;

/**
 * Representation of a reference to an Excel cell.
 * Wrapper for the Apache POI {@link CellReference} class.
 *
 * @author Daan Knoope
 */
public class XillCellRef implements MetadataExpression {

	private CellReference cellReference;

	/**
	 * Constructs a XillCellRef based on 1-indexed references
	 *
	 * @param column a string pointing to the column of the cell
	 *               in alphabetic notation (e.g. "AB"),
	 *               1-indexed (Excel notation)
	 * @param row    an integer pointing to the row of the cell in
	 *               numeric notation (e.g. 28), 1-indexed
	 *               (Excel notation)
	 */
	public XillCellRef(String column, int row) {
		if (row <= 0)
			throw new IllegalArgumentException("The row number must be one or higher (" + row + " was used)");
		cellReference = new CellReference(column + row); //A, 12 => A12
	}

	/**
	 * Constructor for the XillCellRef class
	 *
	 * @param column an integer pointing to the column of the cell
	 *               in numeric notation (e.g. 28), 1-indexed
	 *               (Excel notation)
	 * @param row    an integer pointing to the row of the cell
	 *               in numeric notation (e.g. 28), 1-indexed (Excel notation)
	 */
	public XillCellRef(int column, int row) {
		if (row < 1)
			throw new IllegalArgumentException("The row number must be one or higher (" + row + " was used)");
		if (column < 1)
			throw new IllegalArgumentException("The column number must be one or higher (" + column + " was used)");
		cellReference = new CellReference(row - 1, column - 1);
	}

	@Override public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		XillCellRef cellRef = (XillCellRef) o;

		return !(cellReference != null ? !cellReference.equals(cellRef.cellReference) : cellRef.cellReference != null);

	}

	@Override public int hashCode() {
		return cellReference != null ? cellReference.hashCode() : 0;
	}

	/**
	 * Gets the number of the Excel column, 0-indexed
	 *
	 * @return the column of the reference as integer
	 */
	public int getColumn() {
		return cellReference.getCol();
	}

	/**
	 * Gets the number of the Exel row, 0-indexed
	 *
	 * @return the row of the reference as integer, 0-indexed
	 */
	public int getRow() {
		return cellReference.getRow();
	}
}
