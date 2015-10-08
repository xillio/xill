package nl.xillio.xill.plugins.document.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import nl.xillio.udm.DocumentID;
import nl.xillio.udm.UDM;
import nl.xillio.udm.builders.DocumentBuilder;
import nl.xillio.udm.builders.DocumentHistoryBuilder;
import nl.xillio.udm.builders.DocumentRevisionBuilder;
import nl.xillio.udm.exceptions.ModelException;
import nl.xillio.udm.exceptions.PersistenceException;
import nl.xillio.udm.interfaces.FindResult;
import nl.xillio.udm.services.UDMService;
import nl.xillio.xill.plugins.document.exceptions.VersionNotFoundException;
import nl.xillio.xill.plugins.document.util.UDMFindResultIterator;

import org.bson.Document;

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
	XillUDMServiceImpl(final ConversionService conversionService) {
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
	public Map<String, Map<String, Object>> get(final String documentId, final String versionId, final Section section) {
		// Get the document and convert it to a map.
		try (UDMService udmService = connect()) {
			DocumentID docId = udmService.get(documentId);
			DocumentRevisionBuilder document = getVersion(docId, versionId, section, udmService);
			if (document == null) {
				throw new VersionNotFoundException("The document does not contain a version [" + versionId + "].");
			}
			Map<String, Map<String, Object>> result = conversionService.udmToMap(document);

			// Clear the cache.
			udmService.release(docId);
			return result;
		}
	}

	@Override
	public DocumentID create(String contentType, Map<String, Map<String, Object>> body, String versionId)
			throws PersistenceException {
		try (UDMService udmService = connect()) {
			// Build the document.
			DocumentBuilder builder = udmService.create();
			builder.contentType().name(contentType);
			conversionService.mapToUdm(body, builder.source().current().version(versionId));
			conversionService.mapToUdm(body, builder.target().current().version(versionId));

			// Save to the database and return the id.
			DocumentID id = builder.commit();
			udmService.persist(id);
			return id;
		}
	}

	@Override
	public Iterable<Map<String, Map<String, Object>>> findWhere(Document filter, String version, Section section) throws PersistenceException {
		// Open a UDMService and don't close it yet. The result should be available later. The language framework should close this for us.
		UDMService udmService = connect();
		FindResult result = udmService.find(filter);
		return new UDMFindResultIterator(result, udmService, conversionService, version, section);
	}

	@Override
	public List<String> getVersions(final String documentID) {
		return getVersions(documentID, Section.TARGET);
	}

	@Override
	public List<String> getVersions(final String documentID, final Section section) {
		try (UDMService udmService = connect()) {
			DocumentID doc = udmService.get(documentID);
			List<String> result = getSourceOrTarget(udmService.document(doc), section).versions();
			udmService.release(doc);
			return new ArrayList<>(result);
		}
	}

	@Override
	public void remove(final String documentId, final String versionId, final Section section) throws PersistenceException {
		try (UDMService udmService = connect()) {
			DocumentID docId = udmService.get(documentId);
			if ("all".equals(versionId)) {
				udmService.delete(docId);
			} else {
				DocumentRevisionBuilder revision = getVersion(docId, versionId, section, udmService);
				if (revision == null) {
					throw new VersionNotFoundException("The document does not contain a version [" + versionId + "].");
				}
				revision.removeRevision(versionId);
				udmService.persist(docId);
			}
		}
	}

	@Override
	public long removeWhere(final Document filter) throws PersistenceException {
		try (UDMService udmService = connect()) {
			return udmService.delete(filter);
		}
	}

	@Override
	public long removeWhere(final Document filter, final String version, final Section section) throws PersistenceException {
		try (UDMService udmService = connect()) {
			return udmService.update(filter,
				new Document("$pull",
					new Document(section.toString().toLowerCase() + ".versions",
						new Document("version", version))));
		}
	}

	@Override
	public long updateWhere(Document filter, Map<String, Map<String, Object>> body) throws PersistenceException {
		return updateWhere(filter, body, "current", Section.TARGET);
	}

	@Override
	public long updateWhere(Document filter, Map<String, Map<String, Object>> body, String versionId, Section section) throws PersistenceException {
		try (UDMService udmService = connect()) {
			// Find all applicable documents
			FindResult result = udmService.find(filter);
			// Update all documents and count them
			long count = 0;
			for (DocumentID id : result) {
				if (updateVersion(body, id, versionId, section, udmService))
					count++;
			}
			return count;
		}
	}

	@Override
	public void update(final String documentId, final Map<String, Map<String, Object>> body, final String versionId, final Section section) throws PersistenceException {
		try (UDMService udmService = connect()) {
			DocumentID docId = udmService.get(documentId);
			if (!updateVersion(body, docId, versionId, section, udmService)) {
				throw new VersionNotFoundException("The document does not contain a version [" + versionId + "].");
			}
		}
	}

	private boolean updateVersion(final Map<String, Map<String, Object>> body, DocumentID docId, final String versionId, final Section section, UDMService udmService) throws PersistenceException {
		DocumentRevisionBuilder document = getVersion(docId, versionId, section, udmService);
		if (document == null)
			return false;

		// Verify that all decorator and fields are the same, else some decorators or fields might not be updated
		checkDecorators(body, document);

		// Input into the builder
		conversionService.mapToUdm(body, document);

		// Persist and clear from cache
		udmService.persist(docId);

		return true;
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
	private void checkDecorators(final Map<String, Map<String, Object>> object, final DocumentRevisionBuilder builder) {
		// Immediately throw an exception if the number of decorators does not correspond
		List<String> decorators = builder.decorators();
		if (object.size() != decorators.size()) {
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
	private void checkFields(final Map<String, Map<String, Object>> object, final DocumentRevisionBuilder builder, final String decoratorName) {
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
	 * @return A {@link DocumentRevisionBuilder} for the given parameters, or null if no such version exists
	 */
	private DocumentRevisionBuilder getVersion(final DocumentID documentId, final String versionId, final Section section, final UDMService udmService) {
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
	private DocumentHistoryBuilder getSourceOrTarget(final DocumentBuilder builder, final Section section) {
		// Check if the section is source or target, else throw an exception.
		if (section == Section.TARGET) {
			return builder.target();
		} else {
			return builder.source();
		}
	}

	/**
	 * Get a certain revision from a {@link DocumentHistoryBuilder}.
	 *
	 * @param builder
	 *        The builder to get the revision from.
	 * @param version
	 *        The version to get from the builder.
	 * @return A {@link DocumentRevisionBuilder} that is the correct version of the document.
	 */
	private DocumentRevisionBuilder getVersion(final DocumentHistoryBuilder builder, final String version) {
		// Check if the version is current or the version exists, else throw an exception.
		if ("current".equalsIgnoreCase(version) || builder.current().version().equals(version)) {
			return builder.current();
		} else if (builder.versions().contains(version)) {
			return builder.revision(version);
		} else {
			return null;
		}
	}
}
