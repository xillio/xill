package nl.xillio.xill.docgen.impl;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

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

/**
 * //TODO javadoc
 *
 * @author Thomas Biesaart
 * @author Ivor van der Hoog
 * @since 12-8-2015
 */
public class XmlDocumentationParser implements DocumentationParser {
	private static final Logger LOGGER = LogManager.getLogger();
	private final XPathFactory xpathFactory;

	/**
	 * The constructor for the parser when we hand it a factory.
	 * 
	 * @param xpathFactory
	 *        The {@link XPathFactory} we want the parser to use.
	 */
	public XmlDocumentationParser(final XPathFactory xpathFactory) {
		this.xpathFactory = xpathFactory;
	}

	/**
	 * TODO better javadoc.
	 * The empty constructor for the parser.
	 */
	public XmlDocumentationParser() {
		xpathFactory = XPathFactory.newInstance();
	}

	@Override
	public DocumentationEntity parse(final URL resource, final String identity) throws ParsingException {
		try {
			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder;
			docBuilder = docBuilderFactory.newDocumentBuilder();
			Document doc = docBuilder.parse(resource.openStream());

			return doParse(doc, identity);
		} catch (IllegalArgumentException | IOException e) {
			throw new ParsingException("Failed to access XML for: " + resource.toExternalForm(), e);
		} catch (ParserConfigurationException | SAXException e) {
			throw new ParsingException("Failed to parse XML for: " + identity, e);
		}
	}

	DocumentationEntity doParse(final Document doc, final String identity) throws ParsingException {
		// Create XPathFactory object

		// Create XPath object
		XPath xpath = xpathFactory.newXPath();

		ConstructDocumentationEntity construct = new ConstructDocumentationEntity(identity);

		construct.setDescription(parseDescription(doc, xpath));
		construct.setParameters(parseParameters(doc, xpath));
		construct.setExamples(parseExamples(doc, xpath));
		construct.setReferences(parseReferences(doc, xpath));
		construct.setSearchTags(parseSearchTags(doc, xpath));

		return construct;
	}

	String parseDescription(final Document doc, final XPath xpath) throws ParsingException {
		try {
			XPathExpression descriptionExpr = xpath.compile("/function/description/text()");
			return (String) descriptionExpr.evaluate(doc, XPathConstants.STRING);
		} catch (XPathExpressionException | NullPointerException e) {
			throw new ParsingException("Failed to parse description", e);
		}

	}

	List<Parameter> parseParameters(final Document doc, final XPath xpath) throws ParsingException {
		List<Parameter> parameters = new ArrayList<>();
		NodeList params;

		try {
			params = (NodeList) xpath.compile("/function/parameters/param").evaluate(doc, XPathConstants.NODESET);

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

	List<Example> parseExamples(final Document doc, final XPath xpath) throws ParsingException {
		List<Example> examples = new ArrayList<>();
		NodeList exampleNodes;
		try {
			exampleNodes = (NodeList) xpath.compile("/function/examples/example").evaluate(doc, XPathConstants.NODESET);

			for (int t = 0; t < exampleNodes.getLength(); ++t) {
				examples.add(parseExample(exampleNodes.item(t)));
			}
		} catch (XPathExpressionException | NullPointerException e) {
			throw new ParsingException("Failed to parse examples", e);
		}

		return examples;
	}

	Example parseExample(final Node node) throws NullPointerException {
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

	List<Reference> parseReferences(final Document doc, final XPath xpath) throws ParsingException {
		List<Reference> references = new ArrayList<>();
		NodeList exampleNodes;
		try {
			exampleNodes = (NodeList) xpath.compile("/function/references/reference").evaluate(doc, XPathConstants.NODESET);

			for (int t = 0; t < exampleNodes.getLength(); ++t) {
				try {
					references.add(parseReference(exampleNodes.item(t)));
				} catch (ParsingException | NullPointerException e) {
					LOGGER.error("Failed to parse reference", e);
				}
			}
		} catch (XPathExpressionException | IllegalArgumentException | NullPointerException e) {
			throw new ParsingException("Failed to parse references", e);
		}

		return references;
	}

	Reference parseReference(final Node node) throws ParsingException, NullPointerException {
		String[] reference = node.getTextContent().trim().split("\\.");
		if (reference.length == 1) {
			return new Reference(null, reference[0]);
		} else if (reference.length == 2) {
			return new Reference(reference[0], reference[1]);
		} else {
			throw new ParsingException("Invalid reference format");
		}
	}

	Set<String> parseSearchTags(final Document doc, final XPath xpath) throws ParsingException {
		String[] searchTags = new String[0];
		try {
			XPathExpression searchTagsExpr = xpath.compile("/function/tags");
			Node searchTagNode = (Node) searchTagsExpr.evaluate(doc, XPathConstants.NODE);
			if (searchTagNode != null) {
				searchTags = searchTagNode.getTextContent().replaceAll("\\s", "").split(",");
			}
		} catch (XPathExpressionException | NullPointerException e) {
			throw new ParsingException("Failed to parse searchtags", e);
		}

		return new HashSet<>(Arrays.asList(searchTags));
	}
}
