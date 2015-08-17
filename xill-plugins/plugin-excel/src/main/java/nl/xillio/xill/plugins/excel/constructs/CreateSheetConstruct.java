package nl.xillio.xill.plugins.excel.constructs;

import com.google.inject.Inject;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.excel.dataStructures.XillSheet;
import nl.xillio.xill.plugins.excel.dataStructures.XillWorkbook;
import nl.xillio.xill.plugins.excel.services.ExcelService;

import java.util.LinkedHashMap;

/**
 * Construct to Creates a new sheet in the given workbook.
 *
 * @author Daan Knoope
 */
public class CreateSheetConstruct extends Construct {

	@Inject
	private ExcelService excelService;

	static MetaExpression process(ExcelService service, MetaExpression workbookInput, MetaExpression name) {
		XillWorkbook workbook = assertMeta(workbookInput, "parameter 'workbook'", XillWorkbook.class, "result of loadWorkbook or createWorkbook");
		String sheetName = name.getStringValue();
		XillSheet sheet;
		try {
			sheet = service.createSheet(workbook, sheetName);
		} catch (NullPointerException e) {
			throw new RobotRuntimeException(e.getMessage(), e);
		} catch (IllegalArgumentException e) {
			throw new RobotRuntimeException(e.getMessage(), e);
		}

		LinkedHashMap<String, MetaExpression> properties = new LinkedHashMap<>();
		properties.put("Name", fromValue(sheet.getName()));
		properties.put("Rows", fromValue(0));
		properties.put("Columns", fromValue(0));
		MetaExpression toReturn = fromValue(properties);
		toReturn.storeMeta(XillSheet.class, sheet);
		return toReturn;
	}

	@Override
	public ConstructProcessor prepareProcess(ConstructContext context) {
		return new ConstructProcessor(
						(a, b) -> process(excelService, a, b),
						new Argument("workboook", ATOMIC),
						new Argument("name", ATOMIC));
	}
}
