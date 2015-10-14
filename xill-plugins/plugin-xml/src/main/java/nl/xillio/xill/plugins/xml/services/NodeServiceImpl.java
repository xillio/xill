package nl.xillio.xill.plugins.xml.services;

import java.io.File;
import java.io.IOException;

import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.api.data.XmlNode;
import nl.xillio.xill.plugins.xml.data.XmlNodeVar;
import nl.xillio.xill.plugins.xml.exceptions.XmlParseException;

import org.apache.commons.io.FileUtils;
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
	public XmlNode insertNode(final XmlNode parentXmlNode, final String newChildNodeStr, final XmlNode beforeChildXmlNode) {
		XmlNodeVar newXmlChildNode = null;
		try {
			newXmlChildNode = new XmlNodeVar(newChildNodeStr, false);
		} catch (XmlParseException e) {
			throw new RobotRuntimeException("Function insertNode parse error!\n" + e.getMessage(), e);
		} catch (Exception e) {
			throw new RobotRuntimeException("Function insertNode error!\n" + e.getMessage(), e);
		}

		Node parentNode = parentXmlNode.getNode();
		Node beforeChildNode = beforeChildXmlNode == null ? null : beforeChildXmlNode.getNode();
		Node newNode = parentXmlNode.getDocument().importNode(newXmlChildNode.getNode(), true);

		if (beforeChildNode == null || !beforeChildNode.getParentNode().equals(parentNode)) {
			parentNode.appendChild(newNode);
		} else {
			parentNode.insertBefore(newNode, beforeChildNode);
		}

		return new XmlNodeVar(newNode);
	}

	@Override
	public void moveNode(final XmlNode parentXmlNode, final XmlNode subXmlNode, final XmlNode beforeXmlNode) {
		Node parentNode = parentXmlNode.getNode();
		Node subNode = subXmlNode.getNode();
		Node beforeNode = beforeXmlNode == null ? null : beforeXmlNode.getNode();

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

	@Override
	public XmlNode replaceNode(final XmlNode orgXmlNode, final String replXmlStr) {
		XmlNodeVar replXmlNode = null;
		try {
			replXmlNode = new XmlNodeVar(replXmlStr, false);
		} catch (XmlParseException e) {
			throw new RobotRuntimeException("Function replaceNode parse error!\n" + e.getMessage(), e);
		} catch (Exception e) {
			throw new RobotRuntimeException("Function replaceNode error!\n" + e.getMessage(), e);
		}			
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
	public boolean removeAttribute(final XmlNode xmlNode, final String attrName) {
		NamedNodeMap attributes = xmlNode.getNode().getAttributes();
		if (attributes.getNamedItem(attrName) == null) {
			return false;
		} else {
			attributes.removeNamedItem(attrName);
			return true;
		}
	}

	@Override
	public XmlNode fromFile(final File xmlSource) {
		String content;

		try {
			content = FileUtils.readFileToString(xmlSource).replaceAll("[^\\x20-\\x7e]", "");
		} catch (IOException e) {
			throw new RobotRuntimeException("Read file error.", e);
		}

		if (content.isEmpty()) {
			throw new RobotRuntimeException("The file is empty.");
		}

		try {
			return new XmlNodeVar(content, true);
		} catch (XmlParseException e) {
			throw new RobotRuntimeException("The XML source is invalid.", e);
		} catch (Exception e) {
			throw new RobotRuntimeException("Error occured.", e);
		}
	}

	@Override
	public XmlNode fromString(final String xmlText) {
		try {
			return new XmlNodeVar(xmlText, true);
		} catch (XmlParseException e) {
			throw new RobotRuntimeException("The XML source is invalid." + e.getMessage(), e);
		} catch (Exception e) {
			throw new RobotRuntimeException("Error occured.", e);
		}
	}
}
