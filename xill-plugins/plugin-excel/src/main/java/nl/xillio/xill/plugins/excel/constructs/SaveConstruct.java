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

/**
 * Created by Daan Knoope on 14-8-2015.
 */
public class SaveConstruct extends Construct {
	@Inject
	private ExcelService service;

	@Override
	public ConstructProcessor prepareProcess(ConstructContext context) {
		return new ConstructProcessor(
						(a,b) -> process(service,context,a,b),
						new Argument("workbook", ATOMIC),
						new Argument("path", NULL, ATOMIC)
		);
	}

	static MetaExpression process(ExcelService service, ConstructContext context, MetaExpression workbookInput, MetaExpression path){

		XillWorkbook workbook = assertMeta(workbookInput, "parameter 'workbook'", XillWorkbook.class, "result of loadWorkbook or createWorkbook");
		if(path == NULL)
			return processOverwrite(service, workbook);
		else {
			File file = getFile(context.getRobotID(), path.getStringValue());
			return processToFolder(service, workbook, file);
		}
	}

	static MetaExpression processOverwrite(ExcelService service, XillWorkbook workbook){
		String returnValue = "";
		try{
			returnValue = service.save(workbook);
		} catch (IOException e) {
			throw new RobotRuntimeException(e.getMessage(), e);
		}
		return fromValue(returnValue);
	}

	static MetaExpression processToFolder(ExcelService service, XillWorkbook workbook, File file){
		String returnValue = "";
		try {
			returnValue = service.save(file, workbook);
		} catch (IOException e) {
			throw new RobotRuntimeException(e.getMessage(), e);
		}
		return fromValue(returnValue);
	}

}
