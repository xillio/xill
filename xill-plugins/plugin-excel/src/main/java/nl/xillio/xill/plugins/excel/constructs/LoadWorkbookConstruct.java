package nl.xillio.xill.plugins.excel.constructs;

import com.google.inject.Inject;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.components.Robot;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.excel.dataStructures.XillWorkbook;
import nl.xillio.xill.plugins.excel.services.ExcelService;
import nl.xillio.xill.services.inject.InjectorUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.text.ParseException;

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
		String workbookText = null;
		XillWorkbook workbook = null;
		try {
			workbook = excelService.loadWorkbook(path,file);
			workbookText = workbook.getFileString();
		} catch (IllegalArgumentException e) {
			throw new RobotRuntimeException("Path does not lead to an xls or xlsx Microsoft Excel file");
		} catch(FileNotFoundException e){
			throw new RobotRuntimeException("There is no file at the given path");
		} catch(IOException e){
			throw new RobotRuntimeException("File could not be read", e);
		}catch (ParseException e){
			throw new RobotRuntimeException("File cannot be opened as Excel Workbook");
		}
		if(workbook.isReadonly())
			context.getRootLogger().warn("Opened in read-only mode.");



		MetaExpression result = fromValue(workbookText);
		result.storeMeta(XillWorkbook.class, workbook);
		return result;
	}
}
