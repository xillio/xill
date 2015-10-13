package nl.xillio.xill.util.settings;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;

import org.apache.commons.io.FileUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class ContentHandlerImpl implements ContentHandler {

	private File storage;
	private Document document;
	private TransformerFactory transformerFactory  = TransformerFactory.newInstance();
	private XPathFactory xpathFactory = XPathFactory.newInstance();
	
	private static String DEFAULTPW = "6WyMNf99H32Qn3ofZ32rxVNTXcd8sA6b";
	private static StandardPBEStringEncryptor encryptor;

	static {
		encryptor = new StandardPBEStringEncryptor();
		encryptor.setPassword(DEFAULTPW);
		encryptor.initialize();
	}	
	
	public ContentHandlerImpl(final String fileName) {
		this.storage = new File(fileName);
	}
	
	@Override
	public void init() throws Exception {
		
		try {
			DocumentBuilderFactory docFactory  = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory .newDocumentBuilder();
			
			if (!storage.exists()) {
				this.document = docBuilder.newDocument();
				this.createInnerStructure();
				this.save();
			} else {
				this.document = docBuilder.parse(FileUtils.openInputStream(storage));
			}
		} catch (Exception e) {
			throw new Exception("Cannot open settings file for reason: " + e.getMessage());
		}
	}

	private void createInnerStructure() {
		Element root = this.document.createElement("settings");
		this.document.appendChild(root);
	}

	private void save() throws Exception {
		try {
			Transformer transformer = this.transformerFactory.newTransformer();
			DOMSource source = new DOMSource(this.document);
			StreamResult result = new StreamResult(this.storage);
			transformer.transform(source, result);
		} catch (Exception e) {
			throw new Exception("Cannot save settings file for reason: " + e.getMessage());
		}			
	}
	
	@Override
	public Map<String, Object> get(final String category, final String keyValue) throws Exception {
		String path = String.format("%1$s/item", category);
		Object result = xpathFactory.newXPath().compile(path).evaluate(this.document.getFirstChild(), XPathConstants.NODESET);
		if (result == null) {
			return null;
		}
		if (result instanceof NodeList) {
			HashMap<String, Object> map = new HashMap<>();
			NodeList list = (NodeList) result;
			for (int i = 0; i < list.getLength(); i++) {
				Node node = list.item(i);
				Node attr = node.getAttributes().getNamedItem("key");
				if (attr != null) {
					if (this.keyValueMatch(node, attr.getNodeValue(), keyValue)) {
						Node child = node.getFirstChild();
						while (child != null) {
							map.put(child.getNodeName(), child.getTextContent());
							child = child.getNextSibling();
						}
						break;
					}
				}
			}
			return map;
		}
		throw new Exception("Invalid content structure of settings file!");
	}

	
	private boolean keyValueMatch(final Node item, final String keyName, final String keyValue) {
		Node child = item.getFirstChild();
		while (child != null) {
			if (child.getNodeName().equals(keyName)) {
				if (child.getTextContent().equals(keyValue)) {
					return true;
				}
			}
			child = child.getNextSibling();
		}
		return false;
	}
	
	@Override
	public List<Map<String, Object>> getAll(final String category) throws Exception {
		String path = String.format("%1$s/item", category);
		Object result = xpathFactory.newXPath().compile(path).evaluate(this.document.getFirstChild(), XPathConstants.NODESET);
		if (result == null) {
			return null;
		}
		if (result instanceof NodeList) {
			LinkedList<Map<String, Object>> output = new LinkedList<>();
			
			NodeList nodeList = (NodeList) result;
			for (int i = 0; i < nodeList.getLength(); i++) {
				Node parentNode = nodeList.item(i);
				HashMap<String, Object> map = new HashMap<>();
				Node node = parentNode.getFirstChild();
				while (node != null) {
					map.put(node.getNodeName(), node.getTextContent());
					node = node.getNextSibling();
				}
				output.add(map);
			}
			return output;
		}
		throw new Exception("Invalid content structure of settings file!");
	}
	
	@Override
	public boolean set(final String category, final Map<String, Object> itemContent, final String keyName, final String keyValue) throws Exception {
		
		boolean created = false;
		Node categoryNode = this.getCategoryNode(category);
		Element itemNode[] = {null};
		if (keyName != null) {
			itemNode[0] = this.getItemNode(categoryNode, keyName, keyValue);
		}
		if (itemNode[0] == null) {
			// creates new item node
			created = true;
			itemNode[0] = this.document.createElement("item");
			categoryNode.appendChild(itemNode[0]);
			if (keyName != null) {
				itemNode[0].setAttribute("key", keyName);
			}
		}

		itemContent.forEach( (k,v) -> set(itemNode[0], k, v));

		
		this.save();

		return created;
	}

	
	@Override
	public boolean exist(final String category, final String keyName, final String keyValue) throws Exception {
		String path = String.format("%1$s/item[@key='%2$s'][%2$s/text()='%3$s']", category, keyName, keyValue);
		Object result = xpathFactory.newXPath().compile(path).evaluate(this.document.getFirstChild(), XPathConstants.NODE);
		return (result != null);
	}
	
	@Override
	public boolean delete(final String category, final String keyName, final String keyValue) throws Exception {
		Node categoryNode = this.getCategoryNode(category);
		Element itemNode = this.getItemNode(categoryNode, keyName, keyValue);
		if (itemNode == null) {
			return false;
		}
		categoryNode.removeChild(itemNode);
		
		this.save();
		
		return true;
	}
	
	private void set(Element itemNode, final String nodeName, final Object nodeValue) {
		String path = String.format("%1$s", nodeName);
		Element node;
		try {
			node = (Element) xpathFactory.newXPath().compile(path).evaluate(itemNode, XPathConstants.NODE);
		} catch (XPathExpressionException e) {
			e.printStackTrace();
			return;
		}
		
		if (node == null) {
			// create new node
			node = this.document.createElement(nodeName);
			itemNode.appendChild(node);
		}

		String nodeValueStr = "";
		if (nodeValue != null) {
			nodeValueStr = nodeValue.toString();
		}
		
		Node text = node.getFirstChild();
		if (text == null) {
			// set new node text value
			node.appendChild(this.document.createTextNode(nodeValueStr));
		} else {
			// update existing node text value
			text.setNodeValue(nodeValueStr);
		}
	}

	public static synchronized String encrypt(final String value) {
		return encryptor.encrypt(value);
	}

	public static synchronized String decrypt(final String value) {
		if (value == null || value.isEmpty()) {
			return value;
		}
		return encryptor.decrypt(value);
	}
	
	private Element getItemNode(Node categoryNode, final String keyName, final String keyValue) throws XPathExpressionException {
		// find the item having key attribute=keyName and having tag that has name=keyName and text value=keyValue
		String path = String.format("item[@key='%1$s'][%1$s/text()='%2$s']", keyName, keyValue);
		return (Element) xpathFactory.newXPath().compile(path).evaluate(categoryNode, XPathConstants.NODE);
	}

	private Node getCategoryNode(final String category) throws XPathExpressionException {
		String path = String.format("/settings/%1$s", category);
		Node root = this.document.getFirstChild();
		Object o = xpathFactory.newXPath().compile(path).evaluate(root, XPathConstants.NODE);
		if (o == null) {
			Node categoryNode = this.document.createElement(category);
			root.appendChild(categoryNode);
			return categoryNode;
		} else {
			return (Node)o ;
		}
	}
}
