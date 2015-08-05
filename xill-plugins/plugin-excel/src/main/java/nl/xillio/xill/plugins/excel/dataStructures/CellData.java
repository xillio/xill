package nl.xillio.xill.plugins.excel.dataStructures;

/**
 * Created by Daan Knoope on 5-8-2015.
 */
public final class CellData {
	private String value;
	private CellCoordinates cellCoordinates;

	public CellData(String value, CellCoordinates cellCoordinates) {
		this.value = value;
		this.cellCoordinates = cellCoordinates;
	}

	public String getValue() {
		return value;
	}

	public CellCoordinates getCellCoordinates() {
		return cellCoordinates;
	}
}
