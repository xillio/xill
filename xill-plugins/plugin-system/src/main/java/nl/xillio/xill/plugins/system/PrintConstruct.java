package nl.xillio.xill.plugins.system;

import nl.xillio.xill.api.components.ExpressionBuilder;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;

import org.apache.log4j.Logger;

/**
 * Prints the provided text to the log. Optional the log level can be set: debug, info (default), warning, error.
 */
public class PrintConstruct implements Construct {

	@Override
	public String getName() {
		return "print";
	}

	@Override
	public ConstructProcessor prepareProcess(final ConstructContext context) {
		return new ConstructProcessor((text, level) ->
		process(text, level, context.getRootLogger()),
		new Argument("text", ExpressionBuilder.fromValue("")),
		new Argument("loglevel", ExpressionBuilder.fromValue("info")));
	}

	private static MetaExpression process(final MetaExpression textVar, final MetaExpression logLevel, final Logger robotLog) {
		String level = logLevel.getStringValue();

		String text = textVar.getStringValue();

		if (level.toLowerCase().equals("debug")) {
			robotLog.debug(text);
		} else if (level.toLowerCase().startsWith("warn")) {
			robotLog.warn(text);
		} else if (level.toLowerCase().equals("error")) {
			robotLog.error(text);
		} else {
			robotLog.info(text);
		}

		return ExpressionBuilder.NULL;
	}
}
