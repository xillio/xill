package nl.xillio.xill.plugins.excel.constructs;

import nl.xillio.xill.api.behavior.BooleanBehavior;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.excel.datastructures.XillCellRef;
import nl.xillio.xill.plugins.excel.datastructures.XillSheet;

import java.util.regex.Pattern;

/**
 * Construct to change the value of a given cell.
 *
 * @author Daan Knoope
 */
public class SetCellConstruct extends Construct {

	/**
	 * Processes xill code to change the value of a given cell
	 *
	 * @param sheet  the sheet which' cell needs to be changed
	 * @param column the column of the cell which should be changed
	 * @param row    the row of the cell which should be changed
	 * @param value  the value to which the cell should be set
	 * @return {@code true} when the operation has succeeded, else an exception is thrown
	 * @throws RobotRuntimeException when the sheet is read-only
	 * @throws RobotRuntimeException when a wrong notation for the row has been used (should be numeric)
	 * @throws RobotRuntimeException when a wrong notation for the column has been used (should be numeric or alphabetic)
	 */
	static MetaExpression process(MetaExpression sheet, MetaExpression column, MetaExpression row, MetaExpression value) {
		XillSheet Sheet = assertMeta(sheet, "parameter 'sheet'", XillSheet.class, "result of loadSheet or createSheet");
		if (Sheet.isReadonly())
			throw new RobotRuntimeException("Cannot write on sheet: sheet is read-only. First save as new file.");
		if (!isNumeric(row))
			throw new RobotRuntimeException("Wrong notation for row \"" + row.getStringValue() + "\", should be numeric (e.g. 12)");
		if (!isNumericXORAlphabetic(column))
			throw new RobotRuntimeException("Wrong notation for column \"" + column.getStringValue() + "\", should be numerical or alphabetical notation (e.g. AB or 12)");
		XillCellRef cellRef;
		try {
			cellRef = getCellRef(column, row);
		} catch (IllegalArgumentException e) {
			throw new RobotRuntimeException(e.getMessage(), e);
		}
		try {
			setValue(Sheet, cellRef, value);
		} catch (IllegalArgumentException e) {
			throw new RobotRuntimeException(e.getMessage(), e);
		}
		return fromValue(true);
	}

	/**
	 * Sets the value of a cell on a XillSheet
	 *
	 * @param sheet   the {@link XillSheet} which contains the cell which should be changed
	 * @param cellRef a {@link XillCellRef} pointing to the cell which should be changed
	 * @param value   a {@link MetaExpression} containing the value which the cell should contain
	 */
	static void setValue(XillSheet sheet, XillCellRef cellRef, MetaExpression value) {
        nl.xillio.xill.api.data.Date date = value.getMeta(nl.xillio.xill.api.data.Date.class);
        if(date != null) {
            sheet.setCellValue(cellRef, date.getZoned());
        }
		else if (value.getValue() instanceof BooleanBehavior) { // DO NOT REPEAT ANYWHERE ELSE, WAS UNAVOIDABLE :-(
			sheet.setCellValue(cellRef, value.getBooleanValue());
		} else if (isNumeric(value)) {
			sheet.setCellValue(cellRef, value.getNumberValue().doubleValue());
		} else {
			sheet.setCellValue(cellRef, value.getStringValue());
		}
	}

	/**
	 * Creates a XillCellRef given the raw column and row notation.
	 *
	 * @param column a {@link MetaExpression} pointing to the column of the cell which should be changed,
	 *               either in alphabetic or numeric notation (e.g. "AB" or 28)
	 * @param row    a {@link MetaExpression} pointing to the row of the cell which should be changed,
	 *               in numeric notation (e.g. 28)
	 * @return a {@link XillCellRef} pointing to the row and column of the cell
	 */
	static XillCellRef getCellRef(MetaExpression column, MetaExpression row) {
		if (isNumeric(column))
			return new XillCellRef(column.getNumberValue().intValue(), row.getNumberValue().intValue());
		return new XillCellRef(column.getStringValue(), row.getNumberValue().intValue());
	}

	/**
	 * Checks if a MetaExpression is numeric.
	 *
	 * @param expression the {@link MetaExpression} which should be checked
	 * @return {@code true} when the input is numeric or {@code false} when it is not numeric
	 */
	static boolean isNumeric(MetaExpression expression) {
		return !Double.isNaN(expression.getNumberValue().doubleValue());
	}

	/**
	 * Checks if a MetaExpression is either numeric or alphabetic (exclusive or)
	 *
	 * @param expression the {@link MetaExpression} which should be checked
	 * @return {@code true} when the input is numeric or {@code false} when it is not numeric
	 */
	static boolean isNumericXORAlphabetic(MetaExpression expression) {
		return Pattern.matches("[a-zA-Z]*|[0-9]*", expression.getStringValue());
	}

	@Override
	public ConstructProcessor prepareProcess(ConstructContext context) {
		return new ConstructProcessor(
						SetCellConstruct::process,
						new Argument("sheet", OBJECT),
						new Argument("column", ATOMIC),
						new Argument("row", ATOMIC),
						new Argument("value", ATOMIC));
	}
}
