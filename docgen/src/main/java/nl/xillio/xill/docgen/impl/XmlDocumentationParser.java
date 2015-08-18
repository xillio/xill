package nl.xillio.xill.docgen.impl;

import nl.xillio.xill.docgen.DocumentationEntity;
import nl.xillio.xill.docgen.DocumentationParser;
import nl.xillio.xill.docgen.data.Example;
import nl.xillio.xill.docgen.data.ExampleNode;
import nl.xillio.xill.docgen.data.Parameter;
import nl.xillio.xill.docgen.data.Reference;
import nl.xillio.xill.docgen.exceptions.ParsingException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.*;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;

/**
 * The class represents a parser that will parse xml files into {@link DocumentationEntity}
 *
 * @author Thomas Biesaart
 * @author Ivor van der Hoog
 * @since 12-8-2015
 */
public class XmlDocumentationParser implements DocumentationParser {
	private static final Logger LOGGER = LogManager.getLogger();
	private final XPathFactory xpathFactory;
	private final DocumentBuilderFactory documentBuilderFactory;
	private XPathExpression parameterXPathQuery;
	private XPathExpression descriptionXPathQuery;
	private XPathExpression tagXPathQuery;
	private XPathExpression exampleNodesXPathQuery;

	/**
	 * The constructor for the parser when we hand it a factory.
	 *
	 * @param xpathFactory           The {@link XPathFactory} we want the parser to use.
	 * @param documentBuilderFactory
	 */
	public XmlDocumentationParser(final XPathFactory xpathFactory, DocumentBuilderFactory documentBuilderFactory) {
		this.xpathFactory = xpathFactory;
		this.documentBuilderFactory = documentBuilderFactory;
		try {
			buildQueries();
		} catch (XPathExpressionException e) {
			throw new IllegalStateException("Failed to build xPath queries", e);
		}
	}

	/**
	 * Instantiate a new XmlDocumentationParser using the default factories
	 */
	public XmlDocumentationParser() {
		this(XPathFactory.newInstance(), DocumentBuilderFactory.newInstance());
	}

	private void buildQueries() throws XPathExpressionException {
		XPath xPath = xpathFactory.newXPath();
		descriptionXPathQuery = xPath.compile("/function/description/text()");
		parameterXPathQuery = xPath.compile("/function/parameters/param");
		tagXPathQuery = xPath.compile("/function/tags");
		exampleNodesXPathQuery = xPath.compile("/function/examples/example");
	}


	@Override
	public DocumentationEntity parse(final URL resource, final String identity) throws ParsingException {
		try {
			DocumentBuilder docBuilder = documentBuilderFactory.newDocumentBuilder();
			Document doc = docBuilder.parse(openStream(resource));

			return doParse(doc, identity);
		} catch (IllegalArgumentException | IOException e) {
			throw new ParsingException("Failed to access XML for: " + identity, e);
		} catch (ParserConfigurationException | SAXException e) {
			throw new ParsingException("Failed to parse XML for: " + identity, e);
		}
	}

	InputStream openStream(URL url) throws IOException {
		return url.openStream();
	}

	DocumentationEntity doParse(final Document doc, final String identity) throws ParsingException {
		// Create XPathFactory object

		// Create XPath object
		XPath xpath = xpathFactory.newXPath();

		ConstructDocumentationEntity construct = new ConstructDocumentationEntity(identity);

		construct.setDescription(parseDescription(doc));
		construct.setParameters(parseParameters(doc));
		construct.setExamples(parseExamples(doc));
		construct.setReferences(parseReferences(doc));
		construct.setSearchTags(parseSearchTags(doc));

		return construct;
	}

	String parseDescription(final Document doc) throws ParsingException {
		try {
			return (String) descriptionXPathQuery.evaluate(doc, XPathConstants.STRING);
		} catch (XPathExpressionException e) {
			throw new ParsingException("Failed to parse description", e);
		}

	}

