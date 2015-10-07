package nl.xillio.xill.plugins.document.constructs;

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
import nl.xillio.xill.plugins.document.services.XillUDMService.Section;

/**
 *
 * Construct for removing documents or removing a version of a document.
 * if versionid is left empty the whole document will be removed otherwise only the given version is removed.
 * default value for section is "target" but can be set to "source".
 *
 * @author Sander Visser
 *
 */
public class RemoveConstruct extends Construct {

	@Inject
	XillUDMService udmService;

	@Override
	public ConstructProcessor prepareProcess(final ConstructContext context) {
		return new ConstructProcessor(
			(documentid, versionid, section) -> process(documentid, versionid, section, udmService),
			new Argument("documentid", ATOMIC),
			new Argument("versionid", fromValue("all"), ATOMIC),
			new Argument("section", fromValue("target"), ATOMIC));
	}

	static MetaExpression process(final MetaExpression documentid,
			final MetaExpression versionid, final MetaExpression section,
			final XillUDMService udmService) {
		// get string values of arguments
		String docid = documentid.getStringValue();
		String versid = versionid.getStringValue();
		Section sect = Section.of(section.getStringValue());

		try {
			udmService.remove(docid, versid, sect);
		} catch (DocumentNotFoundException | PersistenceException | IllegalArgumentException | VersionNotFoundException e) {
			throw new RobotRuntimeException(e.getMessage());
		}
		return NULL;
	}
}
