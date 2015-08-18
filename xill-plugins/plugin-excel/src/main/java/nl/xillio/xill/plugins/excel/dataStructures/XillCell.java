package nl.xillio.xill.plugins.excel.datastructures;

import nl.xillio.xill.api.errors.NotImplementedException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;

/**
 * Created by Daan Knoope on 17-8-2015.
 */
public class XillCell {

	private Cell cell;

	public XillCell(Cell cell) {
		this.cell = cell;
	}

	boolean isDateFormatted() {
		return DateUtil.isCellDateFormatted(cell);
	}

	public Object getValue() {
		Object toReturn;
		if (!isNull()) {
			switch (cell.getCellType()) {
				case Cell.CELL_TYPE_STRING:
					toReturn = cell.getRichStringCellValue().getString();
					break;
				case Cell.CELL_TYPE_BOOLEAN:
					toReturn = cell.getBooleanCellValue();
					break;
				case Cell.CELL_TYPE_FORMULA:
					toReturn = cell.getCellFormula();
					break;
				case Cell.CELL_TYPE_NUMERIC:
					if (isDateFormatted()) {
						toReturn = cell.getDateCellValue();
					} else {
						toReturn = cell.getNumericCellValue();
					}
					break;
				default:
					throw new NotImplementedException("A cell format that has been used in the Excel file is currently unsupported.");
			}
		} else {
			toReturn = "[EMPTY]";
		}
		return toReturn;
	}

	public boolean isNull() {
		return cell == null;
	}

	public void setCellValue(String value) {
		if (value.startsWith("="))
			cell.setCellFormula(value);
		else
			cell.setCellValue(value);
	}

	public void setCellValue(Double value) {
		cell.setCellValue(value);
	}

	public void setCellValue(boolean value) {
		cell.setCellValue(value);
	}

}
