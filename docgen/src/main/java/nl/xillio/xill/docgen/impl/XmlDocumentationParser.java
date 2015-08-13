package nl.xillio.xill.docgen.impl;

import java.io.IOException;
import java.net.URL;
import java.util.*;

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
	private XPathFactory xpathFactory = XPathFactory.newInstance();
	
	/**
	 * The constructor for the parser when we hand it a factory.
	 * @param xpathFactory
	 * 					The {@link XPathFactory} we want the parser to use.
	 */
	public XmlDocumentationParser(XPathFactory xpathFactory){
		this.xpathFactory = xpathFactory;
	}
	
	
	/**
	 * TODO better javadoc.
	 * The empty constructor for the parser.
	 */
	public XmlDocumentationParser(){
		
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


	private DocumentationEntity doParse(final Document doc, final String identity) throws ParsingException {

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

	private String parseDescription(final Document doc, final XPath xpath) throws ParsingException {
		try {
			XPathExpression descriptionExpr = xpath.compile("/function/description/text()");
			String result = (String) descriptionExpr.evaluate(doc, XPathConstants.STRING);
			return result.trim();
		} catch (XPathExpressionException | NullPointerException e) {
			throw new ParsingException("Failed to parse the description", e);
		}
	}

	private List<Parameter> parseParameters(final Document doc, final XPath xpath) throws ParsingException {
		List<Parameter> parameters = new ArrayList<>();
		NodeList params;

		try {
			params = (NodeList) xpath.compile("/function/parameters/param").evaluate(doc, XPathConstants.NODESET);
			
			for (int t = 0; t < params.getLength(); ++t) {
				addParameter(parameters, params.item(t));
			}
		} catch (XPathExpressionException  | NullPointerException e) {
			throw new ParsingException("Failed to parse parameters", e);
		}
		return parameters;
	}
	
	private void addParameter(final List<Parameter> parameters, final Node node){
		try {
			parameters.add(parseParameter(node));
		}catch(NullPointerException e) {
			LOGGER.error("Failed to parse a parameter", e);
		}
	}

	private Parameter parseParameter(final Node node) throws NullPointerException {
		String name = getAttribute("name", node);
		String types = getAttributeOrNull("type", node);
		Parameter param = new Parameter(types, name);
		param.setDefault(getAttributeOrNull("default", node));
		param.setDescription(node.getTextContent());
		return param;
	}

	private String getAttribute(String name, Node node) {
		String result = getAttributeOrNull(name ,node);
		if(result == null) {
			throw new NullPointerException("Value of attribute `" + name + "` is null");
		}
		return result;
	}

	private String getAttributeOrNull(String name, Node node) {
		NamedNodeMap attributes = node.getAttributes();
		Node attribute = attributes.getNamedItem(name);
		if(attribute == null) {
			return null;
		}

		return attribute.getNodeValue();
	}

	private List<Example> parseExamples(final Document doc, final XPath xpath) throws ParsingException {
		List<Example> examples = new ArrayList<>();
		NodeList exampleNodes;
		try {
			exampleNodes = (NodeList) xpath.compile("/function/examples/example").evaluate(doc, XPathConstants.NODESET);
			
			for (int t = 0; t < exampleNodes.getLength(); ++t) {
				examples.add(parseExample(exampleNodes.item(t)));
			}
			return examples;
		} catch (XPathExpressionException | NullPointerException e) {
			throw new ParsingException("Failed to parse examples", e);
		}
	}

	private Example parseExample(final Node node) {
		Example example = new Example();
		NodeList exampleContent = node.getChildNodes();
		for (int t = 0; t < exampleContent.getLength(); ++t) {
			example.addContent(new ExampleNode(exampleContent.item(t).getNodeName(),
				exampleContent.item(t).getNodeValue()));
		}
		return example;
	}

	private List<Reference> parseReferences(final Document doc, final XPath xpath) throws ParsingException {
		List<Reference> references = new ArrayList<>();
		NodeList exampleNodes;

		try {
			exampleNodes = (NodeList) xpath.compile("/function/references/reference").evaluate(doc, XPathConstants.NODESET);
			
			for (int t = 0; t < exampleNodes.getLength(); ++t) {
				addReference(references, exampleNodes.item(t));
			}
		}catch(XPathExpressionException | IllegalArgumentException | NullPointerException e) {
			throw new ParsingException("Failed to parse references", e);
		}
		return references;
	}
	
	private void addReference(final List<Reference> references, final Node node){
		try {
			references.add(parseReference(node));
		}catch (ParsingException | NullPointerException e) {
			LOGGER.error("Failed to parse reference", e);
		}
	}

	private Reference parseReference(final Node node) throws ParsingException, NullPointerException {
		String[] reference = node.getTextContent().split("\\.");
		if (reference.length != 2) {
			throw new ParsingException("Incorrect reference in xml. More than one or no '.' found.");
		}
		return new Reference(reference[0], reference[1]);
	}

	private Set<String> parseSearchTags(final Document doc, final XPath xpath) throws ParsingException {
		Node searchTagNode;
		try {
			String[] searchTags = new String[0];
			XPathExpression searchTagsExpr = xpath.compile("/function/searchTags");
			searchTagNode = (Node) searchTagsExpr.evaluate(doc, XPathConstants.NODE);
			String searchTagsContent = searchTagNode.getTextContent();
			if(searchTagsContent != null){
				searchTags = searchTagNode.getTextContent().split("\\.");
			}
			return new HashSet<>(Arrays.asList(searchTags));
		}catch(XPathExpressionException | NullPointerException e) {
			throw new ParsingException("Failed to parse searchTags", e);
		}
	}
}
