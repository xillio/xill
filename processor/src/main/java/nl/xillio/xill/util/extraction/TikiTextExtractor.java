package nl.xillio.xill.util.extraction;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;

public class TikiTextExtractor extends TextExtractor {
	private static final Tika parser = new Tika();
	private static final Logger log = Logger.getLogger("XMT");

	@Override
	public String extractText(final File file) {
		try {
			return parser.parseToString(file);
		} catch (TikaException | IOException e) {
			log.warn("Failed to parse " + file.getName() + " - " + e.getClass().getSimpleName() + ": " + e.getMessage());
			return null;
		}
	}

	@Override
	public Map<String, Object> extractMetadata(final File file) {
		Metadata metadata = new Metadata();
		try (InputStream stream = TikaInputStream.get(file)) {
			parser.parse(stream, metadata);
		} catch (FileNotFoundException e) {
			log.warn("The file `" + file.getAbsolutePath() + "` does not exist. Could not extract metadata.");
			return new HashMap<>();
		} catch (IOException e) {
			log.warn("Could not extract metadata from `" + file.getAbsolutePath() + "`: " + e.getMessage());
			return new HashMap<>();
		}
		return Arrays.stream(metadata.names()).collect(Collectors.toMap(Function.identity(), metadata::get));
	}

	@Override
	public Boolean canExtractFrom(final File file) {
		return true;
	}
}
