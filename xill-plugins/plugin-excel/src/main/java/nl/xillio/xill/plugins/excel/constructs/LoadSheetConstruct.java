package nl.xillio.xill.plugins.excel.constructs;

import com.google.inject.Inject;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.plugins.excel.services.ExcelService;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

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
		Workbook workbook = assertMeta(workbookInput, "workbook", Workbook.class, "Excel workbook");
		Sheet sheet = excelService.loadSheet(workbook, sheetName.toString());
		LinkedHashMap<String, MetaExpression> properties = new LinkedHashMap<>();
		properties.put("Rows", fromValue(excelService.rowSize(sheet)));
		properties.put("Columns", fromValue(excelService.columnSize(sheet)));
		properties.put("Name", fromValue(excelService.name(sheet)));
		MetaExpression result = fromValue(properties);
		result.storeMeta(sheet);
		return result;
	}
}
