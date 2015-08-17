package nl.xillio.xill.plugins.excel.dataStructures;

import nl.xillio.xill.api.components.MetadataExpression;
import org.apache.poi.hssf.util.CellReference;

/**
 * Created by Daan Knoope on 7-8-2015.
 */
public class XillCellRef implements MetadataExpression {

	private CellReference cellReference;

	public XillCellRef(String column, int row) {
		cellReference = new CellReference(column + row); //A, 12 => A12
	}

	public XillCellRef(int column, int row) {
		cellReference = new CellReference(column, row);
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

	public CellReference getCellReference() {
		return cellReference;
	}

	public int getColumn() {
		return cellReference.getCol();
	}

	public int getRow() {
		return cellReference.getRow();
	}
}
