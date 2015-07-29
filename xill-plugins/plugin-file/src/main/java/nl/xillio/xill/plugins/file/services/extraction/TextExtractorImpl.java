package nl.xillio.xill.plugins.file.services.extraction;

import java.io.File;
import java.io.IOException;

import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;

import com.google.inject.Singleton;

/**
 * This is the main implementation of the {@link TextExtractor} service using {@link Tika}
 */
@Singleton
public class TextExtractorImpl implements TextExtractor {
	private final Tika tika = new Tika();

	@Override
	public String extractText(final File file) throws IOException {
		try {
			return tika.parseToString(file);
		} catch (TikaException e) {
			throw new UnsupportedOperationException("Could not extract text from file", e);
		}
	}

}
