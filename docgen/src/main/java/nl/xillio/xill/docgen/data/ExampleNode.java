package nl.xillio.xill.docgen.data;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.OptionalInt;

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

		//Special case: code
		if("code".equalsIgnoreCase(tagName)) {
			this.content = cleanCode(content);
		}else {
			this.content = content;
		}
	}

	static private String cleanCode(String content) {
		String[] lines = getLines(content);
		int indentOffset = getOffset(lines);

		if(indentOffset == -1) {
			//No content
			return "";
		}

		String[] cleaned = removeCharacters(indentOffset, lines);

		return String.join("\n", cleaned);
	}

	static String[] removeCharacters(int indentOffset, String[] lines) {
		return Arrays.stream(lines)
			.map(line -> {
				if(indentOffset >= line.length()) {
					//Is white line
					return "";
				}

				return line.substring(indentOffset, line.length()).replaceAll("\\s+$", "");
			}).toArray(i -> new String[i]);
	}

	static int getOffset(String[] lines) {
		OptionalInt offset = Arrays.stream(lines)
			.filter(line -> !line.replaceAll("\\s", "").isEmpty())
			.mapToInt(line -> countWhiteSpaces(line))
			.min();

		if(!offset.isPresent())
			return -1;

		return offset.getAsInt();
	}

	static int countWhiteSpaces(String line) {
		for(int i = 0; i < line.length(); i++) {
			if(!Character.isWhitespace(line.charAt(i))) {
				return i;
			}
		}
		return 0;
	}

	static String[] getLines(String content) {
		//Remove trailing newline
		String code = content.replaceAll("^\\s*\n|\n\\s*$", "");

		//Split
		return Arrays.stream(code.split("\n"))
			.map(line -> line.replaceAll("\\s", "").isEmpty() ? "" : line)
			.map(line -> line.replaceAll("\\s*\n", "\n"))
			.toArray(i -> new String[i]);
	}

	@Override
	public Map<String, Object> getProperties() {
		Map<String, Object> properties = new LinkedHashMap<>();
		properties.put("tag", tagName);
		properties.put("contents", content);
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
