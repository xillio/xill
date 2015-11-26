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
 * Construct to create a new XillSheet.
 *
 * @author Daan Knoope
 */
public class CreateSheetConstruct extends Construct {

	@Inject
	private ExcelService excelService;

	/**
	 * Processes the xill code input to create a new XillSheet.
	 *
	 * @param service       ExcelService object
	 * @param workbookInput {@link XillWorkbook} {@link MetaExpression}: a String with a workbook
	 *                      stored inside
	 * @param name          the name that the new sheet should be
	 * @return returns a JSON-object containing the name, row length and column length properties
	 * as well as the newly created {@link XillSheet} itself stored inside
	 * @throws RobotRuntimeException when the provided workbook is incorrect (null), the name is
	 *                               invalid or the workbook is read-only
	 */
	static MetaExpression process(ExcelService service, MetaExpression workbookInput, MetaExpression name) {
		XillWorkbook workbook = assertMeta(workbookInput, "parameter 'workbook'", XillWorkbook.class, "result of loadWorkbook or createWorkbook");
		String sheetName = name.getStringValue();
		XillSheet sheet;
		try {
			sheet = service.createSheet(workbook, sheetName);
		} catch (NullPointerException e) { //When the provided workbook is incorrect
			throw new RobotRuntimeException(e.getMessage(), e);
		} catch (IllegalArgumentException e) { //When something is wrong with the name or when the workbook is read-only
			throw new RobotRuntimeException(e.getMessage(), e);
		}

		LinkedHashMap<String, MetaExpression> sheetObject = new LinkedHashMap<>();
		sheetObject.put("sheetName", fromValue(sheet.getName()));
		sheetObject.put("rows", fromValue(0));
		sheetObject.put("columns", fromValue(0));
		MetaExpression toReturn = fromValue(sheetObject);

		toReturn.storeMeta(XillSheet.class, sheet);

		return toReturn;
	}

	@Override
	public ConstructProcessor prepareProcess(ConstructContext context) {
		return new ConstructProcessor(
						(a, b) -> process(excelService, a, b),
						new Argument("workbook", ATOMIC),
						new Argument("name", ATOMIC));
	}

}
