package nl.xillio.xill.plugins.math.constructs;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;

/**
 * The construct of the Round function which rounds a numbervalue.
 *
 * @author Ivor
 *
 */
public class RoundConstruct extends Construct {

	@Override
	public ConstructProcessor prepareProcess(final ConstructContext context) {
		return new ConstructProcessor(RoundConstruct::process, new Argument("value"));
	}

	private static MetaExpression process(final MetaExpression value) {
		Number number = value.getNumberValue();
		if (number instanceof Integer) {
			return fromValue(number.intValue());
		} else if (number instanceof Long) {
			return fromValue(number.longValue());
		} else if (number instanceof Float) {
			return fromValue(Math.round(number.floatValue()));
		} else {
			return fromValue(Math.round(number.doubleValue()));
		}
	}

}
