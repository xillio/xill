package nl.xillio.migrationtool.documentation;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.unit.Fuzziness;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import javafx.util.Pair;

/**
 * This class handles indexing a FunctionDocument and querying the database of FunctionDocuments. <BR> <BR>
 * 
 * The public search function receives a string as query, splits that string and does a fuzzy search on each of the components.
 * It returns an array with the unique ID's of functions that match that query. <BR> <BR>
 * 
 * The public index function recieves a FunctionDocument and adds it to the database.
 * It returns an IndexResponse which is an object from elasticsearch, usually this is cast into the void.
 * @author Ivor
 */
public class DocumentSearcher {
    private static final String DOCUMENTATION_INDEX = "functiondocumentation";
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
	    question = question
			    		.should(QueryBuilders.fuzzyQuery("name", q).fuzziness(Fuzziness.TWO).boost(3.0f))
			    		.should(QueryBuilders.wildcardQuery("name", "*" + q + "*").boost(3.0f))
			    		.should(QueryBuilders.fuzzyQuery("description", q))
			    		.should(QueryBuilders.fuzzyQuery("examples", q))
			    		.should(QueryBuilders.fuzzyQuery("searchTags", q));
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
     * @param packet 
     * The package the function is in
     * @param id
     * The unique id of the functiondocument
     * @return
     * The version or null when the function is non existant
     */
    public String getDocumentVersion(String packet, String id)
    {
    	try{
    	GetResponse Response = client.prepareGet(DOCUMENTATION_INDEX, packet, id).setFields("version").execute().actionGet();
    	return (String) Response.getField("version").getValue();
    	}
    	catch(Exception e)
    	{
    		return null;
    	}
    
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
	return client.prepareIndex(DOCUMENTATION_INDEX, document.getPackage(), document.getName())
		.setSource(jsonBuilder()
			.startObject()
				.field("name", document.getName())
				.field("description", document.getDescription())
				.field("parameters", parameterstrings)
				.field("searchtags", document.getSearchTags())
				.field("version", document.getVersion())
			.endObject())
		
		.execute().actionGet();
    }

}
