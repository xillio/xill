package nl.xillio.xill.plugins.document.constructs;

import java.util.Map;

import com.google.inject.Inject;

import nl.xillio.udm.exceptions.PersistenceException;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.document.services.XillUDMService;
import nl.xillio.xill.plugins.document.util.DocumentUtil;

/**
 * Construct for creating and inserting an object into the database.
 * 
 * @author Luca Scalzotto
 *
 */
public class CreateConstruct extends Construct {

	@Inject
	XillUDMService udmService;
	
	@Override
	public ConstructProcessor prepareProcess(ConstructContext context) {
		return new ConstructProcessor((conType, body) -> process(conType, body, udmService),
				new Argument("contentType", ATOMIC),
				new Argument("body", OBJECT));
	}
	
	static MetaExpression process(final MetaExpression conType, final MetaExpression body, final XillUDMService udmService) {
		String contentType = conType.getStringValue();
		
		// Get the body as a map of maps
		Map<String, Map<String, Object>> bodyMap = DocumentUtil.expressionBodyToMap(body);
		
		// Try to create the document.
		try {
			return fromValue(udmService.create(contentType, bodyMap).get());
		} catch (PersistenceException e) {
			throw new RobotRuntimeException(e.getMessage(), e);
		}
	}
}
