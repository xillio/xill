package nl.xillio.xill.docgen.impl;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import nl.xillio.xill.docgen.DocumentationSearcher;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.admin.indices.refresh.RefreshRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.unit.Fuzziness;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;

/**
 * 
 *
 * @author Thomas Biesaart
 * @author Ivor van der Hoog
 * @since 12-8-2015
 */
public class ElasticsearchDocumentationSearcher implements DocumentationSearcher {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final String DOCUMENTATION_INDEX = "functiondocumentation";
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
		checkIndex();
		
		//Setup the query
		BoolQueryBuilder question = setupQuery(query);
		
		SearchResponse response = null;
		// Retrieve a response
		try{
		response = client.prepareSearch(DOCUMENTATION_INDEX).setQuery(question).execute().actionGet();
		}
		catch(NullPointerException e){
			LOGGER.error("Failed to retrieve a response from query", e);
		}
		// Return the response.
		return handleSearchResponse(response);
	}
	
	private String[] handleSearchResponse(SearchResponse response){
	if(response == null){
		return null;
	}
		// Return the ID of each response (The function name).
		SearchHit[] hits = response.getHits().getHits();
		String[] results = new String[hits.length];
		for (int t = 0; t < hits.length; ++t) {
			results[t] = hits[t].getType() + "." + hits[t].id();
		}
		return results;
	}
	
	private BoolQueryBuilder setupQuery(String query){
		float boostValue = 3.0f;
		BoolQueryBuilder question = QueryBuilders.boolQuery();
		
		// Setup the query
		String[] queries = query.split(" ");
		for (String q : queries) {
			question = question.should(QueryBuilders.fuzzyQuery("name", q).fuzziness(Fuzziness.TWO).boost(boostValue))
				.should(QueryBuilders.wildcardQuery("name", "*" + q + "*").boost(boostValue))
				.should(QueryBuilders.fuzzyQuery("description", q))
				.should(QueryBuilders.fuzzyQuery("examples", q))
				.should(QueryBuilders.fuzzyQuery("searchTags", q).boost(boostValue))
				.should(QueryBuilders.wildcardQuery("searchTags", "*" + q + "*").boost(boostValue));
		}
		return question;
	}

	@Override
	public void index(final String packet, final ConstructDocumentationEntity entity) {
		try {
			client.prepareIndex(DOCUMENTATION_INDEX, packet, entity.getIdentity())
				.setSource(entity.getProperties()).execute().actionGet();
		} catch (ElasticsearchException | NullPointerException e) {
			LOGGER.error("Failed to index: " + packet + "." + entity.getIdentity(), e);
		}
	}

	/**
	 * Check if the index exists, if not then create it
	 */
	private void checkIndex() {
		boolean indexFound = false;

		try {
			IndicesExistsResponse result = client.admin().indices()
				.exists(new IndicesExistsRequest(DOCUMENTATION_INDEX)).get();
			indexFound = result.isExists();
		} catch (InterruptedException | ExecutionException | NullPointerException e) {
			LOGGER.error("Failed to check index of the " + DOCUMENTATION_INDEX + "for ES", e);
		}

		if (!indexFound) {
			try {
				client.admin().indices().create(new CreateIndexRequest(DOCUMENTATION_INDEX)).get();
				client.admin().indices().refresh(new RefreshRequest(DOCUMENTATION_INDEX)).get();
			} catch (InterruptedException | ExecutionException | NullPointerException e) {
				LOGGER.error("Failed to create an index for: " + DOCUMENTATION_INDEX + "for ES", e);
			}
		}
	}
}
