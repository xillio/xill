package nl.xillio.xill.plugins.file.services.extraction;

import com.google.inject.Singleton;
import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;

import java.io.File;
import java.io.IOException;

/**
 * This is the main implementation of the {@link TextExtractor} service using {@link Tika}
 */
@Singleton
public class TextExtractorImpl implements TextExtractor {
	private final Tika tika = new Tika();

	@Override
	public String extractText(final File file, int timeoutValue) throws IOException {
		try {
			return tika.parseToString(file);
		} catch (TikaException e) {
			throw new UnsupportedOperationException("Could not extract text from file", e);
		}
	}

}
