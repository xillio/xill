package nl.xillio.xill.docgen.impl;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import nl.xillio.xill.docgen.data.Example;
import nl.xillio.xill.docgen.data.Parameter;
import nl.xillio.xill.docgen.data.Reference;
import nl.xillio.xill.docgen.exceptions.ParsingException;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *Test the {@link XmlDocumentationParser}.
 */
public class XmlDocumentationParserTest {

	/**
	 * TODO make the javadoc correct
	 * Tests the process with normal usage. Every xpathquery gives null but the parser should be able to handle that.
	 * 
	 * @throws ParsingException
	 * @throws MalformedURLException
	 * @throws XPathExpressionException
	 */
	@Test
	public void testParsingWithNormalUsage() throws ParsingException, MalformedURLException, XPathExpressionException {
		// mock

		// The xpath

		XPath xpath = setupXPath();
		XPathFactory factory = mock(XPathFactory.class);
		when(factory.newXPath()).thenReturn(xpath);

		// the url resource
		URL resource = new URL("http://www.w3schools.com:80/xml/note.xml");

		// run
		XmlDocumentationParser parser = setupParser(factory);
		parser.parse(resource, "functionName");
	}

	/**
	 * Test the parser then it cannot find a description node.
	 * 
	 * @throws XPathExpressionException
	 * @throws ParsingException
	 * @throws MalformedURLException
	 */
	@Test(expectedExceptions = ParsingException.class, expectedExceptionsMessageRegExp = "Failed to parse description")
	public void testNullPointerWhenParsingDescription() throws XPathExpressionException, ParsingException, MalformedURLException {
		// mock

		// The Document
		Document doc = mock(Document.class);

		// The parser
		XPath xpath = setupXPath();
		XPathFactory factory = mock(XPathFactory.class);
		when(factory.newXPath()).thenReturn(xpath);
		XmlDocumentationParser parser = setupParser(factory);

		// The evaluation of the description.
		when(xpath.compile("/function/description/text()")).thenReturn(null);
		// run
		parser.parseDescription(doc, xpath);
	}

	/**
	 * Test the parser then it cannot find a description node.
	 * 
	 * @throws XPathExpressionException
	 * @throws ParsingException
	 * @throws MalformedURLException
	 */
	@Test(expectedExceptions = ParsingException.class, expectedExceptionsMessageRegExp = "Failed to parse description")
	public void testXPathExceptionWhenParsingDescription() throws XPathExpressionException, ParsingException, MalformedURLException {
		// mock

		// The Document
		Document doc = mock(Document.class);

		// The parser
		XPath xpath = setupXPath();
		XPathFactory factory = mock(XPathFactory.class);
		when(factory.newXPath()).thenReturn(xpath);
		XmlDocumentationParser parser = setupParser(factory);

		// The evaluation of the description.
		when(xpath.compile("/function/description/text()")).thenThrow(new XPathExpressionException("I failed"));
		// run
		parser.parseDescription(doc, xpath);
	}

	/**
	 * Test the parsing of the description
	 * 
	 * @throws XPathExpressionException
	 * @throws ParsingException
	 * @throws MalformedURLException
	 */
	@Test
	public void testParsingDescription() throws XPathExpressionException, ParsingException, MalformedURLException {
		// mock

		// The Document
		Document doc = mock(Document.class);

		// The parser
		XPath xpath = setupXPath();
		XPathFactory factory = mock(XPathFactory.class);
		when(factory.newXPath()).thenReturn(xpath);
		XmlDocumentationParser parser = setupParser(factory);

		// The evaluation of the description.
		XPathExpression expression = mock(XPathExpression.class);
		when(xpath.compile("/function/description/text()")).thenReturn(expression);
		when(expression.evaluate(any(Document.class), eq(XPathConstants.STRING))).thenReturn("description");
		// run
		parser.parseDescription(doc, xpath);
	}

