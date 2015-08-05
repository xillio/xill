package nl.xillio.xill.plugins.excel.constructs;

import com.google.inject.Inject;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.plugins.excel.dataStructures.CellCoordinates;
import nl.xillio.xill.plugins.excel.dataStructures.CellData;
import nl.xillio.xill.plugins.excel.dataStructures.WorkbookType;
import nl.xillio.xill.plugins.excel.dataStructures.WorkbookWriter;
import nl.xillio.xill.plugins.excel.services.ExcelService;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.util.List;

/**
 * Created by Daan Knoope on 5-8-2015.
 */
public class WriteConstruct extends Construct {

	@Inject
	private ExcelService excelService;

	@Override public ConstructProcessor prepareProcess(ConstructContext context) {
		return new ConstructProcessor((a) -> process(excelService, a), new Argument("valueToWrite", ATOMIC));
	}

	static MetaExpression process(ExcelService excelService, MetaExpression valueToWrite){
		WorkbookWriter workbookWriter = new WorkbookWriter(WorkbookType.xlsx);
		workbookWriter.createWorkSheet("Sheet1", 10, 10);
		workbookWriter.setCellValue("Sheet1",new CellCoordinates((short)2,(short)2),(String) extractValue(valueToWrite));
		return fromValue(workbookWriter.writeToFS("testfile"));
	}


}
