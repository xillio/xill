package nl.xillio.xill.docgen.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import nl.xillio.xill.docgen.PropertiesProvider;

/**
 * Represents an example item of a {@link ConstructDocumentationEntity}.
 *
 */
public class Example implements PropertiesProvider {
	private List<ExampleNode> content = new ArrayList<>();

	@Override
	public Map<String, Object> getProperties() {
    Map<String, Object> properties = new HashMap<>();
    properties.put("example", PropertiesProvider.extractContent(content));
		return properties;
	}
	
	/**
	 * Add a node to the example.
	 * @param node 
	 * 					The node we want to add.
	 */
	public void addContent(ExampleNode node){
		content.add(node);
	}
}
