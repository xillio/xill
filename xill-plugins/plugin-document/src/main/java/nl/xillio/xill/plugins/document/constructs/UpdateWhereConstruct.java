package nl.xillio.xill.plugins.document.constructs;

import java.util.Map;

import nl.xillio.udm.exceptions.DocumentNotFoundException;
import nl.xillio.udm.exceptions.PersistenceException;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.document.exceptions.VersionNotFoundException;
import nl.xillio.xill.plugins.document.services.XillUDMService;
import nl.xillio.xill.plugins.document.util.DocumentUtil;

import org.bson.Document;

import com.google.inject.Inject;

/**
 * Construct for updating documents or versions using a filter.
 *
 * @author Geert Konijnendijk
 */
public class UpdateWhereConstruct extends Construct {

	@Inject
	XillUDMService udmService;

	@Override
	public ConstructProcessor prepareProcess(final ConstructContext context) {
		return new ConstructProcessor((filter, body, verId, sec) -> process(filter, body, verId, sec, udmService),
			new Argument("filter", OBJECT),
			new Argument("body", OBJECT),
			new Argument("versionId", NULL, ATOMIC),
			new Argument("section", fromValue("target"), ATOMIC));
	}

	static MetaExpression process(final MetaExpression filter, final MetaExpression body, final MetaExpression verId,
			final MetaExpression sec, final XillUDMService udmService) {
		// Get the string values of the arguments.
		String versionId = verId.getStringValue();
		String section = sec.getStringValue();

		String filterJson = filter.toString();
		Document filterBson = Document.parse(filterJson);

		Map<String, Map<String, Object>> bodyMap = DocumentUtil.expressionBodyToMap(body);

		long result;
		try {
			if (versionId.isEmpty()) {
				// Delete the whole entry
				result = udmService.updateWhere(filterBson, bodyMap);
			} else {
				result = udmService.updateWhere(filterBson, bodyMap, versionId, XillUDMService.Section.of(section));
			}
		} catch (VersionNotFoundException | DocumentNotFoundException | IllegalArgumentException e) {
			throw new RobotRuntimeException(e.getMessage(), e);
		} catch (PersistenceException e) {
			if (e.getCause() != null) {
				throw new RobotRuntimeException(e.getMessage() + ": " + e.getCause().getMessage(), e);
			} else {
				throw new RobotRuntimeException(e.getMessage(), e);
			}
		}

		return fromValue(result);
	}
}
