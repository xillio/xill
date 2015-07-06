package nl.xillio.xill.plugins.string;

import org.apache.commons.lang3.StringEscapeUtils;

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
 * Encodes all special XML characters (<,>,&,\",') to their respective xml
 * entities.
 *
 * @author Sander
 *
 */
public class AmpersandEncodeConstruct implements Construct {

	@Override
	public String getName() {

		return "ampersandEncode";
	}

	@Override
	public ConstructProcessor prepareProcess(final ConstructContext context) {
		return new ConstructProcessor(AmpersandEncodeConstruct::process, new Argument("string"));
	}

	private static MetaExpression process(final MetaExpression stringVar) {

		if (stringVar.getType() != ExpressionDataType.ATOMIC) {
			throw new RobotRuntimeException("Expected atomic value.");
		}

		if (stringVar == ExpressionBuilder.NULL) {
			throw new RobotRuntimeException("Input cannot be null.");
		}

		String text = stringVar.getStringValue();

		return ExpressionBuilder.fromValue(StringEscapeUtils.escapeXml11(text));

	}
}
