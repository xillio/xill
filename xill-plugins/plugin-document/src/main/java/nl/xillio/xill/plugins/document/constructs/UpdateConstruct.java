package nl.xillio.xill.plugins.document.constructs;

import java.util.Map;

import com.google.inject.Inject;

import nl.xillio.udm.exceptions.DocumentNotFoundException;
import nl.xillio.udm.exceptions.ModelException;
import nl.xillio.udm.exceptions.PersistenceException;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.document.exceptions.VersionNotFoundException;
import nl.xillio.xill.plugins.document.services.XillUDMService;
import nl.xillio.xill.plugins.document.services.XillUDMService.Section;
import nl.xillio.xill.plugins.document.util.DocumentUtil;
/**
 * Construct for updating a version of a document.
 * 
 * @author Geert Konijnendijk
 *
 */
public class UpdateConstruct extends Construct {

	@Inject
	XillUDMService udmService;

	@Override
	public ConstructProcessor prepareProcess(ConstructContext context) {
		return new ConstructProcessor((docId, body, verId, sec) -> process(docId, body, verId, sec, udmService),
			new Argument("documentId", ATOMIC),
			new Argument("body", OBJECT),
			new Argument("versionId", fromValue("current"), ATOMIC),
			new Argument("section", fromValue("target"), ATOMIC));
	}

	static MetaExpression process(final MetaExpression docId, final MetaExpression body, final MetaExpression verId,
			final MetaExpression sec, final XillUDMService udmService) {
		// Get the string values of the arguments.
		String documentId = docId.getStringValue();
		Map<String, Map<String, Object>> parsedBody = DocumentUtil.expressionBodyToMap(body);
		String versionId = verId.getStringValue();
		Section section = Section.of(sec.getStringValue());

		try {
			udmService.update(documentId, parsedBody, versionId, section);
		} catch (ModelException | PersistenceException | VersionNotFoundException | DocumentNotFoundException e) {
			throw new RobotRuntimeException(e.getMessage(), e);
		}

		return NULL;
	}
}