	List<Parameter> parseParameters(final Document doc) throws ParsingException {
		List<Parameter> parameters = new ArrayList<>();
		NodeList params;

		try {
			params = (NodeList) parameterXPathQuery.evaluate(doc, XPathConstants.NODESET);

		} catch (XPathExpressionException e) {
			throw new ParsingException("Failed to parse parameters", e);
		}

		for (int t = 0; t < params.getLength(); ++t) {
			tryParseParameter(parameters, params.item(t));
		}

		return parameters;
	}

	void tryParseParameter(final List<Parameter> target, final Node node) {
		try {
			target.add(parseParameter(node));
		} catch (NullPointerException e) {
			LOGGER.error("Failed to parse parameter", e);
		}
	}

	Parameter parseParameter(final Node node) {
		String name = getAttribute("name", node);
		String types = getAttributeOrNull("type", node);
		Parameter param = new Parameter(types, name);
		param.setDefault(getAttributeOrNull("default", node));
		param.setDescription(node.getTextContent());
		return param;
	}

	String getAttribute(final String name, final Node node) {
		String result = getAttributeOrNull(name, node);
		if (result == null) {
			throw new NullPointerException("Value of attribute `" + name + "` is null");
		}
		return result;
	}

	String getAttributeOrNull(final String name, final Node node) {
		NamedNodeMap attributes = node.getAttributes();
		Node attribute = attributes.getNamedItem(name);
		if (attribute == null) {
			return null;
		}

		return attribute.getNodeValue();
	}

	List<Example> parseExamples(final Document doc) throws ParsingException {
		List<Example> examples = new ArrayList<>();
		NodeList exampleNodes;
		try {
			exampleNodes = (NodeList) exampleNodesXPathQuery.evaluate(doc, XPathConstants.NODESET);

			for (int t = 0; t < exampleNodes.getLength(); ++t) {
				examples.add(parseExample(exampleNodes.item(t)));
			}
		} catch (XPathExpressionException | NullPointerException e) {
			throw new ParsingException("Failed to parse examples", e);
		}

		return examples;
	}

	Example parseExample(final Node node) {
		// Get the example title
		Example example = new Example(getAttributeOrNull("title", node));
		NodeList exampleContent = node.getChildNodes();
		for (int t = 0; t < exampleContent.getLength(); ++t) {
			Node item = exampleContent.item(t);
			if (item.getNodeName() != null && !item.getNodeName().startsWith("#")) {
				example.addContent(new ExampleNode(exampleContent.item(t).getNodeName(),
					exampleContent.item(t).getTextContent()));
			}
		}
		return example;
	}

	List<Reference> parseReferences(final Document doc) throws ParsingException {
		List<Reference> references = new ArrayList<>();
		NodeList exampleNodes;
		try {
			exampleNodes = (NodeList) parameterXPathQuery.evaluate(doc, XPathConstants.NODESET);
		} catch (XPathExpressionException | IllegalArgumentException | NullPointerException e) {
			throw new ParsingException("Failed to parse references", e);
		}

		for (int t = 0; t < exampleNodes.getLength(); ++t) {
			try {
				references.add(parseReference(exampleNodes.item(t)));
			} catch (ParsingException | NullPointerException e) {
				LOGGER.error("Failed to parse reference", e);
			}
		}

		return references;
	}

	Reference parseReference(final Node node) throws ParsingException {
		String[] reference = node.getTextContent().trim().split("\\.");
		if (reference.length == 0) {
			throw new ParsingException("Failed to parse reference because no content was found");
		}

		String packageName = null;
		String constructName = reference[reference.length - 1];
		if (reference.length > 1) {
			packageName = reference[0];
		}

		return new Reference(packageName, constructName);
	}

	Set<String> parseSearchTags(final Document doc) throws ParsingException {
		String[] searchTags = new String[0];
		try {
			Node searchTagNode = (Node) tagXPathQuery.evaluate(doc, XPathConstants.NODE);
			if (searchTagNode != null) {
				searchTags = searchTagNode.getTextContent().replaceAll("\\s", "").split(",");
			}
		} catch (XPathExpressionException | NullPointerException e) {
			throw new ParsingException("Failed to parse searchtags", e);
		}

		return new HashSet<>(Arrays.asList(searchTags));
	}
}
