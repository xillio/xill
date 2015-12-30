package nl.xillio.xill.plugins.excel.constructs;

import nl.xillio.xill.api.behavior.NumberBehavior;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.excel.datastructures.XillSheet;
import nl.xillio.xill.plugins.excel.datastructures.XillWorkbook;

import java.util.LinkedHashMap;

/**
 * Construct to load a XillSheet from a given workbook.
 *
 * @author Daan Knoope
 */
public class LoadSheetConstruct extends Construct {

    /**
     * Processes the xill code to load a XillSheet from a given workbook.
     *
     * @param workbook  a workbook object including a {@link XillWorkbook} created by
     *                  {@link CreateWorkbookConstruct} or {@link LoadWorkbookConstruct}
     * @param sheetName the name of the sheet that should be returned
     * @return returns a sheet object containing a {@link XillSheet}
     * @throws RobotRuntimeException When no valid workbook has been provided (null)
     *                               or when the name of the sheet cannot be found in the provided workbook
     */
    static MetaExpression process(MetaExpression workbook, MetaExpression sheetName) {
        XillWorkbook xillWorkbook = assertMeta(workbook, "parameter 'workbook'", XillWorkbook.class, "result of loadWorkbook or createWorkbook");
        XillSheet sheet = tryGetSheet(sheetName, xillWorkbook);

        LinkedHashMap<String, MetaExpression> sheetObject = new LinkedHashMap<>();
        sheetObject.put("sheetName", fromValue(sheet.getName()));
        sheetObject.put("rows", fromValue(sheet.getRowLength()));
        sheetObject.put("columns", fromValue(sheet.getColumnLength()));

        MetaExpression returnValue = fromValue(sheetObject);
        returnValue.storeMeta(sheet);

        return returnValue;
    }

    private static XillSheet tryGetSheet(MetaExpression sheetName, XillWorkbook xillWorkbook) {
        XillSheet sheet;
        try {
            // If the sheetName is a number get the sheet at that index, else get the sheet with that name.
            if (sheetName.getValue() instanceof NumberBehavior) {
                sheet = xillWorkbook.getSheetAt(sheetName.getNumberValue().intValue());
            } else {
                sheet = xillWorkbook.getSheet(sheetName.getStringValue());
            }
        } catch (IllegalArgumentException e) {
            throw new RobotRuntimeException(e.getMessage(), e);
        }
        return sheet;
    }

    @Override
    public ConstructProcessor prepareProcess(ConstructContext context) {
        return new ConstructProcessor(
                LoadSheetConstruct::process,
                new Argument("workbook", ATOMIC), new Argument("sheet", ATOMIC));
    }

}
