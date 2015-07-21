package nl.xillio.migrationtool.documentation;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.admin.indices.refresh.RefreshRequest;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.unit.Fuzziness;
import org.elasticsearch.index.get.GetField;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;

import javafx.util.Pair;

/**
 * <p>
 * This class handles indexing a FunctionDocument and querying the database of FunctionDocuments.
 * </p>
 * <p>
 * The public search function receives a string as query, splits that string and does a fuzzy search on each of the components. It returns an array with the unique ID's of functions that match that
 * query.
 * </p>
 *
 * <p>
 * The public index function recieves a FunctionDocument and adds it to the database.
 * </p>
 * <p>
 * It returns an IndexResponse which is an object from elasticsearch, usually this is cast into the void.
 * </p>
 *
 * @author Ivor
 */
public class DocumentSearcher {

	private static final String DOCUMENTATION_INDEX = "functiondocumentation";
	private final Client client;

	/**
	 * Initialises the {@link DocumentSearcher}
	 *
	 * @param c
	 *        The client the {@link DocumentSearcher} works with.
	 */
	public DocumentSearcher(final Client c) {
		client = c;
	}

	/**
	 * <p>
	 * We search the database given a query.
	 * </p>
	 *
	 * <p>
	 * The query (a string) is split on whitespace and each word is queried in the following fasion:
	 * </p>
	 *
	 * <p>
	 * foreach( word in query):
	 * </p>
	 * <ul>
	 * <li>We fuzzyQuery the name, description, examples and searchTags</li>
	 * <li>We wildcardQuery (substring match) name and searchtags</li>
	 * <li>We boost the name and the searchtag category.</li>
	 * </ul>
	 *
	 * @param query
	 *        The search query
	 * @return An array of unique ID's of functions that match that query.
	 */
	public String[] search(final String query) {
		checkIndex();

		BoolQueryBuilder question = QueryBuilders.boolQuery();
		// Setup the query
		String[] queries = query.split(" ");
		for (String q : queries) {
			question = question.should(QueryBuilders.fuzzyQuery("name", q).fuzziness(Fuzziness.TWO).boost(3.0f))
				.should(QueryBuilders.wildcardQuery("name", "*" + q + "*").boost(3.0f))
				.should(QueryBuilders.fuzzyQuery("description", q)).should(QueryBuilders.fuzzyQuery("examples", q))
				.should(QueryBuilders.fuzzyQuery("searchTags", q).boost(3.0f))
				.should(QueryBuilders.wildcardQuery("searchTags", "*" + q + "*").boost(3.0f));
		}
		// Retrieve a response
		SearchResponse response = client.prepareSearch(DOCUMENTATION_INDEX).setQuery(question).execute().actionGet();

		// Return the ID of each response (functionname)
		SearchHit[] hits = response.getHits().getHits();
		String[] results = new String[hits.length];
		for (int t = 0; t < hits.length; ++t) {
			results[t] = hits[t].getType() + "." + hits[t].id();
		}
		return results;
	}

	/**
	 * Returns a string which is the documentversion given a package and ID.
	 *
	 * @param packet
	 *        The package the function is in
	 * @param id
	 *        The unique id of the functiondocument
	 * @return The version or null when the function is non existant
	 */
	public String getDocumentVersion(final String packet, final String id) {
		checkIndex();
		try {

			GetResponse Response = client.get(new GetRequest(DOCUMENTATION_INDEX, packet, id).fields("packageversion")).get(); // client.prepareGet(DOCUMENTATION_INDEX, packet,
			// id).setFields("packageversion").execute().get();
			GetField field = Response.getField("packageversion");
			if (field != null) {
				return (String) field.getValue();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			// Wasn't able to execute the query
		}
		return null;
	}

	/**
	 * Indexes a {@link FunctionDocument} into the database.
	 *
	 * @param document
	 *        The document we want to index
	 * @return An index response of the function.
	 * @throws ElasticsearchException
	 *         Throws an elasticsearchException when we get an error
	 *         indexing the {@link FunctionDocument}
	 * @throws IOException
	 *         Throws an IOException when trying to write to a file.
	 */
	public IndexResponse index(final FunctionDocument document) throws ElasticsearchException, IOException {
		// Create an array of the example texts
		List<Pair<String, String>> items = new ArrayList<>(document.getExamples());
		String[] examples = new String[items.size()];
		for (int t = 0; t < items.size(); t++) {
			examples[t] = items.get(t).getValue();
		}
		// Return an indexed client with three fields
		return client.prepareIndex(DOCUMENTATION_INDEX, document.getPackage(), document.getName())
			.setSource(jsonBuilder().startObject().field("name", document.getName())
				.field("description", document.getDescription()).field("parameters", document.getParameters())
				.field("searchTags", document.getSearchTags()).field("packageversion", document.getVersion())
				.endObject())

		.execute().actionGet();

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
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}

		if (!indexFound) {
			try {
				client.admin().indices().create(new CreateIndexRequest(DOCUMENTATION_INDEX)).get();
				client.admin().indices().refresh(new RefreshRequest(DOCUMENTATION_INDEX)).get();
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
			}
		}
	}
}
