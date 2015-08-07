package nl.xillio.xill.plugins.excel.constructs;

import com.google.inject.Inject;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.components.Robot;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.excel.dataStructures.XillSheet;
import nl.xillio.xill.plugins.excel.dataStructures.XillWorkbook;
import nl.xillio.xill.plugins.excel.services.ExcelService;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.util.LinkedHashMap;

/**
 * Created by Daan Knoope on 6-8-2015.
 */
public class LoadSheetConstruct extends Construct {

	@Inject
	private ExcelService excelService;

	@Override public ConstructProcessor prepareProcess(ConstructContext context) {
		return new ConstructProcessor((a, b) -> process(excelService, a, b),
						new Argument("workbook", ATOMIC), new Argument("sheetName", ATOMIC));
	}

	static MetaExpression process(ExcelService excelService,
					MetaExpression workbookInput, MetaExpression sheetName){
		XillWorkbook workbook = assertMeta(workbookInput, "workbook", XillWorkbook.class, "Excel workbook");
		XillSheet sheet = null;
		try {
			sheet = workbook.getSheet(sheetName.getStringValue());
		} catch (Exception e) {
			throw new RobotRuntimeException(e.getMessage() + ": Could not find the sheet in this workbook or because no workbook was loaded.");
		}
		LinkedHashMap<String, MetaExpression> properties = new LinkedHashMap<>();
		properties.put("Sheet name", fromValue(sheet.getName()));
		properties.put("Rows", fromValue(sheet.getRowLength()));
		properties.put("Columns", fromValue(sheet.getColumnLength()));
		MetaExpression result = fromValue(properties);
		result.storeMeta(sheet);
		return result;
	}
}
