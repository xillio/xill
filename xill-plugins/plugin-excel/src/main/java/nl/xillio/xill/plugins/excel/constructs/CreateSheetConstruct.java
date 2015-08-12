package nl.xillio.xill.plugins.excel.constructs;

import com.google.inject.Inject;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.plugins.excel.services.ExcelService;

/**
 * Created by Daan Knoope on 12-8-2015.
 */
public class CreateSheetConstruct extends Construct {
	@Inject
	private ExcelService excelService;

	@Override public ConstructProcessor prepareProcess(ConstructContext context) {
		return null;
	}

	static MetaExpression process(){

	}
}
