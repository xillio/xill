package nl.xillio.xill.plugins.excel.datastructures;

import nl.xillio.xill.api.errors.NotImplementedException;
import org.apache.poi.ss.formula.FormulaParseException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DateUtil;

import java.time.ZonedDateTime;
import java.util.Date;


/**
 * Representation of an Excel cell.
 * Wrapper for the Apache POI {@link Cell} class.
 *
 * @author Daan Knoope
 */
public class XillCell {

    private Cell cell;
    private final XillSheet sheet;

    /**
     * Constructor for the XillCell class.
     *
     * @param cell  an Apache POI {@link Cell} object
     * @param sheet the parent {@link XillSheet} of this cell
     */
    public XillCell(Cell cell, XillSheet sheet) {

        this.cell = cell;
        this.sheet = sheet;
    }

    boolean isDateFormatted() {
        return DateUtil.isCellDateFormatted(cell);
    }

    XillSheet getParentSheet() {
        return this.sheet;
    }

    /**
     * Gets the value of this cell.
     *
     * @return the value of this cell as an object. Can be: {@link String}, {@link Boolean}, {@link Double} or {@link Date}.
     * @throws NotImplementedException when this cell has an unsupported formatting
     */
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
                    toReturn = "=" + cell.getCellFormula();
                    break;
                case Cell.CELL_TYPE_NUMERIC:
                    if (isDateFormatted()) {
                        toReturn = new DateImpl(cell.getDateCellValue());
                    } else {
                        Double temp = cell.getNumericCellValue();
                        // Check if temp is an integer or double.
                        toReturn = Double.doubleToRawLongBits(Math.floor(temp) - temp) == 0 ? temp.intValue() : temp;
                    }
                    break;
                case Cell.CELL_TYPE_BLANK:
                    toReturn = null;
                    break;
                default:
                    throw new NotImplementedException("A cell format that has been used in the Excel file is currently unsupported.");
            }
        } else {
            toReturn = null;
        }
        return toReturn;
    }

    public boolean isNull() {
        return cell == null;
    }

    /**
     * Sets this cell's value to the provided String
     *
     * @param value the string value which should be stored in this cell. Can be made a formula by
     *              staring the string off with an equals sign (=).
     */
    public void setCellValue(String value) {
        if (value.startsWith("="))
            try {
                cell.setCellFormula(value.substring(1));
            } catch (FormulaParseException e) {
                throw new IllegalArgumentException(e.getMessage(), e);
            }
        else
            cell.setCellValue(value);
    }

    /**
     * Sets the cell's value to the provided Double
     *
     * @param value the double which should be stored in this cell
     */
    public void setCellValue(Double value) {
        cell.setCellValue(value);
    }

    /**
     * Sets the cell's value to the provided boolean
     *
     * @param value the boolean which should be stored in this cell
     */
    public void setCellValue(boolean value) {
        cell.setCellValue(value);
    }

    /**
     * Sets the cell's value to the provided date time
     *
     * @param dateTime the datevalue that should be stored in the cell
     */
    public void setCellValue(ZonedDateTime dateTime) {
        //Assumption: 0:00 means no time
        boolean containsTime = !(dateTime.getHour() == 0 && dateTime.getMinute() == 0);
        if (containsTime) {
            cell.setCellValue(Date.from(dateTime.toInstant()));
            CellStyle dateTimeStyle = this.sheet.getParentWorkbook().getDateTimeCellStyle();
            this.cell.setCellStyle(dateTimeStyle);
        } else {
            cell.setCellValue(Date.from(dateTime.toInstant()));
            CellStyle dateStyle = this.sheet.getParentWorkbook().getDateCellStyle();
            this.cell.setCellStyle(dateStyle);
        }
    }

}