	/**
	 * Test the parser when it fails to find a parameters node.
	 * 
	 * @throws XPathExpressionException
	 * @throws ParsingException
	 * @throws MalformedURLException
	 */
	@Test(expectedExceptions = ParsingException.class, expectedExceptionsMessageRegExp = "Failed to parse parameters")
	public void testNullPointerWhenParsingAllParameters() throws XPathExpressionException, ParsingException, MalformedURLException {
		// mock

		// The document
		Document doc = mock(Document.class);

		// The parser
		XPath xpath = setupXPath();
		XPathFactory factory = mock(XPathFactory.class);
		when(factory.newXPath()).thenReturn(xpath);
		XmlDocumentationParser parser = setupParser(factory);

		// The evaluation of the parameters.
		when(xpath.compile("/function/parameters/param")).thenReturn(null);

		// run
		parser.parseParameters(doc, xpath);
	}

	/**
	 * Test the parser when it fails to find a parameters node.
	 * 
	 * @throws XPathExpressionException
	 * @throws ParsingException
	 * @throws MalformedURLException
	 */
	@Test(expectedExceptions = ParsingException.class, expectedExceptionsMessageRegExp = "Failed to parse parameters")
	public void testXPathExceptionWhenParsingAllParameters() throws XPathExpressionException, ParsingException, MalformedURLException {
		// mock

		// The document
		Document doc = mock(Document.class);

		// The parser
		XPath xpath = setupXPath();
		XPathFactory factory = mock(XPathFactory.class);
		when(factory.newXPath()).thenReturn(xpath);
		XmlDocumentationParser parser = setupParser(factory);

		// The evaluation of the parameters.
		when(xpath.compile("/function/parameters/param")).thenThrow(new XPathExpressionException("I failed!"));

		// run
		parser.parseParameters(doc, xpath);
	}

	/**
	 * Test the parser when it fails to find an examples node.
	 * 
	 * @throws XPathExpressionException
	 * @throws ParsingException
	 * @throws MalformedURLException
	 */
	@Test(expectedExceptions = ParsingException.class, expectedExceptionsMessageRegExp = "Failed to parse examples")
	public void testNullPointerWhenParsingAllExamples() throws XPathExpressionException, ParsingException, MalformedURLException {
		// mock

		// the document
		Document doc = mock(Document.class);

		// The parser
		XPath xpath = setupXPath();
		XPathFactory factory = mock(XPathFactory.class);
		when(factory.newXPath()).thenReturn(xpath);
		XmlDocumentationParser parser = setupParser(factory);

		// The evaluation of the parameters.
		when(xpath.compile("/function/examples/example")).thenReturn(null);

		// run
		parser.parseExamples(doc, xpath);
	}

	/**
	 * Test the parser when it fails to find an examples node.
	 * 
	 * @throws XPathExpressionException
	 * @throws ParsingException
	 * @throws MalformedURLException
	 */
	@Test(expectedExceptions = ParsingException.class, expectedExceptionsMessageRegExp = "Failed to parse examples")
	public void testXPathExceptionWhenParsingAllExamples() throws XPathExpressionException, ParsingException, MalformedURLException {
		// mock

		// the document
		Document doc = mock(Document.class);

		// The parser
		XPath xpath = setupXPath();
		XPathFactory factory = mock(XPathFactory.class);
		when(factory.newXPath()).thenReturn(xpath);
		XmlDocumentationParser parser = setupParser(factory);

		// The evaluation of the parameters.
		when(xpath.compile("/function/examples/example")).thenThrow(new XPathExpressionException("I failed!"));

		// run
		parser.parseExamples(doc, xpath);
	}

	/**
	 * Test the parser when it fails to find an examples node.
	 * 
	 * @throws XPathExpressionException
	 * @throws ParsingException
	 * @throws MalformedURLException
	 */
	@Test(expectedExceptions = ParsingException.class, expectedExceptionsMessageRegExp = "Failed to parse references")
	public void testNullPointerWhenParsingAllReferences() throws XPathExpressionException, ParsingException, MalformedURLException {
		// mock

		// the document
		Document doc = mock(Document.class);

		// The parser
		XPath xpath = setupXPath();
		XPathFactory factory = mock(XPathFactory.class);
		when(factory.newXPath()).thenReturn(xpath);
		XmlDocumentationParser parser = setupParser(factory);

		// The evaluation of the parameters.
		when(xpath.compile("/function/references/reference")).thenReturn(null);

		// run
		parser.parseReferences(doc, xpath);
	}

