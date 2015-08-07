package nl.xillio.xill.plugins.excel.dataStructures;

import org.apache.poi.hssf.util.CellReference;

/**
 * Created by Daan Knoope on 7-8-2015.
 */
public class XillCellRef {
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
}
