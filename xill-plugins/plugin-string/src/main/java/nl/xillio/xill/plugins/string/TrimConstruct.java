package nl.xillio.xill.plugins.string;

import java.util.ArrayList;
import java.util.List;

import nl.xillio.xill.api.components.ExpressionBuilder;
import nl.xillio.xill.api.components.ExpressionDataType;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.api.errors.RobotRuntimeException;

/**
 *
 * Returns the trimmed string. </br>
 * If the input is a list it returns a list where every element is
 * trimmed. </br>
 * If optional parameter 'internal' is set to true the routine will also replace
 * slack whitespace inside the string with a single space. </br>
 * </br>
 *
 *
 * @author Sander
 *
 */
public class TrimConstruct implements Construct {

	@Override
	public String getName() {

		return "trim";
	}

	@Override
	public ConstructProcessor prepareProcess(final ConstructContext context) {
		return new ConstructProcessor(TrimConstruct::process, new Argument("string"),
				new Argument("internal", ExpressionBuilder.FALSE));
	}

	private static MetaExpression process(final MetaExpression string, final MetaExpression internal) {

		if (internal.getType() != ExpressionDataType.ATOMIC) {
			throw new RobotRuntimeException("Expected atomic value.");
		}

		if (string == ExpressionBuilder.NULL || internal == ExpressionBuilder.NULL) {
			throw new RobotRuntimeException("Input cannot be null.");
		}

		if (string.getType() == ExpressionDataType.LIST) {

			List<MetaExpression> stringList = new ArrayList<>();

			@SuppressWarnings("unchecked")
			List<MetaExpression> list = (List<MetaExpression>)string.getValue();
			
			
			list.forEach(str -> {
				if (!str.isNull()) {
					stringList.add(doTrimming(str, internal));
					
				}
			});
			return ExpressionBuilder.fromValue(stringList);

		}
		if (string.getType() == ExpressionDataType.ATOMIC) {
			return ExpressionBuilder.fromValue(doTrimming(string, internal).getStringValue());
		}
		return null;

	}

	private static MetaExpression doTrimming(final MetaExpression string, final MetaExpression internal) {
		String text = string.getStringValue();

		text = text.replaceAll("\u00A0", " ");
		text = text.trim();

		if (internal.getBooleanValue()) {
			text = text.replaceAll("[\\s]+", " ");
		}
		return ExpressionBuilder.fromValue(text);
	}
}