	/**
	 * Test the parser when it fails to find an examples node.
	 * 
	 * @throws XPathExpressionException
	 * @throws ParsingException
	 * @throws MalformedURLException
	 */
	@Test(expectedExceptions = ParsingException.class, expectedExceptionsMessageRegExp = "Failed to parse references")
	public void testXPathExceptionWhenParsingAllReferences() throws XPathExpressionException, ParsingException, MalformedURLException {
		// mock

		// the document
		Document doc = mock(Document.class);

		// The parser
		XPath xpath = setupXPath();
		XPathFactory factory = mock(XPathFactory.class);
		when(factory.newXPath()).thenReturn(xpath);
		XmlDocumentationParser parser = setupParser(factory);

		// The evaluation of the parameters.
		when(xpath.compile("/function/references/reference")).thenThrow(new XPathExpressionException("I failed!"));

		// run
		parser.parseReferences(doc, xpath);
	}

	/**
	 * Test the parser when it fails to find an examples node.
	 * 
	 * @throws XPathExpressionException
	 * @throws ParsingException
	 * @throws MalformedURLException
	 */
	@Test(expectedExceptions = ParsingException.class, expectedExceptionsMessageRegExp = "Failed to parse searchtags")
	public void testNullPointerExceptionWhenParsingSearchTags() throws XPathExpressionException, ParsingException, MalformedURLException {
		// mock

		// the document
		Document doc = mock(Document.class);

		// The parser
		XPath xpath = setupXPath();
		XPathFactory factory = mock(XPathFactory.class);
		when(factory.newXPath()).thenReturn(xpath);
		XmlDocumentationParser parser = setupParser(factory);

		// The evaluation of the parameters.
		when(xpath.compile("/function/searchTags")).thenReturn(null);

		// run
		parser.parseSearchTags(doc, xpath);
	}

	/**
	 * Test the parser when it fails to find an examples node.
	 * 
	 * @throws XPathExpressionException
	 * @throws ParsingException
	 * @throws MalformedURLException
	 */
	@Test(expectedExceptions = ParsingException.class, expectedExceptionsMessageRegExp = "Failed to parse searchtags")
	public void testXPathExceptionWhenParsingSearchTags() throws XPathExpressionException, ParsingException, MalformedURLException {
		// mock

		// the document
		Document doc = mock(Document.class);

		// The parser
		XPath xpath = setupXPath();
		XPathFactory factory = mock(XPathFactory.class);
		when(factory.newXPath()).thenReturn(xpath);
		XmlDocumentationParser parser = setupParser(factory);

		// The evaluation of the parameters.
		when(xpath.compile("/function/searchTags")).thenThrow(new XPathExpressionException("I failed!"));

		// run
		parser.parseSearchTags(doc, xpath);
	}

	/**
	 * Test the parser with all possible parametervalues.
	 * 
	 * @throws XPathExpressionException
	 * @throws ParsingException
	 * @throws MalformedURLException
	 */
	@Test
	public void testParseAllParameters() throws XPathExpressionException, ParsingException, MalformedURLException {
		// mock
		// The document
		Document doc = mock(Document.class);

		// The parser
		XPath xpath = setupXPath();
		XPathFactory factory = mock(XPathFactory.class);
		when(factory.newXPath()).thenReturn(xpath);
		XmlDocumentationParser parser = setupParser(factory);

		// The nodelist:
		Node node = mock(Node.class);
		NodeList nodeList = mock(NodeList.class);
		when(nodeList.getLength()).thenReturn(1);
		when(nodeList.item(0)).thenReturn(node);

		// We mock the parseParameter function
		Parameter param = mock(Parameter.class);
		doReturn(param).when(parser).parseParameter(node);

		XPathExpression parametersExpression = mock(XPathExpression.class);
		when(xpath.compile("/function/parameters/param")).thenReturn(parametersExpression);
		when(parametersExpression.evaluate(any(Document.class), eq(XPathConstants.NODESET))).thenReturn(nodeList);

		// run
		parser.parseParameters(doc, xpath);
	}

