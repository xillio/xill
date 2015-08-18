package nl.xillio.xill.plugins.excel.constructs;

import com.google.inject.Inject;
import nl.xillio.xill.api.components.ExpressionDataType;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.excel.datastructures.XillWorkbook;
import nl.xillio.xill.plugins.excel.services.ExcelService;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Daan Knoope on 14-8-2015.
 */
public class RemoveSheetConstruct extends Construct {

	@Inject
	private ExcelService excelService;

	static MetaExpression process(ExcelService service, MetaExpression workbookInput, MetaExpression sheetName) {
		XillWorkbook workbook = assertMeta(workbookInput, "parameter 'workbook'", XillWorkbook.class, "result of loadWorkbook or createWorkbook");

		if (sheetName.getType() == ExpressionDataType.ATOMIC)
			return processSingle(service, workbook, sheetName);
		else if (sheetName.getType() == ExpressionDataType.LIST)
			return processMultiple(service, workbook, sheetName);
		else
			throw new RobotRuntimeException("No valid (list of) sheetnames provided");

	}

	static MetaExpression processSingle(ExcelService service, XillWorkbook workbook, MetaExpression sheetName) {
		try {
			service.removeSheet(workbook, sheetName.getStringValue());
		} catch (IllegalArgumentException e) {
			throw new RobotRuntimeException(e.getMessage(), e);
		}
		return fromValue(true);
	}

	static MetaExpression processMultiple(ExcelService service, XillWorkbook workbook, MetaExpression sheetNamesInput) {
		List<String> sheetNames = ((List<MetaExpression>) (sheetNamesInput.getValue())).stream().
						map(MetaExpression::getStringValue).collect(Collectors.toList());
		try {
			service.removeSheets(workbook, sheetNames);
		} catch (IllegalArgumentException e) {
			throw new RobotRuntimeException(e.getMessage(), e);
		}
		return fromValue(true);
	}

	@Override
	public ConstructProcessor prepareProcess(ConstructContext context) {
		return new ConstructProcessor(
						(a, b) -> process(excelService, a, b),
						new Argument("workbook", ATOMIC),
						new Argument("sheetName", ATOMIC, LIST));
	}

}
