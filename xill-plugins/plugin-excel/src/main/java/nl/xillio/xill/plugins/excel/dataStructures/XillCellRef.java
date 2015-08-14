package nl.xillio.xill.plugins.excel.dataStructures;

import nl.xillio.xill.api.components.MetadataExpression;
import org.apache.poi.hssf.util.CellReference;

/**
 * Created by Daan Knoope on 7-8-2015.
 */
public class XillCellRef implements MetadataExpression{

	@Override
	public boolean equals(Object obj) {
		XillCellRef cellRef = (XillCellRef) obj;
		return cellRef.getColumn() == this.getColumn() && cellRef.getRow() == this.getRow();
	}

	@Override
	public int hashCode(){
		return getColumn() * 100000 + getRow();
	}

	private CellReference cellReference;

	public XillCellRef(String column, int row){
		cellReference = new CellReference(column + row); //A, 12 => A12
	}

	public XillCellRef(int column, int row){
			cellReference = new CellReference(column,row);
	}

	public CellReference getCellReference() {
		return cellReference;
	}

	public int getColumn(){
		return cellReference.getCol();
	}

	public int getRow(){
		return cellReference.getRow();
	}
}