	/**
	 * Test the parser with all possible example values.
	 * 
	 * @throws XPathExpressionException
	 * @throws ParsingException
	 * @throws MalformedURLException
	 */
	@Test
	public void testParseAllExamples() throws XPathExpressionException, ParsingException, MalformedURLException {
		// mock
		// The document
		Document doc = mock(Document.class);

		// The parser
		XPath xpath = setupXPath();
		XPathFactory factory = mock(XPathFactory.class);
		when(factory.newXPath()).thenReturn(xpath);
		XmlDocumentationParser parser = setupParser(factory);

		// The nodelist:
		Node node = mock(Node.class);
		NodeList nodeList = mock(NodeList.class);
		when(nodeList.getLength()).thenReturn(1);
		when(nodeList.item(0)).thenReturn(node);

		// Retrieving the nodelist
		XPathExpression parametersExpression = mock(XPathExpression.class);
		when(xpath.compile("/function/examples/example")).thenReturn(parametersExpression);
		when(parametersExpression.evaluate(any(Document.class), eq(XPathConstants.NODESET))).thenReturn(nodeList);

		Example example = mock(Example.class);
		doReturn(example).when(parser).parseExample(node);

		// run
		parser.parseExamples(doc, xpath);
	}

	/**
	 * Test the parser with all possible reference values.
	 * 
	 * @throws XPathExpressionException
	 * @throws ParsingException
	 * @throws MalformedURLException
	 */
	@Test
	public void testParseAllReferences() throws XPathExpressionException, ParsingException, MalformedURLException {
		// mock
		// The document
		Document doc = mock(Document.class);

		// The parser
		XPath xpath = setupXPath();
		XPathFactory factory = mock(XPathFactory.class);
		when(factory.newXPath()).thenReturn(xpath);
		XmlDocumentationParser parser = setupParser(factory);

		// The nodelist:
		Node node = mock(Node.class);
		NodeList nodeList = mock(NodeList.class);
		when(nodeList.getLength()).thenReturn(1);
		when(nodeList.item(0)).thenReturn(node);

		// Retrieving the nodelist
		XPathExpression parametersExpression = mock(XPathExpression.class);
		when(xpath.compile("/function/references/reference")).thenReturn(parametersExpression);
		when(parametersExpression.evaluate(any(Document.class), eq(XPathConstants.NODESET))).thenReturn(nodeList);

		Reference reference = mock(Reference.class);
		doReturn(reference).when(parser).parseReference(node);

		// run
		parser.parseReferences(doc, xpath);
	}

	/**
	 * Test the parser on how well it processes search tag parsing.
	 * 
	 * @throws XPathExpressionException
	 * @throws MalformedURLException
	 * @throws ParsingException
	 */
	@Test
	public void testParseSearchTags() throws XPathExpressionException, MalformedURLException, ParsingException {
		// mock
		// The document
		Document doc = mock(Document.class);

		// The parser
		XPath xpath = setupXPath();
		XPathFactory factory = mock(XPathFactory.class);
		when(factory.newXPath()).thenReturn(xpath);
		XmlDocumentationParser parser = setupParser(factory);

		// The search tags
		Node correctSearchTag = mock(Node.class);
		when(correctSearchTag.getTextContent()).thenReturn("math, abs, absolute");

		XPathExpression parametersExpression = mock(XPathExpression.class);
		when(xpath.compile("/function/searchTags")).thenReturn(parametersExpression);
		when(parametersExpression.evaluate(any(Document.class), eq(XPathConstants.NODE))).thenReturn(correctSearchTag);

		parser.parseSearchTags(doc, xpath);
	}

	/**
	 * Tests the parser for when extracting values from a parameter node.
	 * 
	 * @throws XPathExpressionException
	 * @throws MalformedURLException
	 * @throws ParsingException
	 */
	@Test
	public void testParseParameter() throws XPathExpressionException, MalformedURLException, ParsingException {
		// mock
		// The node
		Node node = mock(Node.class);

		// The parser
		XPath xpath = setupXPath();
		XPathFactory factory = mock(XPathFactory.class);
		when(factory.newXPath()).thenReturn(xpath);
		XmlDocumentationParser parser = setupParser(factory);
		doReturn("A value").when(parser).getAttributeOrNull(anyString(), any());

		// run
		parser.parseParameter(node);
	}

	/**
	 * Tests the parser for when extracting values from an example node.
	 * 
	 * @throws XPathExpressionException
	 * @throws MalformedURLException
	 * @throws ParsingException
	 */
	@Test
	public void testParseExample() throws XPathExpressionException, MalformedURLException, ParsingException {
		// mock
		// The node
		Node node = mock(Node.class);
		NodeList nodeList = mock(NodeList.class);
		when(node.getChildNodes()).thenReturn(nodeList);
		when(nodeList.getLength()).thenReturn(1);
		when(nodeList.item(0)).thenReturn(node);

		// The parser
		XPath xpath = setupXPath();
		XPathFactory factory = mock(XPathFactory.class);
		when(factory.newXPath()).thenReturn(xpath);
		XmlDocumentationParser parser = setupParser(factory);
		doReturn(null).when(parser).getAttributeOrNull(anyString(), any());

		// run
		parser.parseExample(node);
	}

