package nl.xillio.xill.plugins.excel.constructs;

import com.google.inject.Inject;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.excel.datastructures.XillWorkbook;
import nl.xillio.xill.plugins.excel.services.ExcelService;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InvalidObjectException;

/**
 * Created by Daan Knoope on 6-8-2015.
 */

public class LoadWorkbookConstruct extends Construct {

	@Inject
	private ExcelService excelService;

	static MetaExpression process(ExcelService excelService, ConstructContext context, MetaExpression filePath) {

		String path = filePath.getStringValue();
		File file = getFile(context.getRobotID(), path);
		String workbookText = null;
		XillWorkbook workbook = null;
		try {
			workbook = excelService.loadWorkbook(file);
			workbookText = workbook.getFileString();
		} catch (IllegalArgumentException e) {
			throw new RobotRuntimeException("Path does not lead to an xls or xlsx Microsoft Excel file");
		} catch (FileNotFoundException e) {
			throw new RobotRuntimeException("There is no file at the given path", e);
		} catch (InvalidObjectException e) {
			throw new RobotRuntimeException(e.getMessage(), e);
		} catch (IOException e) {
			throw new RobotRuntimeException("File could not be opened", e);
		}
		if (workbook.isReadonly())
			context.getRootLogger().warn("Opened in read-only mode.");

		MetaExpression result = fromValue(workbookText);
		result.storeMeta(XillWorkbook.class, workbook);
		return result;
	}

	@Override
	public ConstructProcessor prepareProcess(ConstructContext context) {
		return new ConstructProcessor(
						a -> process(excelService, context, a),
						new Argument("filePath", ATOMIC));
	}
}
