package nl.xillio.xill.plugins.xml.data;

import nl.xillio.xill.api.preview.TextPreview;
import nl.xillio.xill.plugins.xml.exceptions.XmlParseException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
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
public class XmlNodeVar implements nl.xillio.xill.api.data.XmlNode, TextPreview {

    private static final Logger LOGGER = LogManager.getLogger();
    private Node node = null;
    private boolean treatAsDocument = false;

    TransformerFactory tf = TransformerFactory.newInstance();

    /**
     * Creates XmlNode from XML string
     *
     * @param xmlString XML document
     * @throws Exception         when any unspecified error occurs
     * @throws XmlParseException when XML format is invalid
     */
    public XmlNodeVar(final String xmlString, final boolean treatAsDocument) throws Exception, XmlParseException {
        this.treatAsDocument = treatAsDocument;

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
        if (this.treatAsDocument) {
            return String.format("XML Document[first node = %1$s]", this.node.getNodeName());
        } else {
            return String.format("XML Node[%1$s]", this.node.getNodeName());
        }
    }

    /**
     * @return a string containing all text extracted from XML node or XML document
     */
    public String getText() {
        String itemText, text = "";
        NodeList list = this.getNode().getChildNodes();
        for(int i=0; i<list.getLength(); i++) {
            itemText = list.item(i).getTextContent();
            if (!itemText.isEmpty()) {
                if (!text.isEmpty()) {
                    text += "\n";
                }
                text += itemText;
            }
        }
        return text;
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
            DOMSource domSource = new DOMSource(this.treatAsDocument ? this.getDocument() : this.node);
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

    @Override
    public String getTextPreview() {
        return getXmlContent();
    }

}
