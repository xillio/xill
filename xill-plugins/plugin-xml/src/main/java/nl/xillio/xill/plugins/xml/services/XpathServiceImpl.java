package nl.xillio.xill.plugins.xml.services;

import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.xml.data.XmlNode;
import com.google.inject.Singleton;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import javax.xml.xpath.XPathFactoryConfigurationException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import net.sf.saxon.lib.NamespaceConstant;

/**
 * This class is the main implementation of the {@link XpathService}
 *
 * @author Zbynek Hochmann
 */

@Singleton
public class XpathServiceImpl implements XpathService {

	private static XPathFactory xpf = null;


	private static final Logger LOGGER = LogManager.getLogger();

	static {
		System.setProperty("javax.xml.xpath.XPathFactory:" + NamespaceConstant.OBJECT_MODEL_SAXON, "net.sf.saxon.xpath.XPathFactoryImpl");
		try {
			xpf = XPathFactory.newInstance(NamespaceConstant.OBJECT_MODEL_SAXON);
		} catch (XPathFactoryConfigurationException e) {
			LOGGER.error("Could not initialise XPath", e);
		}
	}

	@Override
	public List<Object> xpath(final XmlNode node, final String xpathQuery, final Map<String, String> namespaces) {
		HTMLNamespaceContext namespaceContext = new HTMLNamespaceContext(namespaces);
		XPath xpath = xpf.newXPath();
		ArrayList<Object> output = new ArrayList<Object>(); 
		
		boolean fetchText = xpathQuery.endsWith("/text()");

		// More hacking... the java implementation bugs out on selecting a CDATA textnode.
		// We will need to first query the node, then do another query to fetch the textual content.
		String query = xpathQuery;
		if (fetchText) {
			query = xpathQuery.substring(0, xpathQuery.length() - "/text()".length());
		}

		try {
			Document document = node.getDocument();
			namespaceContext.setDocument(document);

			Object result = this.xPath(xpath, node.getNode(), query);

			if (result instanceof NodeList) {
				NodeList results = (NodeList) result;

				for (int i = 0; i < results.getLength(); i++) {
					Node n = results.item(i);
					output.add(fetchText ? xPathText(xpath, n, "./text()") : parseVariable(n));
				}
			} else {
				output.add(result.toString());
			}
		} catch (XPathExpressionException e) {
			throw new RobotRuntimeException("Invalid XPath", e);
		}

		return output;
	}

	private Object xPath(final XPath xpath, final Object node, final String expression) throws XPathExpressionException {
		XPathExpression expr;
		try {
			expr = xpath.compile(expression);
		} catch (Exception e) { // Sometimes, an unexpected net.sf.saxon.trans.XPathException can be thrown...
			LOGGER.error("Failed to run xpath expression", e);
			throw new XPathExpressionException(e.getMessage());
		}

		try {
			return expr.evaluate(node, XPathConstants.NODESET);
		} catch (Exception e) {
			LOGGER.warn("Exception while evaluating xpath expression", e);
		}

		return expr.evaluate(node, XPathConstants.STRING);
	}

	private String xPathText(final XPath xpath, final Object node, final String expression) throws XPathExpressionException {
		return xpath.compile(expression).evaluate(node).trim();
	}

	private static Object parseVariable(final Node node) {
		switch (node.getNodeType()) {
			case Node.COMMENT_NODE:
			case Node.ATTRIBUTE_NODE:
			case Node.CDATA_SECTION_NODE:
			case Node.TEXT_NODE:
				return node.getNodeValue();
			default:
				return new XmlNode(node);
		}
	}	

	/**
	 * Innerclass for handling XML namespaces
	 */
	private class HTMLNamespaceContext implements NamespaceContext {
		private Document document;
		private static final String URI = "http://www.w3.org/1999/xhtml";
		private final Map<String, String> namespaces;

		public HTMLNamespaceContext(final Map<String, String> namespaces) {
			this.namespaces = namespaces;
		}

		@Override
		public String getNamespaceURI(final String prefix) {
			if (document == null) {
				return URI;
			} else if (prefix == null || prefix.equals(XMLConstants.DEFAULT_NS_PREFIX)) {
				return document.lookupNamespaceURI(null);
			} else if (namespaces.containsKey(prefix)) {
				return namespaces.get(prefix);
			} else {
				return document.lookupNamespaceURI(prefix);
			}
		}

		@Override
		public String getPrefix(final String namespaceURI) {
			return document == null ? URI : document.lookupPrefix(namespaceURI);
		}

		@Override
		public Iterator<?> getPrefixes(final String arg0) {
			return null;
		}

		public void setDocument(final Document document) {
			this.document = document;
		}
	}

}