package nl.xillio.xill.plugins.excel.constructs;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.plugins.excel.dataStructures.XillCellRef;
import nl.xillio.xill.plugins.excel.dataStructures.XillSheet;

/**
 * Created by Daan Knoope on 7-8-2015.
 */
public class GetCellConstruct extends Construct {

	@Override
	public ConstructProcessor prepareProcess(ConstructContext context) {
		return new ConstructProcessor(
						GetCellConstruct::process,
						new Argument("sheet", OBJECT),
						new Argument("column", ATOMIC),
						new Argument("row", ATOMIC));
	}

	static MetaExpression process(MetaExpression sheetInput, MetaExpression column, MetaExpression row){
		//extract sheet from meta
		XillSheet sheet = assertMeta(sheetInput, "sheet", XillSheet.class, "Excel Sheet");
		//test if column is in numeric or alphabetic notation
		XillCellRef cell;
		if(Double.isNaN(column.getNumberValue().doubleValue())) //Then String Representation
			cell = new XillCellRef(column.getStringValue(), row.getNumberValue().intValue());
		else
			cell = new XillCellRef((column.getNumberValue().intValue()), row.getNumberValue().intValue());
		Object cellValue = sheet.getCellValue(cell);
		return parseObject(cellValue);
	}
}
