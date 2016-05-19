package nl.xillio.xill.api.data;

/**
 * This interface is capable of building xml nodes from source.
 * @since 1.0.7
 */
public interface XmlNodeFactory {
	/**
	 * Creates XML document from string, parse it and returns root node (XML document)
	 *
	 * @param xmlText string that contains valid XMl document
	 * @return newly created XML node representing root node of the entire document
	 */
	XmlNode fromString(final String xmlText);
}
