package nl.xillio.xill.plugins.excel.constructs;

import com.google.inject.Inject;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.excel.dataStructures.XillWorkbook;
import nl.xillio.xill.plugins.excel.services.ExcelService;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;

/**
 * Created by Daan Knoope on 11-8-2015.
 */
public class CreateWorkbookConstruct extends Construct {

	@Inject
	private ExcelService excelService;

	static MetaExpression process(ExcelService excelService, ConstructContext context, MetaExpression filePath) {
		String inputPath = filePath.getStringValue();
		File file = getFile(context.getRobotID(), inputPath);
		XillWorkbook workbook;
		try {
			workbook = excelService.createWorkbook(file);
		} catch (FileAlreadyExistsException e) {
			throw new RobotRuntimeException(e.getMessage(), e);
		} catch (IOException e) {
			throw new RobotRuntimeException("Cannot write to the supplied path", e);
		}

		String toReturn = workbook.getFileString();
		MetaExpression expr = fromValue(toReturn);
		expr.storeMeta(XillWorkbook.class, workbook);
		return expr;
	}

	@Override
	public ConstructProcessor prepareProcess(ConstructContext context) {
		return new ConstructProcessor(
						a -> process(excelService, context, a),
						new Argument("filePath", ATOMIC));
	}
}
