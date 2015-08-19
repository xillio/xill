package nl.xillio.xill.plugins.excel.constructs;

import com.google.inject.Inject;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.excel.datastructures.XillSheet;
import nl.xillio.xill.plugins.excel.datastructures.XillWorkbook;
import nl.xillio.xill.plugins.excel.services.ExcelService;

import java.util.LinkedHashMap;

/**
 * Construct to load a XillSheet from a given workbook.
 * @author Daan Knoope 
 */
public class LoadSheetConstruct extends Construct {

	@Override
	public ConstructProcessor prepareProcess(ConstructContext context) {
		return new ConstructProcessor(
						LoadSheetConstruct::process,
						new Argument("workbook", ATOMIC), new Argument("sheetName", ATOMIC));
	}

	/**
	 * Processes the xill code to load a XillSheet from a given workbook.
	 * @param 	workbook	a workbook object including a {@link XillWorkbook} created by
	 * 										{@link CreateWorkbookConstruct} or {@link LoadWorkbookConstruct}
	 * @param 	sheetName the name of the sheet that should be returned
	 *
	 * @return 	returns a sheet object containing a {@link XillSheet}
	 *
	 * @throws	RobotRuntimeException When no valid workbook has been provided (null)
	 * 					or when the name of the sheet cannot be found in the provided workbook
	 */
	static MetaExpression process(MetaExpression workbook, MetaExpression sheetName) {
		XillWorkbook Workbook = assertMeta(workbook, "parameter 'workbook'", XillWorkbook.class, "result of loadWorkbook or createWorkbook");
		XillSheet sheet;
		try {
			sheet = Workbook.getSheet(sheetName.getStringValue());
		} catch (IllegalArgumentException e) {
			throw new RobotRuntimeException(e.getMessage(), e);
		}

		LinkedHashMap<String, MetaExpression> sheetObject = new LinkedHashMap<>();
		sheetObject.put("Sheet name", fromValue(sheet.getName()));
		sheetObject.put("Rows", fromValue(sheet.getRowLength()));
		sheetObject.put("Columns", fromValue(sheet.getColumnLength()));

		MetaExpression returnValue = fromValue(sheetObject);
		returnValue.storeMeta(XillSheet.class, sheet);

		return returnValue;
	}
	
}
