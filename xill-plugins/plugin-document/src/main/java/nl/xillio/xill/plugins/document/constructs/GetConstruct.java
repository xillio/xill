package nl.xillio.xill.plugins.document.constructs;

import java.util.LinkedHashMap;

import com.google.inject.Inject;
import com.mongodb.MongoException;

import nl.xillio.udm.DocumentID;
import nl.xillio.udm.UDM;
import nl.xillio.udm.builders.DocumentBuilder;
import nl.xillio.udm.builders.DocumentHistoryBuilder;
import nl.xillio.udm.builders.DocumentRevisionBuilder;
import nl.xillio.udm.exceptions.DocumentNotFoundException;
import nl.xillio.udm.services.UDMService;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.document.services.ConversionService;

public class GetConstruct extends Construct {
	
	@Inject
	ConversionService conversion;
	
	@Override
	public ConstructProcessor prepareProcess(ConstructContext context) {
		return new ConstructProcessor((docId, verId, sec) -> process(docId, verId, sec, conversion),
				new Argument("documentId", ATOMIC),
				new Argument("versionId", fromValue("current"), ATOMIC),
				new Argument("section", fromValue("target"), ATOMIC));
	}
	
	private static MetaExpression process(final MetaExpression docId, final MetaExpression verId,
			final MetaExpression sec, final ConversionService conversion) {
		// Get the string values of the arguments.
		String versionId = verId.getStringValue();
		String section = sec.getStringValue();
		
		// The resulting map.
		LinkedHashMap<String, MetaExpression> result = null;
		
		// Connect to the UDM service.
		try (UDMService udm = UDM.connect()) {
			// Get the document and convert it to a map.
			DocumentID documentId = udm.get(docId.getStringValue());
			DocumentRevisionBuilder document = getVersion(getSourceOrTarget(udm.document(documentId), section), versionId);
			result = new LinkedHashMap<>(conversion.udmToMap(document));
			
			// Clear the cache.
			udm.release(documentId);
		}
		catch (MongoException e) {
			e.printStackTrace();
		}
		catch (DocumentNotFoundException d) { /* Do nothing, will return null. */ }
		
		return fromValue(result);
	}
	
	/**
	 * Get the source or target from a {@link DocumentBuilder}.
	 * @param builder The builder to get the section from.
	 * @param section The section to return. Can be either "source" or "target", and is case-insensitive.
	 * @return A {@link DocumentHistoryBuilder} of the given builder.
	 */
	private static DocumentHistoryBuilder getSourceOrTarget(DocumentBuilder builder, String section) {
		// Check if the section is source or target, else throw an exception.
		if (section.equalsIgnoreCase("source"))
			return builder.source();
		else if (section.equalsIgnoreCase("target"))
			return builder.target();
		else
			throw new RobotRuntimeException("The \"section\" argument is not in a valid format."
					+ "Valid values are \"source\" and \"target\".");
	}
	
	/**
	 * Get a certain revision from a {@link DocumentHistoryBuilder}.
	 * @param builder The builder to get the revision from.
	 * @param version The version to het from the builder.
	 * @return A {@link DocumentRevisionBuilder} that is the correct version of the document.
	 */
	private static DocumentRevisionBuilder getVersion(DocumentHistoryBuilder builder, String version) {
		// Check if the version is current or the version exists, else throw an exception.
		if (version.equalsIgnoreCase("current"))
			return builder.current();
		else if (builder.versions().contains(version))
			return builder.revision(version);
		else
			throw new RobotRuntimeException("The document does not contain a version [" + version + "].");
	}
}
