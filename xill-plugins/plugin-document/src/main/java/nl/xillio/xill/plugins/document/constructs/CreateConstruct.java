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

public class CreateConstruct extends Construct {

	@Inject
	XillUDMService udmService;
	
	@Override
	public ConstructProcessor prepareProcess(ConstructContext context) {
		return new ConstructProcessor((conType, body) -> process(conType, body, udmService),
				new Argument("contentType", ATOMIC),
				new Argument("body", OBJECT));
	}
	
	private static MetaExpression process(final MetaExpression conType, final MetaExpression body, final XillUDMService udmService) {
		String contentType = conType.getStringValue();
		Object bodyObj = extractValue(body);
		
		// Get the body as a map (which we know it is, because it's an object).
		@SuppressWarnings("unchecked")
		Map<String, Object> firstMap = (Map<String, Object>)bodyObj;
		
		// Check if all values in the map are also maps.
		for (Object value : firstMap.values())
			if (!(value instanceof Map))
				throw new RobotRuntimeException("The body has an invalid format."
						+ "It should be an object containing sub-objects for each decorator.");
		
		// We now know that the body is the correct format, so it's safe to cast it.
		@SuppressWarnings("unchecked")
		Map<String, Map<String, Object>> bodyMap = (Map<String, Map<String, Object>>) bodyObj;
		
		// Try to create the document.
		try {
			return fromValue(udmService.create(contentType, bodyMap).get());
		} catch (PersistenceException e) {
			throw new RobotRuntimeException(e.getMessage(), e);
		}
	}
}
