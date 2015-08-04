package nl.xillio.xill.plugins.excel.constructs;

import com.google.inject.Inject;
import nl.xillio.xill.api.components.Expression;
import nl.xillio.xill.api.components.ExpressionDataType;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.api.errors.NotImplementedException;
import nl.xillio.xill.plugins.excel.services.ExcelService;
import nl.xillio.xill.plugins.excel.utils.ExcelData;

import java.util.List;

/**
 * Created by Daan Knoope on 4-8-2015.
 */
public class TestConstruct extends Construct{
	
	@Inject
	private ExcelService excelService;

	@Override public ConstructProcessor prepareProcess(ConstructContext context) {
		return new ConstructProcessor((a) -> process(excelService, a), new Argument("daan", ATOMIC, LIST));
	}

	static MetaExpression process(ExcelService excelService, MetaExpression daan){
		String result = "";

		ExcelData data = new ExcelData();

		daan.storeMeta(data);


		ExcelData fetched = daan.getMeta(ExcelData.class);


		return fromValue(result);
	}
}
