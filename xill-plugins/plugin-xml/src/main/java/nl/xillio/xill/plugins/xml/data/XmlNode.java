package nl.xillio.xill.plugins.xml.data;

import nl.xillio.xill.api.components.MetadataExpression;
import nl.xillio.xill.plugins.xml.exceptions.XmlParseException;

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXParseException;

/**
 * This class represents a XML node MetadataExpression
 *
 * @author Zbynek Hochmann
 */
public class XmlNode implements MetadataExpression {

	private Node node = null;

	TransformerFactory tf = TransformerFactory.newInstance();

	/**
	 * Creates XmlNode from XML string
	 * 
	 * @param xmlString	XML document
	 * @throws Exception when any unspecified error occurs
	 * @throws XmlParseException when XML format is invalid
	 */
	public XmlNode(String xmlString) throws Exception, XmlParseException {
		xmlString = xmlCharacterWhitelist(xmlString);

		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setNamespaceAware(true);
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document document = db.parse(new InputSource(new StringReader(xmlString)));

			// Normalize whitespace nodes
			removeEmptyTextNodes(document);
			document.normalize();
			this.node = document.getFirstChild();

		} catch (SAXParseException e) {
			throw new XmlParseException(e.getMessage());
		}
	}

	/**
	 * Creates XmlNode from org.w3c.dom.Node 
	 * 
	 * @param node input node
	 */
	public XmlNode(Node node) {
		this.node = node;
	}

	/**
	 * Returns XML document of this node
	 * 
	 * @return	org.w3c.dom.Document of this node
	 */
	public Document getDocument() {
		return this.node.getOwnerDocument();
	}

	/**
	 * @return	org.w3c.dom.Node data specifying this node
	 */
	public Node getNode() {
		return this.node;
	}

	@Override public String toString() {
		if (this.node == null) {
			return "XML Node[null]";
		}
		return String.format("XML Node[root = %1$s]", this.node.getNodeName());
	}
	
	/**
	 * Returns XML content of this node in string format
	 * 
	 * @return XML content in string format
	 */
	public String getXmlContent() {
		if (this.node == null) {
			return "null";
		}

		try {
			DOMSource domSource = new DOMSource(this.node);
			StringWriter writer = new StringWriter();
			StreamResult result = new StreamResult(writer);
			Transformer transformer = tf.newTransformer();
			transformer.transform(domSource, result);
			return writer.toString();
		} catch (Exception e) {
			System.err.println("Error while formatting XML: " + e.getMessage());
		}
		return null;		
	}

	private void removeEmptyTextNodes(final Node parent) {
		for (int i = 0; i < parent.getChildNodes().getLength(); i++) {
			Node child = parent.getChildNodes().item(i);
			if (child.hasChildNodes()) {
				removeEmptyTextNodes(child);
			} else if (child.getNodeType() == Node.TEXT_NODE && child.getNodeValue().trim().equals("")) {
				parent.removeChild(child);
				i = i - 1;
			}
		}
	}

	private static String xmlCharacterWhitelist(final String inputString) {
		if (inputString == null) {
			return null;
		}

		StringBuffer output = new StringBuffer();
		char ch;

		for (int i = 0; i < inputString.length(); i++) {
			ch = inputString.charAt(i);
			if ((ch >= 0x0020 && ch <= 0xD7FF) ||
					(ch >= 0xE000 && ch <= 0xFFFD) ||
					ch == 0x0009 ||
					ch == 0x000A ||
					ch == 0x000D) {
				output.append(ch);
			}
		}
		return output.toString();
	}

}
