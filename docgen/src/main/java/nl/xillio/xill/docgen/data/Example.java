package nl.xillio.xill.docgen.data;

import nl.xillio.xill.docgen.PropertiesProvider;
import nl.xillio.xill.docgen.impl.ConstructDocumentationEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents an example item of a {@link ConstructDocumentationEntity}.
 *
 */
public class Example implements PropertiesProvider {
	private final List<ExampleNode> content = new ArrayList<>();
	private final String title;

	public Example(String title) {
		this.title = title;
	}

	@Override
	public Map<String, Object> getProperties() {
		Map<String, Object> properties = new HashMap<>();
		properties.put("nodes", PropertiesProvider.extractContent(content));
		properties.put("title", title);
		return properties;
	}

	/**
	 * Add a node to the example.
	 * 
	 * @param node
	 *        The node we want to add.
	 */
	public void addContent(final ExampleNode node) {
		content.add(node);
	}
	
	/**
	 * Returns all Nodes in the {@link Example}.
	 * @return
	 * 				all nodes in the example.
	 */
	public List<ExampleNode> getExampleNodes() {
		return content;
	}
}
