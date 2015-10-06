package nl.xillio.xill.plugins.document.services;

import java.util.List;
import java.util.Map;

import nl.xillio.udm.DocumentID;
import nl.xillio.udm.UDM;
import nl.xillio.udm.builders.DocumentBuilder;
import nl.xillio.udm.builders.DocumentHistoryBuilder;
import nl.xillio.udm.builders.DocumentRevisionBuilder;
import nl.xillio.udm.exceptions.ModelException;
import nl.xillio.udm.exceptions.PersistenceException;
import nl.xillio.udm.services.UDMService;
import nl.xillio.xill.plugins.document.exceptions.VersionNotFoundException;

import com.google.inject.Inject;

/**
 * Implementation of {@link XillUDMService}.
 * 
 * @author Geert Konijnendijk
 *
 */
public class XillUDMServiceImpl implements XillUDMService {

	private final ConversionService conversionService;

	/**
	 * Create a new XillUDMService and set the services it depends on manually
	 *
	 * @param conversionService
	 *        The {@link ConversionService}
	 */
	@Inject
	XillUDMServiceImpl(ConversionService conversionService) {
		this.conversionService = conversionService;
	}

	/**
	 * Connect to the UDM
	 * 
	 * @return Service to do UDM operations on
	 */
	protected UDMService connect() {
		return UDM.connect();
	}

	@Override
	public Map<String, Map<String, Object>> get(String documentId, String versionId, String section) {
		// Get the document and convert it to a map.
		try (UDMService udmService = connect()) {
			DocumentID docId = udmService.get(documentId);
			DocumentRevisionBuilder document = getVersion(docId, versionId, section, udmService);
			Map<String, Map<String, Object>> result = conversionService.udmToMap(document);

			// Clear the cache.
			udmService.release(docId);

			return result;
		}
	}

	@Override
	public void update(String documentId, Map<String, Map<String, Object>> body, String versionId, String section) throws PersistenceException {
		try (UDMService udmService = connect()) {
			DocumentID docId = udmService.get(documentId);
			DocumentRevisionBuilder document = getVersion(docId, versionId, section, udmService);

			// Verify that all decorator and fields are the same, else some decorators or fields might not be updated
			checkDecorators(body, document);

			// Input into the builder
			conversionService.mapToUdm(body, document);

			// Persist and clear from cache
			udmService.persist(docId);
		}
	}

	/**
	 * Check that the builder and object have exactly the same decorators and that these decorators have the same fields.
	 * 
	 * @param object
	 *        Object representing a document version
	 * @param builder
	 *        Builder already containing data
	 * @throws ModelException
	 *         When object and builder do not correspond
	 */
	private void checkDecorators(Map<String, Map<String, Object>> object, DocumentRevisionBuilder builder) {
		// Immediately throw an exception if the number of decorators does not correspond
		List<String> decorators = builder.decorators();
		if (object.size() != decorators.size())
		{
			throw new ModelException("Body and retrieved document version do not have the same number of decorators");
		}
		// Check if decorator names correspond one-to-one
		for (String decoratorName : decorators) {
			// The decorators correspond one-to-one if the object contains the same names as the builder
			// since the number of decorators is the same
			if (!object.containsKey(decoratorName)) {
				throw new ModelException(String.format("Body does not contain decorator %s", decoratorName));
			}
			checkFields(object, builder, decoratorName);
		}
	}

	/**
	 * Check that the decorator with the given name from the builder and object has exactly the same fields.
	 * 
	 * @param object
	 *        Object representing a document version
	 * @param builder
	 *        Builder already containing data
	 * @param decoratorName
	 *        Name of the decorator to check
	 * @throws ModelException
	 *         When object and builder do not correspond
	 */
	private void checkFields(Map<String, Map<String, Object>> object, DocumentRevisionBuilder builder, String decoratorName) {
		List<String> fields = builder.decorator(decoratorName).fields();
		// Immediately throw an exception if the number of fields does not correspond
		Map<String, Object> decorator = object.get(decoratorName);
		if (decorator.size() != fields.size()) {
			throw new ModelException(String.format("Decorator %s does not have the same number of fields in the body and retrieved document version", decoratorName));
		}
		for (String fieldName : fields) {
			// The fields correspond one-to-one if the object's decorator contains the same names as the builder's
			// since the number of fields is the same
			if (!decorator.containsKey(fieldName)) {
				throw new ModelException(String.format("Decorator %s does not contain field %s", decoratorName, fieldName));
			}
		}
	}

	/**
	 * Retrieve a {@link DocumentRevisionBuilder} from the UDM.
	 * 
	 * @param documentId
	 *        ID of the document
	 * @param versionId
	 *        Version to retrieve
	 * @param section
	 *        Section to retrieve
	 * @param udmService
	 *        Service to use
	 * @return A {@link DocumentRevisionBuilder} for the given parameters
	 */
	private DocumentRevisionBuilder getVersion(DocumentID documentId, String versionId, String section, UDMService udmService) {
		return getVersion(getSourceOrTarget(udmService.document(documentId), section), versionId);
	}

	/**
	 * Get the source or target from a {@link DocumentBuilder}.
	 *
	 * @param builder
	 *        The builder to get the section from.
	 * @param section
	 *        The section to return. Can be either "source" or "target", and is case-insensitive.
	 * @return A {@link DocumentHistoryBuilder} of the given builder.
	 */
	private DocumentHistoryBuilder getSourceOrTarget(DocumentBuilder builder, String section) {
		// Check if the section is source or target, else throw an exception.
		if ("source".equalsIgnoreCase(section))
			return builder.source();
		else if ("target".equalsIgnoreCase(section))
			return builder.target();
		else
			throw new IllegalArgumentException("The \"section\" argument is not in a valid format."
					+ "Valid values are \"source\" and \"target\".");
	}

	/**
	 * Get a certain revision from a {@link DocumentHistoryBuilder}.
	 *
	 * @param builder
	 *        The builder to get the revision from.
	 * @param version
	 *        The version to het from the builder.
	 * @return A {@link DocumentRevisionBuilder} that is the correct version of the document.
	 */
	private DocumentRevisionBuilder getVersion(DocumentHistoryBuilder builder, String version) {
		// Check if the version is current or the version exists, else throw an exception.
		if ("current".equalsIgnoreCase(version))
			return builder.current();
		else if (builder.versions().contains(version))
			return builder.revision(version);
		else
			throw new VersionNotFoundException("The document does not contain a version [" + version + "].");
	}

}
