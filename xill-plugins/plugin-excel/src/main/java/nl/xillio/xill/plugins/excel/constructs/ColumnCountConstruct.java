package nl.xillio.xill.plugins.excel.constructs;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.plugins.excel.dataStructures.XillSheet;

/**
 * Construct that returns the current amount of columns in a sheet.
 *
 * @author Daan Knoope
 */
public class ColumnCountConstruct extends Construct {

	/**
	 * Returns the amount of columns in the provided sheet.
	 *
	 * @param sheet The sheet which' columns need to be counted
	 * @return a {@link MetaExpression} containing the number of columns in the sheet
	 */
	static MetaExpression process(MetaExpression sheet) {
		XillSheet Sheet = assertMeta(sheet, "parameter 'sheet'", XillSheet.class, "result of loadSheet or createSheet");
		return fromValue(Sheet.getColumnLength());
	}

	@Override public ConstructProcessor prepareProcess(ConstructContext context) {
		return new ConstructProcessor(ColumnCountConstruct::process, new Argument("sheet", OBJECT));
	}
}
