package nl.xillio.xill.plugins.document.constructs;

import java.util.Map;

import nl.xillio.udm.exceptions.DocumentNotFoundException;
import nl.xillio.udm.exceptions.PersistenceException;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.components.MetaExpressionIterator;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.document.exceptions.VersionNotFoundException;
import nl.xillio.xill.plugins.document.services.XillUDMService;
import nl.xillio.xill.plugins.document.services.XillUDMService.Section;

import org.bson.Document;

import com.google.inject.Inject;
/**
 * Construct for getting all documents that match a certain filter.
 *
 * @author Thomas Biesaart
 */
public class FindWhereConstruct extends Construct {

	@Inject
	XillUDMService udmService;

	@Override
	public ConstructProcessor prepareProcess(ConstructContext context) {
		return new ConstructProcessor((filter, verId, sec) -> process(filter, verId, sec, udmService),
			new Argument("filter", OBJECT),
			new Argument("versionId", fromValue("current"), ATOMIC),
			new Argument("section", fromValue("target"), ATOMIC));
	}

	static MetaExpression process(final MetaExpression filter, final MetaExpression verId,
																final MetaExpression sec, final XillUDMService udmService) {
		// Get the string values of the arguments.
		String filterJson = filter.getStringValue();
		String versionId = verId.getStringValue();
		Section section = Section.of(sec.getStringValue());

		Document filterBson = Document.parse(filterJson);


		Iterable<Map<String, Map<String, Object>>> result;
		try {
			result = udmService.findWhere(filterBson, versionId, section);
		} catch (VersionNotFoundException | DocumentNotFoundException | IllegalArgumentException e) {
			throw new RobotRuntimeException(e.getMessage(), e);
		} catch (PersistenceException e) {
			throw new RobotRuntimeException(e.getMessage(), e);
		}

		MetaExpression metaExpression = fromValue("findWhere[" + filterJson + "]");
		metaExpression.storeMeta(MetaExpressionIterator.class, new MetaExpressionIterator<>(result.iterator(), Construct::parseObject));
		return metaExpression;
	}
}
