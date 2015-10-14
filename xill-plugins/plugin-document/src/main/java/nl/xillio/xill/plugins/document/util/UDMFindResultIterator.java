package nl.xillio.xill.plugins.document.util;

import java.util.Map;

import nl.xillio.udm.DocumentID;
import nl.xillio.udm.builders.DocumentBuilder;
import nl.xillio.udm.builders.DocumentHistoryBuilder;
import nl.xillio.udm.builders.DocumentRevisionBuilder;
import nl.xillio.udm.interfaces.FindResult;
import nl.xillio.udm.services.UDMService;
import nl.xillio.udm.util.TransformationIterable;
import nl.xillio.xill.plugins.document.services.ConversionService;
import nl.xillio.xill.plugins.document.services.XillUDMService.Section;

/**
 * This Iterable will iterate over all requested DocumentRevisionBuilder and close the service once the close method is called.
 *
 * @author Thomas Biesaart
 */
public class UDMFindResultIterator extends TransformationIterable<DocumentID, Map<String, Map<String, Object>>> implements AutoCloseable {

	private final UDMService owningService;

	/**
	 * Create a new UDMFindResultIterator.
	 *  @param source        the source FindResult
	 * @param owningService the service that owns this result and should be closed later
	 * @param conversionService
	 * @param version       the requested version
	 * @param section       the requested section
	 */
	public UDMFindResultIterator(
		final FindResult source,
		final UDMService owningService,
		final ConversionService conversionService, final String version,
			final Section section) {
		super(
			source.iterator(),
			(DocumentID id) -> {
				DocumentBuilder documentBuilder = owningService.document(id);
				DocumentHistoryBuilder documentHistoryBuilder = section == Section.SOURCE ? documentBuilder.source() : documentBuilder.target();
				DocumentRevisionBuilder documentRevisionBuilder = "current".equalsIgnoreCase(version) ? documentHistoryBuilder.current() : documentHistoryBuilder.revision(version);
				return conversionService.udmToMap(documentRevisionBuilder);
			},
			null);
		this.owningService = owningService;
	}

	@Override
	public void close() {
		owningService.close();
	}
}