	/**
	 * @return
	 *         Returns all possible values in a reference field.
	 */
	@DataProvider(name = "possibleReferenceValues")
	public Object[][] createData() {
		return new Object[][] {
				{"singleConstruct", "singleConstruct"},
				{"Package.construct", "Package.construct"}
		};
	}

	/**
	 * Tests the parser for when extracting values from a reference node
	 * 
	 * @param value
	 *        A possible value for a reference field
	 * @param valueAgain
	 *        Value, again.
	 * @throws XPathExpressionException
	 * @throws MalformedURLException
	 * @throws ParsingException
	 */
	@Test(dataProvider = "possibleReferenceValues")
	public void testParseReference(final String value, final String valueAgain) throws XPathExpressionException, MalformedURLException, ParsingException {
		// mock
		// The node
		Node node = mock(Node.class);
		when(node.getTextContent()).thenReturn(value);

		// The parser
		XPath xpath = setupXPath();
		XPathFactory factory = mock(XPathFactory.class);
		when(factory.newXPath()).thenReturn(xpath);
		XmlDocumentationParser parser = setupParser(factory);

		// run
		parser.parseReference(node);
	}

	/**
	 * Tests the parser for when extracting values from an example node.
	 * 
	 * @throws XPathExpressionException
	 * @throws MalformedURLException
	 * @throws ParsingException
	 */
	@Test(expectedExceptions = ParsingException.class, expectedExceptionsMessageRegExp = "Invalid reference format")
	public void testParsingExceptionWhenParsingReference() throws XPathExpressionException, MalformedURLException, ParsingException {
		// mock
		// The node
		Node node = mock(Node.class);
		when(node.getTextContent()).thenReturn("Package.construct.secondconstruct?");

		// The parser
		XPath xpath = setupXPath();
		XPathFactory factory = mock(XPathFactory.class);
		when(factory.newXPath()).thenReturn(xpath);
		XmlDocumentationParser parser = setupParser(factory);

		// run
		parser.parseReference(node);
	}

	/**
	 * Tests the parser for when a nullpointerexception occurs when trying to parse a parameter
	 * 
	 * @throws XPathExpressionException
	 * @throws MalformedURLException
	 * @throws ParsingException
	 */
	@Test(expectedExceptions = NullPointerException.class, expectedExceptionsMessageRegExp = "Value of attribute `name` is null")
	public void testNullPointerExceptionWhenParsingParameter() throws XPathExpressionException, MalformedURLException, ParsingException {
		// mock
		// The node
		Node node = mock(Node.class);

		// The parser
		XPath xpath = setupXPath();
		XPathFactory factory = mock(XPathFactory.class);
		when(factory.newXPath()).thenReturn(xpath);
		XmlDocumentationParser parser = setupParser(factory);
		doReturn(null).when(parser).getAttributeOrNull(anyString(), any());

		// run
		parser.parseParameter(node);
	}

	private XPath setupXPath() throws XPathExpressionException {
		XPath xpath = mock(XPath.class);

		// the xpath querying:
		XPathExpression expression = mock(XPathExpression.class);

		// what evaluating the expression should return. Node node = mock(Node.class);
		NodeList nodeList = mock(NodeList.class);
		Node node = mock(Node.class);

		when(xpath.compile(anyString())).thenReturn(expression);
		when(expression.evaluate(any(Document.class), eq(XPathConstants.NODE))).thenReturn(node);
		when(expression.evaluate(any(Document.class), eq(XPathConstants.NODESET))).thenReturn(nodeList);
		when(expression.evaluate(any(Document.class), eq(XPathConstants.STRING))).thenReturn("a string");

		return xpath;
	}

	private XmlDocumentationParser setupParser(final XPathFactory factory) throws ParsingException {
		XmlDocumentationParser parser = spy(new XmlDocumentationParser(factory));
		return parser;
	}

}
