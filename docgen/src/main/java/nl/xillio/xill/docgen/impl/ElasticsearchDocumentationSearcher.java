package nl.xillio.xill.docgen.impl;

import java.util.concurrent.ExecutionException;

import nl.xillio.xill.docgen.DocumentationSearcher;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.client.Client;

/**
 *
 *
 * @author Thomas Biesaart
 * @author Ivor van der Hoog
 * @since 12-8-2015
 */
public class ElasticsearchDocumentationSearcher implements DocumentationSearcher {
	private final ElasticsearchDocumentationRepository repository = new ElasticsearchDocumentationRepository();

	private static final Logger LOGGER = LogManager.getLogger();

	private final Client client;

	/**
	 * Constructor for the {@link ElasticsearchDocumentationSearcher}
	 *
	 * @param client
	 *        The {@link Client} we use.
	 */
	public ElasticsearchDocumentationSearcher(final Client client) {
		this.client = client;
	}

	@Override
	public String[] search(final String query) {
		String[] response = null;
		// Retrieve a response
		try {
			response = repository.search(client, query);
		} catch (NullPointerException | ElasticsearchException | InterruptedException | ExecutionException e) {
			LOGGER.error("Failed to retrieve a response from query", e);
		}
		// Return the response.
		return response;
	}

	@Override
	public void index(final String packet, final ConstructDocumentationEntity entity) {
		try {
			repository.index(client, packet, entity);
		} catch (ElasticsearchException | NullPointerException e) {
			LOGGER.error("Failed to index: " + packet + "." + entity.getIdentity(), e);
		}
	}
}
