package nl.xillio.xill.plugins.xml.data;

import nl.xillio.xill.plugins.xml.exceptions.XmlParseException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXParseException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringReader;
import java.io.StringWriter;

/**
 * This class represents a XML node MetadataExpression
 *
 * @author Zbynek Hochmann
 */
public class XmlNodeVar implements nl.xillio.xill.api.data.XmlNode {

	private static final Logger LOGGER = LogManager.getLogger();
	private Node node = null;

	TransformerFactory tf = TransformerFactory.newInstance();

	/**
	 * Creates XmlNode from XML string
	 *
	 * @param xmlString XML document
	 * @throws Exception         when any unspecified error occurs
	 * @throws XmlParseException when XML format is invalid
	 */
	public XmlNodeVar(final String xmlString) throws Exception, XmlParseException {
		String xmlStringValue = xmlCharacterWhitelist(xmlString);

		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setNamespaceAware(true);
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document document = db.parse(new InputSource(new StringReader(xmlStringValue)));

			// Normalize whitespace nodes
			removeEmptyTextNodes(document);
			document.normalize();
			this.node = document.getFirstChild();

		} catch (SAXParseException e) {
			throw new XmlParseException(e.getMessage(), e);
		}
	}

	/**
	 * Creates XmlNode from org.w3c.dom.Node
	 *
	 * @param node input node
	 */
	public XmlNodeVar(Node node) {
		this.node = node;
	}

	/**
	 * Returns XML document of this node
	 *
	 * @return org.w3c.dom.Document of this node
	 */
	public Document getDocument() {
		return this.node.getOwnerDocument();
	}

	/**
	 * @return org.w3c.dom.Node data specifying this node
	 */
	public Node getNode() {
		return this.node;
	}

	@Override
	public String toString() {
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
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.transform(domSource, result);
			return writer.toString();
		} catch (Exception e) {
			LOGGER.error("Error while formatting XML", e);
		}
		return null;
	}

	private void removeEmptyTextNodes(final Node parent) {
		for (int i = 0; i < parent.getChildNodes().getLength(); i++) {
			Node child = parent.getChildNodes().item(i);
			if (child.hasChildNodes()) {
				removeEmptyTextNodes(child);
			} else if (child.getNodeType() == Node.TEXT_NODE && child.getNodeValue().trim().isEmpty()) {
				parent.removeChild(child);
				i--;
			}
		}
	}

	private static String xmlCharacterWhitelist(final String inputString) {
		if (inputString == null) {
			return null;
		}

		StringBuilder output = new StringBuilder();
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
