package nl.xillio.xill.plugins.document.constructs;

import java.util.Map;

import com.google.inject.Inject;

import nl.xillio.udm.exceptions.DocumentNotFoundException;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.document.exceptions.VersionNotFoundException;
import nl.xillio.xill.plugins.document.services.XillUDMService;

/**
 * Construct for getting all decorators of a specific document version as one object.
 * 
 * @author Geert Konijnendijk
 * @author Luca Scalzotto
 *
 */
public class GetConstruct extends Construct {

	@Inject
	XillUDMService udmService;

	@Override
	public ConstructProcessor prepareProcess(ConstructContext context) {
		return new ConstructProcessor((docId, verId, sec) -> process(docId, verId, sec, udmService),
			new Argument("documentId", ATOMIC),
			new Argument("versionId", fromValue("current"), ATOMIC),
			new Argument("section", fromValue("target"), ATOMIC));
	}

	private static MetaExpression process(final MetaExpression docId, final MetaExpression verId,
			final MetaExpression sec, final XillUDMService udmService) {
		// Get the string values of the arguments.
		String documentId = docId.getStringValue();
		String versionId = verId.getStringValue();
		String section = sec.getStringValue();

		Map<String, Map<String, Object>> result = null;
		try {
			result = udmService.get(documentId, versionId, section);
		} catch (VersionNotFoundException e) {
			throw new RobotRuntimeException(e.getMessage(), e);
		} catch (DocumentNotFoundException e) {
			throw new RobotRuntimeException(e.getMessage(), e);
		} catch (IllegalArgumentException e) {
			throw new RobotRuntimeException(e.getMessage(), e);
		}

		return parseObject(result);
	}
}
