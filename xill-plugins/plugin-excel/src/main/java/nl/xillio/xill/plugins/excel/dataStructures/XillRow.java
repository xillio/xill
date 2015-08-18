package nl.xillio.xill.plugins.excel.datastructures;

import org.apache.poi.ss.usermodel.Row;

/**
 * Created by Daan Knoope on 17-8-2015.
 */
public class XillRow {
	private Row row;

	public XillRow(Row row) {
		this.row = row;
	}

	public XillCell getCell(int columnNr) {
		return new XillCell(row.getCell(columnNr));
	}

	public boolean isNull() {
		return row == null;
	}

	public XillCell createCell(int columnNr) {
		row.createCell(columnNr);
		return new XillCell(row.getCell(columnNr));
	}

	public int getLastCellNum() {
		return row.getLastCellNum();
	}
}
