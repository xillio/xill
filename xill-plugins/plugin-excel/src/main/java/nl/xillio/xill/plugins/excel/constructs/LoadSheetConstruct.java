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
 * Created by Daan Knoope on 6-8-2015.
 */
public class LoadSheetConstruct extends Construct {

	@Inject
	private ExcelService excelService;

	@Override public ConstructProcessor prepareProcess(ConstructContext context) {
		return new ConstructProcessor(
						LoadSheetConstruct::process,
						new Argument("workbook", ATOMIC), new Argument("sheetName", ATOMIC));
	}

	static MetaExpression process(MetaExpression workbookInput, MetaExpression sheetName){
		XillWorkbook workbook = assertMeta(workbookInput, "parameter 'workbook'", XillWorkbook.class, "result of loadWorkbook or createWorkbook");
		XillSheet sheet = null;
		sheet = workbook.getSheet(sheetName.getStringValue());
		if(sheet == null)
			throw new RobotRuntimeException("Sheet can not be found in the supplied workbook");

		LinkedHashMap<String, MetaExpression> properties = new LinkedHashMap<>();
		properties.put("Sheet name", fromValue(sheet.getName()));
		properties.put("Rows", fromValue(sheet.getRowLength()));
		properties.put("Columns", fromValue(sheet.getColumnLength()));
		MetaExpression result = fromValue(properties);
		result.storeMeta(XillSheet.class, sheet);
		return result;
	}
}
