package nl.xillio.migrationtool.ElasticConsole;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.count.CountRequestBuilder;
import org.elasticsearch.action.count.CountResponse;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.action.search.SearchPhaseExecutionException;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.indices.IndexMissingException;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.sort.SortOrder;

import nl.xillio.events.Event;
import nl.xillio.events.EventHost;
import nl.xillio.xill.api.components.RobotID;

public class ESConsoleClient {

	/**
	 * The different logging message types.
	 */
	public static enum LogType {
		DEBUG, INFO, WARN, ERROR, FATAL
	}

	private static final ESConsoleClient instance = new ESConsoleClient();
	private static final Map<String, EventHost<RobotLogMessage>> eventInstances = new HashMap<>();

	// A list of characters that are illegal in the index and type
	private static final Pattern illegalChars = Pattern.compile("[\\\\/*?\"<>| ,:]");

	private final Node node;

	private ESConsoleClient() {
		// Create the settings and node
		Settings settings = ImmutableSettings.settingsBuilder()
			.put("cluster.name", "console")
			// Paths
			.put("path.data", "ESConsole/data")
			.put("path.logs", "ESConsole/logs")
			.put("path.work", "ESConsole/work")
			.put("path.plugins", "ESConsole/plugins")
			.put("path.conf", "ESConsole/config")
			// Make the node unreachable from the outside
			.put("discovery.zen.ping.multicast.enabled", false)
			.put("node.local", true)
			.put("http.enabled", false)
			.build();
		node = NodeBuilder.nodeBuilder().settings(settings).node();

		// Add a shutdown hook
		Runtime.getRuntime().addShutdownHook(new Thread(node::close));
	}

	/**
	 * Logs a message.
	 *
	 * @param robotId
	 *        The robot ID of the robot that called the log.
	 * @param type
	 *        The log type.
	 * @param timestamp
	 *        The timestamp of the log message.
	 * @param order
	 * @param message
	 *        The message to log.
	 */
	public void log(final String robotId, String type, final long timestamp, final int order, final String message) {

		// Normalize index and type
		EventHost<RobotLogMessage> eventHost = eventInstances.get(robotId);
		String index = normalizeId(robotId);
		type = normalize(type);

		// Create the index
		createIndex(index);

		// Create the map
		Map<String, Object> data = new HashMap<>();
		data.put("timestamp", timestamp);
		data.put("order", order);
		data.put("message", message);

		// Index the log message in robotId/type/
		IndexRequestBuilder request = getClient().prepareIndex(index, type).setSource(data);
		request.execute().actionGet();

		// Fire event if there is one
		if (eventHost != null) {
			eventHost.invoke(new RobotLogMessage(type, message));
		}
	}

	/**
	 * Create the index (with default log mapping) if it does not exist yet.
	 *
	 * @param index
	 *        The index to create.
	 */
	private void createIndex(final String index) {
		// Check if the index exists
		if (getClient().admin().indices().prepareExists(index).execute().actionGet().isExists()) {
			return;
		}

		// Create the index creation request with all mapping
		CreateIndexRequest request = new CreateIndexRequest(index);
		for (LogType type : LogType.values()) {
			request.mapping(type.toString().toLowerCase(), getLogMapping(type));
		}

		// Create the index
		getClient().admin().indices().create(request).actionGet();
	}

