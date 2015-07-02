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
 * Decodes all ampersand-encoded characters in the provided text.
 *
 * @author Sander
 *
 */
public class AmpersandDecodeConstruct implements Construct {

	@Override
	public String getName() {

		return "ampersandDecode";
	}

	@Override
	public ConstructProcessor prepareProcess(final ConstructContext context) {
		return new ConstructProcessor(AmpersandDecodeConstruct::process, new Argument("string"),
				new Argument("passes",ExpressionBuilder.fromValue(1)));
	}

	private static MetaExpression process(final MetaExpression stringVar, final MetaExpression passesVar) {

		if (stringVar.getType() != ExpressionDataType.ATOMIC || passesVar.getType() != ExpressionDataType.ATOMIC) {
			throw new RobotRuntimeException("Expected atomic value.");
		}

		if (stringVar == ExpressionBuilder.NULL || passesVar == ExpressionBuilder.NULL) {
			throw new RobotRuntimeException("Input cannot be null.");
		}

		String text = stringVar.getStringValue();

		int passes = passesVar.getNumberValue().intValue();

		for (int i = 0; i < passes; i++) {
			text = StringEscapeUtils.unescapeXml(text);
		}

		return ExpressionBuilder.fromValue(text);

	}
}
