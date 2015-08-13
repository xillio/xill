package nl.xillio.xill.docgen.impl;

import freemarker.template.Configuration;
import nl.xillio.xill.docgen.*;

import org.elasticsearch.client.Client;

/**
 * This {@link DocGen} uses a template system and a elasticsearch backed searcher
 * 
 * @author Thomas Biesaart
 * @author Ivor van der Hoog
 * @since 12-8-2015
 */
public class XillDocGen implements DocGen {
	private final Client client;
  private final DocGenConfiguration config = new DocGenConfiguration();

	/**
	 * The constructor for the {@link XillDocGen}.
	 * @param client
	 * 					The {@link Client}  we're using for data storage and retrieval.
	 */
	public XillDocGen(final Client client) {
		this.client = client;
	}

	@Override
	 public DocumentationParser getParser() {
		return new XmlDocumentationParser();
	}

	@Override
	public DocumentationGenerator getGenerator(String collectionIdentity) {
		return new FreeMarkerDocumentationGenerator(
			collectionIdentity,
			getFreeMarkerConfig(),
			config.getDocumentationFolder());
	}

	@Override
	public DocumentationSearcher getSearcher() {
		return new ElasticsearchDocumentationSearcher(client);
	}

	Configuration getFreeMarkerConfig() {
		Configuration configuration = new Configuration(Configuration.VERSION_2_3_23);
		configuration.setClassForTemplateLoading(getClass(), config.getTemplateUrl());

		return configuration;
	}

}
