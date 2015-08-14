package nl.xillio.xill.docgen.impl;

import freemarker.template.Configuration;
import nl.xillio.xill.docgen.*;
import nl.xillio.xill.docgen.exceptions.ParsingException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.client.Client;


/**
 * This {@link DocGen} uses a template system and a elasticsearch backed searcher
 *
 * @author Thomas Biesaart
 * @author Ivor van der Hoog
 * @since 12-8-2015
 */
public class XillDocGen implements DocGen {
	private static final Logger LOGGER = LogManager.getLogger();
	private final Client client;
	private final DocGenConfiguration config = new DocGenConfiguration();
	private DocumentationGenerator lastGenerator;

	/**
	 * The constructor for the {@link XillDocGen}.
	 *
	 * @param client The {@link Client}  we're using for data storage and retrieval.
	 */
	public XillDocGen(final Client client) {
		this.client = client;
	}

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
		return new ElasticsearchDocumentationSearcher(client);
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
