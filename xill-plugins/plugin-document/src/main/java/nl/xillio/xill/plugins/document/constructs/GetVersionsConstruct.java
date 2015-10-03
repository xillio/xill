package nl.xillio.xill.plugins.document.constructs;

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

import java.util.List;
import java.util.Map;

/**
 * Construct for getting a list of versions on a document.
 * 
 * @author Thomas Biesaart
 *
 */
public class GetVersionsConstruct extends Construct {

	@Inject
	XillUDMService udmService;

	@Override
	public ConstructProcessor prepareProcess(ConstructContext context) {
		return new ConstructProcessor((docId, sec) -> process(docId, sec, udmService),
			new Argument("documentId", ATOMIC),
			new Argument("section", fromValue("target"), ATOMIC));
	}

	static MetaExpression process(final MetaExpression docId, final MetaExpression sec, final XillUDMService udmService) {
		// Get the string values of the arguments.
		String documentId = docId.getStringValue();
		String section = sec.getStringValue();

		List<String> result;
		try {
			result = udmService.getVersions(documentId, section);
		} catch (DocumentNotFoundException | IllegalArgumentException e) {
			throw new RobotRuntimeException(e.getMessage(), e);
		}

		return parseObject(result);
	}
}
