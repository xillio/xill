package nl.xillio.xill.plugins.xml.services;

import nl.xillio.xill.plugins.xml.data.XmlNode;
import nl.xillio.xill.plugins.xml.exceptions.XmlParseException;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import com.google.inject.Singleton;

/**
 * This class is the main implementation of the {@link NodeService}
 *
 * @author Zbynek Hochmann
 */

@Singleton
public class NodeServiceImpl implements NodeService {

	@Override 
	public XmlNode insertNode(final XmlNode parentXmlNode, final String newChildNodeStr, final XmlNode beforeChildXmlNode) throws Exception, XmlParseException {
		XmlNode newXmlChildNode = new XmlNode(newChildNodeStr);
		
		Node parentNode = parentXmlNode.getNode();
		Node beforeChildNode = beforeChildXmlNode == null ? null : beforeChildXmlNode.getNode();
		Node newNode = parentXmlNode.getDocument().importNode(newXmlChildNode.getNode(), true);
		
		if (beforeChildNode == null || !beforeChildNode.getParentNode().equals(parentNode)) {
			parentNode.appendChild(newNode);
		} else {
			parentNode.insertBefore(newNode, beforeChildNode);
		}

		return newXmlChildNode;
	}

	public void moveNode(final XmlNode parentXmlNode, final XmlNode subXmlNode, final XmlNode beforeXmlNode) {
		Node parentNode = parentXmlNode.getNode();
		Node subNode = subXmlNode.getNode();
		Node beforeNode = (beforeXmlNode == null ? null : beforeXmlNode.getNode());
		
		// If by accident we ended up with a document, get the root child node
		if (subNode instanceof Document) {
			subNode = subNode.getFirstChild();
		}

		// If we are moving/copying the node from one document to another, register the node first.
		if (!parentNode.getOwnerDocument().equals(subNode.getOwnerDocument())) {
			subNode = parentNode.getOwnerDocument().importNode(subNode, true);
		}

		// Register the node at the new position
		if (beforeNode == null || !beforeNode.getParentNode().equals(parentNode)) {
			parentNode.appendChild(subNode);
		} else {
			parentNode.insertBefore(subNode, beforeNode);
		}
	}

	public XmlNode replaceNode(final XmlNode orgXmlNode, final String replXmlStr)  throws Exception, XmlParseException {
		XmlNode replXmlNode = new XmlNode(replXmlStr);
		Node orgNode = orgXmlNode.getNode();
		Node newReplNode = orgXmlNode.getDocument().importNode(replXmlNode.getNode(), true);
		orgNode.getParentNode().replaceChild(newReplNode, orgNode);
		return replXmlNode;
	}

	@Override
	public void removeNode(final XmlNode xmlNode) {
		Node node = xmlNode.getNode();
		node.getParentNode().removeChild(node);
	}

	@Override
	public void setAttribute(final XmlNode xmlNode, final String attrName, final String attrValue) {
		NamedNodeMap attributes = xmlNode.getNode().getAttributes();
		Node attNode = xmlNode.getDocument().createAttribute(attrName);
		attNode.setNodeValue(attrValue);
		attributes.setNamedItem(attNode);
	}

	@Override
	public void removeAttribute(final XmlNode xmlNode, final String attrName) {
		xmlNode.getNode().getAttributes().removeNamedItem(attrName);
	}

}
