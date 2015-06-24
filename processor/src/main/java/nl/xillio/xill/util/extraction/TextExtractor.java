package nl.xillio.xill.util.extraction;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class TextExtractor {
	private static List<TextExtractor> registeredExtractors;

	public static TextExtractor getInstance(final File file) {
		if (registeredExtractors == null) {
			LoadExtractors();
		}
		return registeredExtractors.stream().filter(textExtractor -> textExtractor.extractText(file) != null).findAny().orElse(null);
	}

	private static void LoadExtractors() {
		registeredExtractors = new ArrayList<>();
		registeredExtractors.add(new TikiTextExtractor());
	}

	public abstract Boolean canExtractFrom(final File file);

	public abstract String extractText(final File file);

	public abstract Map<String, Object> extractMetadata(final File file);
}
