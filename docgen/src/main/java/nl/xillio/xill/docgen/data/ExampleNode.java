package nl.xillio.xill.docgen.data;

import java.util.LinkedHashMap;
import java.util.Map;

import nl.xillio.xill.docgen.PropertiesProvider;

/**
 * Represents a single node in an example tag.
 *
 */
public class ExampleNode implements PropertiesProvider {
	private String tagName;
	private String content;
	
	/**
	 * The constructor for the {@link ExampleNode}
	 * @param tagName
	 * 					The name of the tag of the node.
	 * @param content
	 * 					The content of the node.
	 */
	public ExampleNode(String tagName, String content){
		this.tagName = tagName;
		this.content = content;
	}

	@Override
	public Map<String, Object> getProperties() {
		Map<String, Object> properties = new LinkedHashMap<>();
		properties.put("tag", tagName);
		properties.put("value", content);
		return properties;
	}
	
	/**
	 * Returns the tag name of the Node.
	 * @return
	 * 				The tag name of the Node.
	 */
	public String getTagName(){
		return tagName;
	}
	
	/**
	 * Returns the content of the Node.
	 * @return
	 * 				The content of the Node.
	 */
	public String getContent(){
		return content;
	}
}
