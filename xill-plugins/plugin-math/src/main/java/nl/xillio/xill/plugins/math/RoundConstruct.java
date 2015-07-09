package nl.xillio.xill.plugins.math;


import java.io.InputStream;

import nl.xillio.xill.api.components.ExpressionBuilder;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.api.construct.HelpComponent;

/**
 * The construct of the Round function which rounds a numbervalue.
 * @author Ivor
 *
 */
public class RoundConstruct  implements HelpComponent {

	@Override
	public String getName() {
		return "round";
	}

	@Override
	public ConstructProcessor prepareProcess(ConstructContext context) {
		return new ConstructProcessor(RoundConstruct::process, new Argument("value"));
	}
	
	private static MetaExpression process(final MetaExpression value)
	{
		Number number = value.getNumberValue();
		if (number instanceof Integer) {
			return ExpressionBuilder.fromValue(number.intValue());
		} else if (number instanceof Long) {
			return ExpressionBuilder.fromValue(number.longValue());
		} else if (number instanceof Float) {
			return ExpressionBuilder.fromValue(Math.round(number.floatValue()));
		} else {
			return ExpressionBuilder.fromValue(Math.round(number.doubleValue()));
		}
	}

	@Override
	public InputStream openDocumentationStream() {
		return getClass().getResourceAsStream("/helpfiles/round.xml");
	}

}