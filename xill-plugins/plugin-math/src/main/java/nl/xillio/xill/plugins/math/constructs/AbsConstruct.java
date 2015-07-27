package nl.xillio.xill.plugins.math.constructs;

import com.google.inject.Inject;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.plugins.math.services.math.MathOperations;

/**
 * The construct for the Abs function which can give the absolute value of a
 * number.
 *
 * @author Ivor
 *
 */
public class AbsConstruct extends Construct {

	@Inject
	private MathOperations mathService;
	
	@Override
	public ConstructProcessor prepareProcess(final ConstructContext context) {
		return new ConstructProcessor(value -> process(value, mathService), new Argument("value"));
	}

	private static MetaExpression process(final MetaExpression value, final MathOperations math) {
		if (value == NULL) {
			return NULL;
		}

		return fromValue(math.abs(value.getNumberValue()));
	}

}
