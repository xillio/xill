package nl.xillio.xill.docgen.impl;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
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

	@Override
	public DocumentationEntity parse(final URL resource, final String identity) {
		try {
			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder;
			docBuilder = docBuilderFactory.newDocumentBuilder();
			Document doc = docBuilder.parse(resource.openStream());

			return tryParse(doc, identity);
		} catch (IllegalArgumentException | IOException e) {
			LOGGER.error("Failed to access XML for: " + identity, e);
		} catch (ParserConfigurationException | SAXException e) {
			LOGGER.error("Failed to parse XML for: " + identity, e);
		}
		return null;
	}

	private ConstructDocumentationEntity tryParse(final Document doc, final String identity) {
		// Create XPathFactory object
		XPathFactory xpathFactory = XPathFactory.newInstance();

		// Create XPath object
		XPath xpath = xpathFactory.newXPath();

		ConstructDocumentationEntity construct = new ConstructDocumentationEntity(identity);

		construct.setDescription(parseDescription(doc, xpath, identity));
		construct.setParameters(parseParameters(doc, xpath, identity));
		construct.setExamples(parseExamples(doc, xpath, identity));
		construct.setReferences(parseReferences(doc, xpath, identity));
		construct.setSearchTags(parseSearchTags(doc, xpath, identity));

		return null;
	}

	private String parseDescription(final Document doc, final XPath xpath, final String identity) {
		try {
			XPathExpression descriptionExpr = xpath.compile("/function/description/text()");
			return ((String) descriptionExpr.evaluate(doc, XPathConstants.STRING)).trim();
		} catch (XPathExpressionException e) {
			LOGGER.error("Failed to execute description xpath at: " + identity, e);
		}
		return null;
	}

	private List<Parameter> parseParameters(final Document doc, final XPath xpath, final String identity) {
		try {
			List<Parameter> parameters = new ArrayList<>();
			NodeList params = (NodeList) xpath.compile("/function/parameters/param").evaluate(doc, XPathConstants.NODESET);

			for (int t = 0; t < params.getLength(); ++t) {
				parameters.add(parseParameter(params.item(t)));
			}

			return parameters;
		} catch (XPathExpressionException e) {
			LOGGER.error("Failed to execute a parameter xpath at: " + identity, e);
		}
		return null;
	}

	private Parameter parseParameter(final Node node) {
		Parameter param = new Parameter();
		param.setName(node.getAttributes().getNamedItem("name").getNodeValue());
		param.setDefault(node.getAttributes().getNamedItem("default").getNodeValue());
		param.setType(node.getAttributes().getNamedItem("type").getNodeValue());
		return param;
	}

	private List<Example> parseExamples(final Document doc, final XPath xpath, final String identity) {
		try {
			List<Example> examples = new ArrayList<>();
			NodeList exampleNodes = (NodeList) xpath.compile("/function/examples/example").evaluate(doc, XPathConstants.NODESET);

			for (int t = 0; t < exampleNodes.getLength(); ++t) {
				examples.add(parseExample(exampleNodes.item(t)));
			}
			return examples;
		} catch (XPathExpressionException e) {
			LOGGER.error("Failed to parse a parameter at " + identity, e);
		}
		return null;
	}

	private Example parseExample(final Node node) {
		Example example = new Example();
		NodeList exampleContent = node.getChildNodes();
		for (int t = 0; t < exampleContent.getLength(); ++t) {
			example.addContent(new ExampleNode(exampleContent.item(t).getNodeName(),
				exampleContent.item(t).getNodeValue()));
		}
		return null;
	}

	private List<Reference> parseReferences(final Document doc, final XPath xpath, final String identity) {
		try {
			List<Reference> references = new ArrayList<>();
			NodeList exampleNodes = (NodeList) xpath.compile("/function/references/reference").evaluate(doc, XPathConstants.NODESET);

			for (int t = 0; t < exampleNodes.getLength(); ++t) {
				references.add(parseReference(exampleNodes.item(t)));
			}
			return references;
		} catch (XPathExpressionException | ParsingException e) {
			LOGGER.error("Failed to parse a reference at " + identity, e);
		}
		return null;
	}

	private Reference parseReference(final Node node) throws ParsingException {
		String[] reference = node.getNodeValue().split(".");
		if (reference.length != 2) {
			throw new ParsingException("Incorrect reference in xml. More than one or no '.' found.");
		}
		return new Reference(reference[0], reference[1]);
	}

	private Set<String> parseSearchTags(final Document doc, final XPath xpath, final String identity) {
		try {
			Set<String> searchTags = new HashSet<>();
			XPathExpression searchTagsExpr = xpath.compile("/function/searchTags/searchTag");
			NodeList searchTagNodes = (NodeList) searchTagsExpr.evaluate(doc, XPathConstants.NODESET);
			for (int t = 0; t < searchTagNodes.getLength(); ++t) {
				searchTags.add(searchTagNodes.item(t).getTextContent().trim());
			}
			return searchTags;
		} catch (XPathExpressionException e) {
			LOGGER.error("Failed to parse a searchtag at " + identity, e);
		}
		return null;
	}
}
