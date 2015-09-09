package nl.xillio.xill.plugins.xml.services;

import com.google.inject.ImplementedBy;

import nl.xillio.xill.plugins.xml.XMLXillPlugin;
import nl.xillio.xill.plugins.xml.data.XmlNode;

import java.io.File;

/**
 * This interface represents some of the operations for the {@link XMLXillPlugin}.
 *
 * @author Zbynek Hochmann
 */

@ImplementedBy(NodeServiceImpl.class)
public interface NodeService {
	/**
	 * Inserts new node into existing XML document
	 * 
	 * @param parentXmlNode 			parent node
	 * @param newChildNodeStr 		XML definition of the new node
	 * @param beforeChildXmlNode 	optional child node that is used for positioning of the new node in the XML document
	 * @return newly created XML node
	 */
	XmlNode insertNode(final XmlNode parentXmlNode, final String newChildNodeStr, final XmlNode beforeChildXmlNode);

	/**
	 * Moves existing node to a new position in XML document
	 * 
	 * @param parentXmlNode	parent node
	 * @param subXmlNode		existing node that will be moved under parent node
	 * @param beforeXmlNode	optional child node that is used for positioning of the node in the XML document
	 */
	void moveNode(final XmlNode parentXmlNode, final XmlNode subXmlNode, final XmlNode beforeXmlNode);

	/**
	 * Replace existing node with new node 
	 * 
	 * @param orgXmlNode	the node that will be replaced by @replXmlStr
	 * @param replXmlStr	XML definition of the new node that will replace @orgXmlNode
	 * @return newly created XML node
	 */
	XmlNode replaceNode(final XmlNode orgXmlNode, final String replXmlStr);

	/**
	 * Removes existing node from XML document
	 * 
	 * @param xmlNode	the node that will be removed
	 */
	void removeNode(final XmlNode xmlNode);

	/**
	 * Add a new attribute to / set a new attribute value of - XML node
	 * 
	 * @param xmlNode		existing XML node
	 * @param attrName	attribute name
	 * @param attrValue	optional attribute value
	 */
	void setAttribute(final XmlNode xmlNode, final String attrName, final String attrValue);

	/**
	 * Removes attribute from XML node
	 * 
	 * @param xmlNode		existing XML node
	 * @param attrName	name of the attribute that will be removed
	 */
	void removeAttribute(final XmlNode xmlNode, final String attrName);
	
	/**
	 * Loads XML document from file, parse it and returns root node (XML document)
	 * 
	 * @param xmlSource file that contains valid XMl document
	 * @return newly created XML node representing root node of the entire document
	 */
	XmlNode fromFile(final File xmlSource);
	
	/**
	 * Creates XML document from string, parse it and returns root node (XML document)
	 * 
	 * @param xmlText string that contains valid XMl document
	 * @return newly created XML node representing root node of the entire document
	 */
	XmlNode fromString(final String xmlText);
}