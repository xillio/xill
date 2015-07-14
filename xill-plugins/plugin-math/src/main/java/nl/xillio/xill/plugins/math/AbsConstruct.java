package nl.xillio.xill.plugins.math;

import java.io.InputStream;

import nl.xillio.xill.api.components.ExpressionBuilder;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.api.construct.HelpComponent;

/**
 * The construct for the Abs function which can give the absolute value of a
 * number.
 *
 * @author Ivor
 *
 */
public class AbsConstruct implements HelpComponent {

    @Override
    public String getName() {
	return "abs";
    }

    @Override
    public ConstructProcessor prepareProcess(final ConstructContext context) {
	return new ConstructProcessor(AbsConstruct::process, new Argument("value"));
    }

    private static MetaExpression process(final MetaExpression value) {
	if (value == ExpressionBuilder.NULL) {
	    return ExpressionBuilder.NULL;
	}

	Number number = value.getNumberValue();
	if (number instanceof Integer) {
	    return ExpressionBuilder.fromValue(Math.abs(number.intValue()));
	} else if (number instanceof Long) {
	    return ExpressionBuilder.fromValue(Math.abs(number.longValue()));
	} else if (number instanceof Float) {
	    return ExpressionBuilder.fromValue(Math.abs(number.floatValue()));
	} else {
	    return ExpressionBuilder.fromValue(Math.abs(number.doubleValue()));
	}
    }

    @Override
    public InputStream openDocumentationStream() {
	return getClass().getResourceAsStream("/helpfiles/abs.xml");
    }

}
