package nl.xillio.xill.plugins.excel.constructs;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.excel.datastructures.DateImpl;
import nl.xillio.xill.plugins.excel.datastructures.XillCellRef;
import nl.xillio.xill.plugins.excel.datastructures.XillSheet;

/**
 * Construct to get the value of a cell from a XillSheet.
 *
 * @author Daan Knoope
 */
public class GetCellConstruct extends Construct {

    /**
     * Processes the xill code to return the value of the indicated cell from the provided XillSheet.
     *
     * @param sheetInput a sheet object containing a XillSheet as generated by
     *                   {@link CreateSheetConstruct} or {@link LoadSheetConstruct}
     * @param column     the column of the required cell - either in alphabetical or
     *                   numeric notation (eg AB or 28)
     * @param row        the row of the required cell in numeric notation (eg 28)
     * @return the value of the cell
     */
    static MetaExpression process(MetaExpression sheetInput, MetaExpression column, MetaExpression row) {
        XillSheet sheet = assertMeta(sheetInput, "sheet", XillSheet.class, "Excel Sheet");
        XillCellRef cell;
        if (!isNumeric(row))
            throw new RobotRuntimeException("The row number must be in numeric notation (\"" + row.getStringValue() + "\" was used)");
        if (isNumeric(column))
            cell = createCellRef(column.getNumberValue().intValue(), row.getNumberValue().intValue());
        else //numeric notation
            cell = createCellRef(column.getStringValue(), row.getNumberValue().intValue());
        Object cellValue = sheet.getCellValue(cell);

        if (cellValue instanceof DateImpl) {
            DateImpl date = (DateImpl) cellValue;
            MetaExpression toReturn = fromValue(date.getZoned().toString());
            toReturn.storeMeta(date);
            return toReturn;
        }
        return parseObject(cellValue);
    }

    static XillCellRef createCellRef(String column, int row) {
        try {
            return new XillCellRef(column, row);
        } catch (IllegalArgumentException e) {
            throw new RobotRuntimeException(e.getMessage(), e);
        }
    }

    static XillCellRef createCellRef(int column, int row) {
        try {
            return new XillCellRef(column, row);
        } catch (IllegalArgumentException e) {
            throw new RobotRuntimeException(e.getMessage(), e);
        }
    }

    static boolean isNumeric(MetaExpression expression) {
        return !Double.isNaN(expression.getNumberValue().doubleValue());
    }

    @Override
    public ConstructProcessor prepareProcess(ConstructContext context) {
        return new ConstructProcessor(
                GetCellConstruct::process,
                new Argument("sheet", OBJECT),
                new Argument("column", ATOMIC),
                new Argument("row", ATOMIC));
    }
}
