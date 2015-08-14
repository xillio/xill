package nl.xillio.xill.docgen.impl;

import java.util.concurrent.ExecutionException;

import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.admin.indices.refresh.RefreshRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.unit.Fuzziness;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;

/**
 * TODO: jdoc
 */
public class ElasticsearchDocumentationRepository {
	private static final String DOCUMENTATION_INDEX = "functiondocumentation";

	/**
	 * TODO: jdoc
	 * 
	 * @param client
	 * @param query
	 * @return
	 * @throws ElasticsearchException
	 * @throws NullPointerException
	 * @throws ExecutionException
	 * @throws InterruptedException
	 */
	String[] search(final Client client, final String query) throws ElasticsearchException, NullPointerException, InterruptedException, ExecutionException {
		checkIndex(client);

		// Setup the query
		QueryBuilder question = setupQuery(query);

		SearchResponse response = null;
		// Retrieve a response
		response = executeSearch(client, question);

		// Return the response.
		return handleSearchResponse(response);
	}

/**
	 * Executes a query on a client.
	 * @param client
	 * 					The client we query.
	 * @param question
	 * 					The question we ask, represented as a fully parameterized {@link QueryBuilder}.
	 * @return
	 * 				A {@link SearchResponse}.
	 */
	SearchResponse executeSearch(final Client client, final QueryBuilder question) {
		return client.prepareSearch(DOCUMENTATION_INDEX).setQuery(question).execute().actionGet();
	}

	/**
	 * Takes a searchResponse of a query for constructs and returns an array of references to constructs.
	 * 
	 * @param response
	 *        The SearchResponse we're parsing.
	 * @return
	 *         An array of strings formatted as: packageName.constructName.
	 */
	String[] handleSearchResponse(final SearchResponse response) {
		if (response == null) {
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

	/**
	 * Sets up a parameterized {@link BoolQueryBuilder} from a query.
	 * 
	 * @param query
	 *        The query.
	 * @return
	 *         A fully parameterized QueryBuilder.
	 */
	BoolQueryBuilder setupQuery(final String query) {
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

	/**
	 * Index a ConstructDocumentationEntity in a client.
	 *
	 * @param client
	 *        The client we want to index the Construct in.
	 * @param packet
	 *        The package the construct is in.
	 * @param entity
	 *        The construct we want to index.
	 * @throws ElasticsearchException
	 * @throws NullPointerException
	 */
	void index(final Client client, final String packet, final ConstructDocumentationEntity entity) throws ElasticsearchException, NullPointerException {
		client.prepareIndex(DOCUMENTATION_INDEX, packet, entity.getIdentity())
		.setSource(entity.getProperties()).execute().actionGet();
	}

	/**
	 * Check if the index exists, if not then create it
	 */
	void checkIndex(final Client client) throws InterruptedException, ExecutionException, NullPointerException, ElasticsearchException {
		boolean indexFound = false;

		IndicesExistsResponse result = client.admin().indices()
				.exists(new IndicesExistsRequest(DOCUMENTATION_INDEX)).get();
		indexFound = result.isExists();

		if (!indexFound) {
			client.admin().indices().create(new CreateIndexRequest(DOCUMENTATION_INDEX)).get();
			client.admin().indices().refresh(new RefreshRequest(DOCUMENTATION_INDEX)).get();
		}
	}

}
