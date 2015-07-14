package nl.xillio.xill.plugins.date.constructs;

import nl.xillio.xill.api.components.ExpressionBuilder;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;

/**
 *
 *
 * Converts the format of the date with the provided format.
 *
 * @author Sander
 *
 */
public class FormatConstruct implements Construct {

    @Override
    public String getName() {

	return "format";
    }

    @Override
    public ConstructProcessor prepareProcess(final ConstructContext context) {

	return new ConstructProcessor((dateVar, formatVar, timezoneVar) -> process(context, dateVar, formatVar, timezoneVar), new Argument("date"), new Argument("format", ExpressionBuilder.NULL),
		new Argument("timezone", ExpressionBuilder.NULL));
    }

    private static MetaExpression process(final ConstructContext context, final MetaExpression dateVar, final MetaExpression formatVar, final MetaExpression timezoneVar) {

	return null;

    }
}
