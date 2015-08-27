package nl.xillio.xill.plugins.excel.constructs;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.plugins.excel.dataStructures.XillSheet;

/**
 * Construct to return the current amount of rows in a sheet.
 *
 * @author Daan Knoope
 */
public class RowCountConstruct extends Construct {

	/**
	 * Returns the number of rows in the provided sheet.
	 *
	 * @param sheet the {@link XillSheet} which' rows need to be counted
	 * @return a {@link MetaExpression} containing the number of rows in the sheet
	 */
	static MetaExpression process(MetaExpression sheet) {
		XillSheet Sheet = assertMeta(sheet, "parameter 'sheet'", XillSheet.class, "result of loadSheet or createSheet");
		return fromValue(Sheet.getRowLength());
	}

	@Override public ConstructProcessor prepareProcess(ConstructContext context) {
		return new ConstructProcessor(RowCountConstruct::process, new Argument("sheet", OBJECT));
	}

}
