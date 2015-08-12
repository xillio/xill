package nl.xillio.xill.docgen.impl;

import nl.xillio.xill.docgen.DocGen;
import nl.xillio.xill.docgen.DocumentationGenerator;
import nl.xillio.xill.docgen.DocumentationParser;
import nl.xillio.xill.docgen.DocumentationSearcher;

import org.elasticsearch.client.Client;

/**
 * This {@link DocGen} uses a template system and a elasticsearch backed searcher
 * 
 * @author Thomas Biesaart
 * @author Ivor van der Hoog
 * @since 12-8-2015
 */
public class XillDocGen implements DocGen {
	Client client;

	public XillDocGen(final Client client) {
		this.client = client;
	}

	@Override
	public DocumentationParser getParser() {
		return new XmlDocumentationParser();
	}

	@Override
	public DocumentationGenerator getGenerator(final String collectionIdentity) {
		return new FreeMarkerDocumentationGenerator(collectionIdentity);
	}

	@Override
	public DocumentationSearcher getSearcher() {
		return new ElasticsearchDocumentationSearcher(client);
	}

}
