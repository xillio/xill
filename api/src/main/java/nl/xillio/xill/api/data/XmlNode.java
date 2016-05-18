package nl.xillio.xill.api.data;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * This interface represents an object that contains XML information.
 *
 * @author Zbynek Hochmann
 * @since 3.0.0
 */
public interface XmlNode extends MetadataExpression {
	/**
	 * Returns XML document of this node
	 *
	 * @return {@link org.w3c.dom.Document} of this node
	 */
	Document getDocument();

	/**
	 * @return {@link org.w3c.dom.Node} data specifying this node
	 */
	Node getNode();

	/**
	 * Returns XML content of this node in string format
	 *
	 * @return XML content in string format
	 */
	String getXmlContent();

	/**
	 * @return a string containing all text extracted from XML node or XML document
	 */
	String getText();
}
