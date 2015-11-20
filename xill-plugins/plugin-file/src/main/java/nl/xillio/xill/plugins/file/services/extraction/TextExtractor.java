package nl.xillio.xill.plugins.file.services.extraction;

import com.google.inject.ImplementedBy;
import nl.xillio.xill.services.XillService;

import java.io.File;
import java.io.IOException;

/**
 * This {@link XillService} is responsible for extracting text from a file
 */
@ImplementedBy(TextExtractorImpl.class)
public interface TextExtractor extends XillService {

	/**
	 * Get the text from a file
	 *
	 * @param file         the file
	 * @param timeoutValue the time in milliseconds after which to give up
	 *
	 * @return the raw text contained in that file
	 *
	 * @throws IOException                   The an I/O Error occurred
	 * @throws UnsupportedOperationException when the file format wasn't supported or could not be parsed
	 */
	String extractText(File file, int timeoutValue) throws IOException;
}
