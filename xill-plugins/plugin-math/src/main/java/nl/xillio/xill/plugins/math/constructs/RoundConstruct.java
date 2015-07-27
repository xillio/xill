package nl.xillio.xill.plugins.math.constructs;

import com.google.inject.Inject;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.plugins.math.services.math.MathOperations;

/**
 * The construct of the Round function which rounds a numbervalue.
 *
 * @author Ivor
 *
 */
public class RoundConstruct extends Construct {

	@Inject
	private MathOperations mathService;

	@Override
	public ConstructProcessor prepareProcess(final ConstructContext context) {
		return new ConstructProcessor(
		  value -> process(value, mathService),
		  new Argument("value", ATOMIC));
	}

	private static MetaExpression process(final MetaExpression value, final MathOperations math) {
		return fromValue(math.round(value.getNumberValue()));
	}

}
