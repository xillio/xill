package nl.xillio.xill.plugins.excel.constructs;

import com.google.inject.Inject;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.components.Robot;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.excel.services.ExcelService;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;

/**
 * Created by Daan Knoope on 6-8-2015.
 */

public class LoadWorkbookConstruct extends Construct {

	@Inject
	private ExcelService excelService;


	@Override public ConstructProcessor prepareProcess(ConstructContext context) {
		return new ConstructProcessor((a) -> process(excelService, context, a), new Argument("filePath", ATOMIC));
	}

	static MetaExpression process(ExcelService excelService, ConstructContext context, MetaExpression filePath){
		String path = filePath.getStringValue();
		File file = getFile(context.getRobotID(), path);
		String pathToFile = null;
		Workbook workbook = null;
		try {
			pathToFile = excelService.getFilePath(file);
			workbook = excelService.loadWorkbook(path,file);
		} catch (IOException e) {
			throw new RobotRuntimeException(e.getMessage() + ": Could not load file");
		}
		MetaExpression result = fromValue("Excel Workbook [" + pathToFile + "]");
		result.storeMeta(workbook);
		return result;
	}
}
