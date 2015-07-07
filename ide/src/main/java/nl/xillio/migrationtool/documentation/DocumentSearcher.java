package nl.xillio.migrationtool.documentation;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;

import javafx.util.Pair;

/**
 * @author Ivor
 */
public class DocumentSearcher {

    private final Client client;

    /**
     * A class with a method to search a client based on a query
     *
     * @param client
     *            The client we index and search on.
     */
    public DocumentSearcher(final Client client) {
	this.client = client;
    }

    /**
     * @param query
     *            The search query
     * @param client
     *            The client we're searching on
     * @return An array of unique ID's of functions that match that query.
     */
    public String[] search(final String query) {
	BoolQueryBuilder question = QueryBuilders.boolQuery();

	// Setup the query
	String[] queries = query.split(" ");
	for (String q : queries) {
	    question = question.should(QueryBuilders.fuzzyQuery("name", q))
		    .should(QueryBuilders.fuzzyQuery("description", q)).should(QueryBuilders.fuzzyQuery("examples", q))
		    .should(QueryBuilders.fuzzyQuery("searchtags", q));
	}

	// Retrieve a response
	SearchResponse response = client.prepareSearch("functiondocumentation").setTypes("function").setQuery(question)
		.execute().actionGet();

	// Return the ID of each response (functionname)
	SearchHit[] hits = response.getHits().getHits();
	String[] results = new String[hits.length];

	for (int t = 0; t < hits.length; ++t) {
	    results[t] = hits[t].id();
	}
	return results;
    }

    /**
     * @param document
     *            The document we want to index
     * @return An index response of the function.
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
	return client.prepareIndex("functiondocumentation", "function", document.getName())
		.setSource(jsonBuilder().startObject().field("name", document.getName())
			.field("description", document.getDescription()).field("parameters", parameterstrings)
			.field("searchtags", document.getSearchTags()).endObject())
		.execute().actionGet();
    }

}
