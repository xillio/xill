package nl.xillio.xill.plugins.excel.dataStructures;

/**
 * Created by Daan Knoope on 5-8-2015.
 */

//Holds the coordinates of the cells
// Numeric instead of AB notation for columns
public class CellCoordinates {
	private short row;
	private short column;

	public CellCoordinates(short row, short column) {
		this.row = row;
		this.column = column;
	}

	public short getRow() {
		return row;
	}

	public short getColumn() {
		return column;
	}
}
