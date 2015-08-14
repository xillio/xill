package nl.xillio.xill.plugins.excel.constructs;

import com.google.inject.Inject;
import nl.xillio.xill.api.behavior.BooleanBehavior;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.excel.dataStructures.XillCellRef;
import nl.xillio.xill.plugins.excel.dataStructures.XillSheet;
import nl.xillio.xill.plugins.excel.services.ExcelService;

import java.util.regex.Pattern;

/**
 * Created by Daan Knoope on 13-8-2015.
 */
public class SetCellConstruct extends Construct {

	@Inject
	private ExcelService excelService;

	@Override public ConstructProcessor prepareProcess(ConstructContext context) {
		return new ConstructProcessor((a,b,c,d) -> process(a,b,c,d),
						new Argument("sheet", OBJECT),
						new Argument("column", ATOMIC),
						new Argument("row", ATOMIC),
						new Argument("value", ATOMIC));
	}

	public static MetaExpression process(MetaExpression sheetInput, MetaExpression column, MetaExpression row, MetaExpression value){
		XillSheet sheet = assertMeta(sheetInput, "sheet", XillSheet.class, "Excel Sheet");
		boolean result;

		if(sheet == null)
			throw new RobotRuntimeException("Incorrect sheet provided");
		if(sheet.isReadonly())
			throw new RobotRuntimeException("Cannot write on sheet: sheet is read-only");
		if(!isNumeric(row))
			throw new RobotRuntimeException("Wrong notation for row \"" + row.getStringValue() + "\", should be numeric (e.g. 12)");
		if(isNumericXORAlphabetic(column))
			throw new RobotRuntimeException("Wrong notation for column \"" + column.getStringValue() + "\", should be numerical or alphabetical notation (e.g. AB or 12)");

		result = setValue(value,sheet,getCellRef(column, row));
		return fromValue(result);

	}

	private static boolean setValue(MetaExpression value, XillSheet sheet, XillCellRef cellRef){
		if(isNumeric(value)) {
			sheet.setCellValue(cellRef, value.getNumberValue().doubleValue());
			return true;
		}
		else if(value.getValue() instanceof BooleanBehavior) { // DO NOT REPEAT ANYWHERE ELSE, WAS UNAVOIDABLE :-(
			sheet.setCellValue(cellRef, value.getBooleanValue());
			return true;
		}
		else {
			sheet.setCellValue(cellRef, value.getStringValue());
			return true;
		}
	}

	private static XillCellRef getCellRef(MetaExpression column, MetaExpression row){
		if(isNumeric(column))
			return new XillCellRef(column.getNumberValue().intValue(), row.getNumberValue().intValue());
		return new XillCellRef(column.getStringValue(), row.getNumberValue().intValue());
	}

	private static boolean isNumeric(MetaExpression expression){
		return !Double.isNaN(expression.getNumberValue().doubleValue());
	}

	private static boolean isNumericXORAlphabetic(MetaExpression expression){
		return Pattern.matches("[a-zA-Z]*|[0-9]*", expression.getStringValue());
	}

}
