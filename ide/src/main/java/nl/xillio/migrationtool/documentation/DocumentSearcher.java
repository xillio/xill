package nl.xillio.migrationtool.documentation;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javafx.util.Pair;
import java.util.concurrent.ExecutionException;

import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.unit.Fuzziness;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;

/**
 * <p> This class handles indexing a FunctionDocument and querying the database of FunctionDocuments. </p>
 * <p>The public search function receives a string as query, splits that string and does a fuzzy search on each of the components.
 * It returns an array with the unique ID's of functions that match that query. </p>
 *
 * <p>The public index function recieves a FunctionDocument and adds it to the database.</p>
 * <p>It returns an IndexResponse which is an object from elasticsearch, usually this is cast into the void.</p>
=======

import javafx.util.Pair;

/**
 * This class handles indexing a FunctionDocument and querying the database of
 * FunctionDocuments. <BR>
 * <BR>
 *
 * The public search function receives a string as query, splits that string and
 * does a fuzzy search on each of the components. It returns an array with the
 * unique ID's of functions that match that query. <BR>
 * <BR>
 *
 * The public index function recieves a FunctionDocument and adds it to the
 * database. It returns an IndexResponse which is an object from elasticsearch,
 * usually this is cast into the void.
>>>>>>> origin/develop
 * 
 * @author Ivor
 */
public class DocumentSearcher {

	private static final String DOCUMENTATION_INDEX = "functiondocumentation";
	private final Client client;
	
	/**
	 * Initialises the {@link DocumentSearcher}
	 * @param c
	 * 					The client the {@link DocumentSearcher} works with
	 */
	public DocumentSearcher(Client c)
	{
		client = c;
	}

    /**
     * We search the database given a query. <BR>
     * The query (a string) is split on whitespace and each word is queried in
     * the following fasion:<BR>
     * <BR>
     * foreach( word in query): <BR>
     * We fuzzyQuery the name, description, examples and searchTags <BR>
     * We wildcardQuery (substring match) name and searchtags <BR>
     * We boost the name and the searchtag category <BR>
     * 
     * @param query
     *            The search query
     * @return An array of unique ID's of functions that match that query.
     */
    public String[] search(final String query) {
	checkIndex();

	BoolQueryBuilder question = QueryBuilders.boolQuery();
		// Setup the query
		String[] queries = query.split(" ");
		for (String q : queries) {
			question = question
				.should(QueryBuilders.fuzzyQuery("name", q).fuzziness(Fuzziness.TWO).boost(3.0f))
				.should(QueryBuilders.wildcardQuery("name", "*" + q + "*").boost(3.0f))
				.should(QueryBuilders.fuzzyQuery("description", q))
				.should(QueryBuilders.fuzzyQuery("examples", q))
				.should(QueryBuilders.fuzzyQuery("searchTags", q).boost(3.0f))
				.should(QueryBuilders.wildcardQuery("searchTags", "*" + q + "*").boost(3.0f));
		}
		// Retrieve a response
		SearchResponse response = client.prepareSearch(DOCUMENTATION_INDEX).setQuery(question)
			.execute().actionGet();

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
     *            The package the function is in
     * @param id
     *            The unique id of the functiondocument
     * @return The version or null when the function is non existant
     */
    public String getDocumentVersion(final String packet, final String id) {
	checkIndex();
	try {
	    GetResponse Response = client.prepareGet(DOCUMENTATION_INDEX, packet, id).setFields("version").execute()
		    .actionGet();
	    return (String) Response.getField("version").getValue();
	} catch (Exception e) {
	    return null;
	}

    }


	/**
	 * Indexes a {@link FunctionDocument} into the database.
	 * @param document
	 *        The document we want to index
	 * @return 
	 * 				An index response of the function.
	 * @throws ElasticsearchException
	 * @throws IOException
	 */
	public IndexResponse index(final FunctionDocument document) throws ElasticsearchException, IOException {
		// Create an array of the example texts
		List<Pair<String, String>> items = new ArrayList<>(document.getExamples());
		String[] examples = new String[items.size()];
		for (int t = 0; t < items.size(); t++) {
			examples[t] = items.get(t).getValue();
		}

		// Create an array with all the parameters
		// To be adjusted in 3.0
		List<Pair<String, String>> parameters = new ArrayList<>(document.getParameters());
		String[] parameterstrings = new String[parameters.size()];
		for (int t = 0; t < parameters.size(); t++) {
			parameterstrings[t] = parameters.get(t).getKey() + " " + parameters.get(t).getValue();
		}

		// Return an indexed client with three fields
		return client.prepareIndex(DOCUMENTATION_INDEX, document.getPackage(), document.getName())
			.setSource(jsonBuilder()
				.startObject()
					.field("name", document.getName())
					.field("description", document.getDescription())
					.field("parameters", parameterstrings)
					.field("searchTags", document.getSearchTags())
					.field("version", document.getVersion())
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
	    } catch (InterruptedException | ExecutionException e) {
		e.printStackTrace();
	    }
	}
    }
}