	/**
	 * Get the default mapping for log entries in elasticsearch.
	 *
	 * @param type
	 *        The type to create the mapping source for.
	 * @return An XContentBuilder that contains the source for the mapping.
	 */
	private static XContentBuilder getLogMapping(final LogType type) {
		try {
			// Create the content builder
			XContentBuilder source = XContentFactory.jsonBuilder();

			// Create the source
			source.startObject().startObject(type.toString().toLowerCase()).startObject("properties");
			// Set the field types
			source.startObject("timestamp").field("type", "long").field("store", true).endObject();
			source.startObject("order").field("type", "integer").field("store", true).endObject();
			source.startObject("message").field("type", "string").field("store", true).endObject();
			// End
			source.endObject().endObject().endObject();

			return source;
		} catch (IOException e) {
			// Should never occur
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Searches for the last log entries for the specified robot and types, sorted by the timestamp.
	 *
	 * @param robotId
	 *        The robot ID of the robot to get the log for.
	 * @param amount
	 *        The amount of messages to collect.
	 * @return A list of maps containing the timestamp, type and message of each log message.
	 */
	public ArrayList<Map<String, Object>> getLastEntries(final String robotId, final int amount) {
		// Normalize index
		String normalizedId = normalizeId(robotId);

		// Create the search request
		int from = Math.max(0, countEntries(normalizedId) - amount);
		SearchRequestBuilder request = getClient().prepareSearch(normalizedId).setQuery(QueryBuilders.matchAllQuery());
		request.addSort("timestamp", SortOrder.ASC).addSort("order", SortOrder.ASC).setFrom(from).setSize(amount);

		SearchResponse response = null;
		try {
			// Refresh the index to make sure everything is properly indexed etc
			getClient().admin().indices().prepareRefresh(normalizedId).execute().actionGet();
			// Get the response
			response = request.execute().actionGet();
		} catch (SearchPhaseExecutionException | IndexMissingException e) {
			// If an exception is thrown, return an empty list
			return new ArrayList<>(0);
		}

		// Return the hits as a list of maps
		return hitsToList(response.getHits());
	}

	/**
	 * Count the amount of documents in an index.
	 *
	 * @param robotId
	 *        The ID of the robot to count the entries for.
	 * @return The amount of documents in the index.
	 */
	public int countEntries(final String robotId) {
		// Normalize index
		String normalizedId = normalizeId(robotId);

		// Create the count request
		CountRequestBuilder request = getClient().prepareCount(normalizedId).setQuery(QueryBuilders.matchAllQuery());

		CountResponse response = null;
		try {
			// Refresh the index to make sure everything is properly indexed etc
			getClient().admin().indices().prepareRefresh(normalizedId).execute().actionGet();
			// Get the response
			response = request.execute().actionGet();
		} catch (SearchPhaseExecutionException | IndexMissingException e) {
			return 0;
		}

		// Return the count
		return (int) response.getCount();
	}

	private static ArrayList<Map<String, Object>> hitsToList(final SearchHits hits) {
		// Get the search hits, create a result list
		SearchHit[] searchHits = hits.getHits();
		ArrayList<Map<String, Object>> results = new ArrayList<>(searchHits.length);

		// Add all hits to the list
		for (int i = 0; i < searchHits.length; i++) {
			results.add(searchHits[i].getSource());
			results.get(i).put("type", searchHits[i].type());
		}

		return results;
	}

	/**
	 * Clear the log for a robot.
	 *
	 * @param robotId
	 *        The robot ID of the robot to clear the log for.
	 */
	public void clearLog(final String robotId) {
		// Normalize index
		String normalizedId = normalizeId(robotId);

		// Delete all documents in the robotId index
		try {
			getClient().admin().indices().delete(new DeleteIndexRequest(normalizedId));
		} catch (IndexMissingException e) {
			// If the index does not exist yet, do nothing
		}
	}

	private static String normalizeId(final String id) {
		// Check if the id starts with an alphanum character, else prefix with "bot_"
		String n = id.matches("^[A-Za-z\\d].*") ? normalize(id) : "bot_" + normalize(id);
		return n;
	}

	private static String normalize(final String text) {
		// Replace illegal chars
		String pattern = illegalChars.pattern();
		String replaced = text.replaceAll(pattern, "_");
		String lower = replaced.toLowerCase();
		return lower;
	}

	/**
	 * @return the {@link Client}
	 */
	public Client getClient() {
		return node.client();
	}

	/**
	 * Get the elasticsearch console client.
	 *
	 * @return The running ESConsoleClient instance.
	 */
	public static ESConsoleClient getInstance() {
		return instance;
	}

	/**
	 * Gets the log event corresponding to the {@link RobotID}
	 *
	 * @param robotId
	 * @return The event
	 */
	public static Event<RobotLogMessage> getLogEvent(final RobotID robotId) {

		synchronized (eventInstances) {
			EventHost<RobotLogMessage> host = eventInstances.get(robotId.toString());

			if (host == null) {
				host = new EventHost<>();
				eventInstances.put(robotId.toString(), host);
			}

			return host.getEvent();
		}
	}
}
