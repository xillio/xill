package nl.xillio.xill.plugins.document.constructs;

import org.bson.Document;

import com.google.inject.Inject;

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

/**
 * Construct for removing documents or versions using a filter.
 *
 * @author Thomas Biesaart
 */
public class RemoveWhereConstruct extends Construct {

	@Inject
	XillUDMService udmService;

	@Override
	public ConstructProcessor prepareProcess(ConstructContext context) {
		return new ConstructProcessor((filter, verId, sec) -> process(filter, verId, sec, udmService),
			new Argument("filter", OBJECT),
			new Argument("versionId", NULL, ATOMIC),
			new Argument("section", fromValue("target"), ATOMIC));
	}

	static MetaExpression process(final MetaExpression filter, final MetaExpression verId,
			final MetaExpression sec, final XillUDMService udmService) {
		// Get the string values of the arguments.
		String versionId = verId.getStringValue();
		String section = sec.getStringValue();

		String filterJson = filter.toString();
		Document filterBson = Document.parse(filterJson);

		long result;
		try {
			if (versionId.isEmpty()) {
				// Delete the whole entry
				result = udmService.removeWhere(filterBson);
			} else {
				result = udmService.removeWhere(filterBson, versionId, XillUDMService.Section.of(section));
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
