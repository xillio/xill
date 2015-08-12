package nl.xillio.xill.plugins.database.constructs;

import com.google.inject.Inject;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.plugins.database.services.EscapeService;

/**
 * Returns an SQL-escaped string.
 * 
 * @author Sander Visser
 *
 */
public class EscapeConstruct extends Construct {

	@Inject
	private EscapeService escapeService;
	
	@Override
	public ConstructProcessor prepareProcess(final ConstructContext context) {
		return new ConstructProcessor(
			(value) -> process(value,escapeService),
			new Argument("value", ATOMIC));
	}
	
	static MetaExpression process(final MetaExpression value,final EscapeService escapeService){
		String input = value.getStringValue();
		return fromValue(escapeService.escape(input));
		
	}

}
