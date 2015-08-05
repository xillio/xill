package nl.xillio.xill.plugins.excel.constructs;

import com.google.inject.Inject;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.api.errors.NotImplementedException;
import nl.xillio.xill.plugins.excel.dataStructures.CellCoordinates;
import nl.xillio.xill.plugins.excel.dataStructures.CellData;
import nl.xillio.xill.plugins.excel.dataStructures.WorkbookType;
import nl.xillio.xill.plugins.excel.dataStructures.WorkbookWriter;
import nl.xillio.xill.plugins.excel.services.ExcelService;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Daan Knoope on 5-8-2015.
 */
public class WriteConstruct extends Construct {

	@Inject
	private ExcelService excelService;


	@Override public ConstructProcessor prepareProcess(ConstructContext context) {
		return new ConstructProcessor((a,b,c,d,e) -> process(excelService, a,b,c,d,e), new Argument("filename", ATOMIC), new Argument("extension", ATOMIC),
						new Argument("valueToWrite", ATOMIC), new Argument("RowNr", ATOMIC), new Argument("ColumnNr", ATOMIC));
	}

	static MetaExpression process(ExcelService excelService, MetaExpression filename, MetaExpression extension, MetaExpression valueToWrite,
					MetaExpression RowNr, MetaExpression ColumnNr){

		WorkbookWriter workbookWriter;

		switch(extension.getStringValue()) {
			case "xls":
				workbookWriter = new WorkbookWriter(WorkbookType.xls);
				break;
			case "xlsx":
				workbookWriter = new WorkbookWriter(WorkbookType.xlsx);
				break;
			default:
				throw new NotImplementedException("Only xls and xlsx are supported as file extensions for an excel file.");
		}


		workbookWriter.createSheetAndSetValues("Sheet1", Collections.singletonList(new CellData(valueToWrite.getStringValue(),
						new CellCoordinates(RowNr.getNumberValue().shortValue(),ColumnNr.getNumberValue().shortValue()))));

		return fromValue(workbookWriter.writeToFS(filename.getStringValue()));
	}


}
