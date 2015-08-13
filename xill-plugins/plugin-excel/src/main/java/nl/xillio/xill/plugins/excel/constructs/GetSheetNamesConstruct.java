package nl.xillio.xill.plugins.excel.constructs;

import com.google.inject.Inject;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.*;
import nl.xillio.xill.plugins.excel.dataStructures.XillWorkbook;
import nl.xillio.xill.plugins.excel.services.ExcelService;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Daan Knoope on 13-8-2015.
 */
public class GetSheetNamesConstruct extends Construct {

	@Inject
	private ExcelService excelService;

	@Override public ConstructProcessor prepareProcess(ConstructContext context) {
		return new ConstructProcessor(a -> process(a),
						new Argument("workbook", ATOMIC));
	}

	static MetaExpression process(MetaExpression workbookInput){
		XillWorkbook workbook = assertMeta(workbookInput, "parameter 'workbook'",
						XillWorkbook.class, "result of loadWorkbook or createWorkbook");
		List<String> workbookNames = workbook.getSheetNames();
		List<MetaExpression> toReturn = workbookNames.stream().map(ExpressionBuilderHelper::fromValue).collect(Collectors.toList());
		return fromValue(toReturn);
	}
}
