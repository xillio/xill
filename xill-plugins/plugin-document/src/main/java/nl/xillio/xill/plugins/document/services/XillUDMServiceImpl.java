package nl.xillio.xill.plugins.document.services;

import com.google.inject.Inject;
import nl.xillio.udm.DocumentID;
import nl.xillio.udm.UDM;
import nl.xillio.udm.builders.DocumentBuilder;
import nl.xillio.udm.builders.DocumentHistoryBuilder;
import nl.xillio.udm.builders.DocumentRevisionBuilder;
import nl.xillio.udm.services.UDMService;
import nl.xillio.xill.plugins.document.exceptions.VersionNotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class XillUDMServiceImpl implements XillUDMService {

	private final ConversionService conversionService;

	/**
	 * Create a new XillUDMService and set the services it depends on manually
	 *
	 * @param conversionService The {@link ConversionService}
	 */
	@Inject
	XillUDMServiceImpl(ConversionService conversionService) {
		this.conversionService = conversionService;
	}

	protected UDMService connect() {
		return UDM.connect();
	}

	@Override
	public Map<String, Map<String, Object>> get(String documentId, String versionId, String section) {
		// Get the document and convert it to a map.
		try (UDMService udmService = connect()) {
			DocumentID docId = udmService.get(documentId);
			DocumentRevisionBuilder document = getVersion(getSourceOrTarget(udmService.document(docId), section), versionId);
			Map<String, Map<String, Object>> result = conversionService.udmToMap(document);

			// Clear the cache.
			udmService.release(docId);

			return result;
		}
	}

	@Override
	public List<String> getVersions(String documentID) {
		return getVersions(documentID, "target");
	}

	@Override
	public List<String> getVersions(String documentID, String section) {
		try (UDMService udmService = connect()) {
			DocumentID doc = udmService.get(documentID);
			List<String> result = getSourceOrTarget(udmService.document(doc), section).versions();
			udmService.release(doc);
			return new ArrayList<>(result);
		}
	}

	/**
	 * Get the source or target from a {@link DocumentBuilder}.
	 *
	 * @param builder The builder to get the section from.
	 * @param section The section to return. Can be either "source" or "target", and is case-insensitive.
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
	 * @param builder The builder to get the revision from.
	 * @param version The version to het from the builder.
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
