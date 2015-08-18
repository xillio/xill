package nl.xillio.xill.docgen.impl;

import freemarker.template.Configuration;
import nl.xillio.xill.docgen.*;
import nl.xillio.xill.docgen.exceptions.ParsingException;


/**
 * This {@link DocGen} uses a template system and a elasticsearch backed searcher
 *
 * @author Thomas Biesaart
 * @author Ivor van der Hoog
 * @since 12-8-2015
 */
public class XillDocGen implements DocGen {
	private final DocGenConfiguration config = new DocGenConfiguration();
	private DocumentationGenerator lastGenerator;

	@Override
	public DocumentationParser getParser() throws ParsingException {
		return new XmlDocumentationParser();
	}

	@Override
	public DocumentationGenerator getGenerator(String collectionIdentity) {
		lastGenerator = new FreeMarkerDocumentationGenerator(
			collectionIdentity,
			getFreeMarkerConfig(),
			config.getDocumentationFolder());
		return lastGenerator;
	}

	@Override
	public DocumentationSearcher getSearcher() {
		return new InMemoryDocumentationSearcher();
	}

	@Override
	public DocGenConfiguration getConfig() {
		return config;
	}

	@Override
	public void generateIndex() throws ParsingException {
		if (lastGenerator == null) {
			throw new ParsingException("No packages have been parsed");
		}

		lastGenerator.generateIndex();
	}

	Configuration getFreeMarkerConfig() {
		Configuration configuration = new Configuration(Configuration.VERSION_2_3_23);
		configuration.setClassForTemplateLoading(getClass(), config.getTemplateUrl());

		return configuration;
	}


}
