package nl.xillio.xill.docgen.impl;

import static org.mockito.Mockito.*;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashSet;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import javax.xml.xpath.XPath;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;







import nl.xillio.xill.docgen.data.Parameter;
import nl.xillio.xill.docgen.exceptions.ParsingException;
import nl.xillio.xill.docgen.impl.XmlDocumentationParser;

import org.testng.Assert;
import org.testng.annotations.Test;

public class XmlDocumentationParserTest {
	
	/**
	 *TODO make the javadoc correct
	 * Tests the process with normal usage. Every xpathquery gives null but the parser should be able to handle that.
	 * @throws ParsingException 
	 * @throws MalformedURLException 
	 * @throws XPathExpressionException 
	 */
	@Test
	public void testParsingWithNormalUsage() throws ParsingException, MalformedURLException, XPathExpressionException{
		// mock
		
		//The xpath
		
		XPath xpath = setupXPath();
		XPathFactory factory = mock(XPathFactory.class);
		when(factory.newXPath()).thenReturn(xpath);
		
		//the url resource
		URL resource = new URL("http://www.w3schools.com:80/xml/note.xml");
		
		// run
		XmlDocumentationParser parser = setupParser(factory);
		parser.parse(resource, "functionName");
	}
	
	/**
	 * Test the parser then it cannot find a description node.
	 * @throws XPathExpressionException
	 * @throws ParsingException
	 * @throws MalformedURLException
	 */
	@Test(expectedExceptions = ParsingException.class, expectedExceptionsMessageRegExp = "Failed to parse description")
	public void testNullPointerWhenParsingDescription() throws XPathExpressionException, ParsingException, MalformedURLException{
		// mock
		
		//The Document
		Document doc = mock(Document.class);
		
		//The parser
		XPath xpath = setupXPath();
		XPathFactory factory = mock(XPathFactory.class);
		when(factory.newXPath()).thenReturn(xpath);
		XmlDocumentationParser parser = setupParser(factory);
		
		//The evaluation of the description.		
		when(xpath.compile("/function/description/text()")).thenReturn(null);		
		// run
		parser.parseDescription(doc, xpath);
	}
	
	/**
	 * Test the parser then it cannot find a description node.
	 * @throws XPathExpressionException
	 * @throws ParsingException
	 * @throws MalformedURLException
	 */
	@Test(expectedExceptions = ParsingException.class, expectedExceptionsMessageRegExp = "Failed to parse description")
	public void testXPathExceptionWhenParsingDescription() throws XPathExpressionException, ParsingException, MalformedURLException{
		// mock
		
		//The Document
		Document doc = mock(Document.class);
		
		//The parser
		XPath xpath = setupXPath();
		XPathFactory factory = mock(XPathFactory.class);
		when(factory.newXPath()).thenReturn(xpath);
		XmlDocumentationParser parser = setupParser(factory);
		
		//The evaluation of the description.		
		when(xpath.compile("/function/description/text()")).thenThrow(new XPathExpressionException("I failed"));	
		// run
		parser.parseDescription(doc, xpath);
	}
	
	/**
	 * Test the parsing of the description
	 * @throws XPathExpressionException
	 * @throws ParsingException
	 * @throws MalformedURLException
	 */
	@Test
	public void testParsingDescription() throws XPathExpressionException, ParsingException, MalformedURLException{
		// mock
		
		//The Document
		Document doc = mock(Document.class);
		
		//The parser
		XPath xpath = setupXPath();
		XPathFactory factory = mock(XPathFactory.class);
		when(factory.newXPath()).thenReturn(xpath);
		XmlDocumentationParser parser = setupParser(factory);
		
		//The evaluation of the description.		
		XPathExpression expression = mock(XPathExpression.class);
		when(xpath.compile("/function/description/text()")).thenReturn(expression);
		when(expression.evaluate(any(Document.class), eq(XPathConstants.STRING))).thenReturn("description");
		// run
		parser.parseDescription(doc, xpath);
	}
	
	/**
	 * Test the parser when it fails to find a parameters node.
	 * @throws XPathExpressionException
	 * @throws ParsingException
	 * @throws MalformedURLException
	 */
	@Test(expectedExceptions = ParsingException.class, expectedExceptionsMessageRegExp = "Failed to parse parameters")
	public void testNullPointerWhenParsingAllParameters() throws XPathExpressionException, ParsingException, MalformedURLException{
		// mock
		
		//The document
		Document doc = mock(Document.class);
		
		//The parser
		XPath xpath = setupXPath();
		XPathFactory factory = mock(XPathFactory.class);
		when(factory.newXPath()).thenReturn(xpath);
		XmlDocumentationParser parser = setupParser(factory);

		
		//The evaluation of the parameters.		
		when(xpath.compile("/function/parameters/param")).thenReturn(null);
		
		// run
		parser.parseParameters(doc, xpath);
	}
	
	/**
	 * Test the parser when it fails to find a parameters node.
	 * @throws XPathExpressionException
	 * @throws ParsingException
	 * @throws MalformedURLException
	 */
	@Test(expectedExceptions = ParsingException.class, expectedExceptionsMessageRegExp = "Failed to parse parameters")
	public void testXPathExceptionWhenParsingAllParameters() throws XPathExpressionException, ParsingException, MalformedURLException{
		// mock
		
		//The document
		Document doc = mock(Document.class);
		
		//The parser
		XPath xpath = setupXPath();
		XPathFactory factory = mock(XPathFactory.class);
		when(factory.newXPath()).thenReturn(xpath);
		XmlDocumentationParser parser = setupParser(factory);

		
		//The evaluation of the parameters.		
		when(xpath.compile("/function/parameters/param")).thenThrow(new XPathExpressionException("I failed!"));
		
		// run
		parser.parseParameters(doc, xpath);
	}
	
	/**
	 * Test the parser when it fails to find an examples node.
	 * @throws XPathExpressionException
	 * @throws ParsingException
	 * @throws MalformedURLException
	 */
	@Test(expectedExceptions = ParsingException.class, expectedExceptionsMessageRegExp = "Failed to parse examples")
	public void testNullPointerWhenParsingAllExamples() throws XPathExpressionException, ParsingException, MalformedURLException{
		// mock
		
		//the document
		Document doc = mock(Document.class);
		
		//The parser
		XPath xpath = setupXPath();
		XPathFactory factory = mock(XPathFactory.class);
		when(factory.newXPath()).thenReturn(xpath);
		XmlDocumentationParser parser = setupParser(factory);
		
		//The evaluation of the parameters.		
		when(xpath.compile("/function/examples/example")).thenReturn(null);
		
		// run
		parser.parseExamples(doc, xpath);
	}
	
	/**
	 * Test the parser when it fails to find an examples node.
	 * @throws XPathExpressionException
	 * @throws ParsingException
	 * @throws MalformedURLException
	 */
	@Test(expectedExceptions = ParsingException.class, expectedExceptionsMessageRegExp = "Failed to parse examples")
	public void testXPathExceptionWhenParsingAllExamples() throws XPathExpressionException, ParsingException, MalformedURLException{
		// mock
		
		//the document
		Document doc = mock(Document.class);
		
		//The parser
		XPath xpath = setupXPath();
		XPathFactory factory = mock(XPathFactory.class);
		when(factory.newXPath()).thenReturn(xpath);
		XmlDocumentationParser parser = setupParser(factory);
		
		//The evaluation of the parameters.		
		when(xpath.compile("/function/examples/example")).thenThrow(new XPathExpressionException("I failed!"));
		
		// run
		parser.parseExamples(doc, xpath);
	}
	
	/**
	 * Test the parser when it fails to find a reference node.
	 * @throws XPathExpressionException
	 * @throws ParsingException
	 * @throws MalformedURLException
	 */
	@Test(expectedExceptions = ParsingException.class, expectedExceptionsMessageRegExp = "Failed to parse references")
	public void testFailingToParseAllReferences() throws XPathExpressionException, ParsingException, MalformedURLException{
		// mock
		
		//The xpath
		
		XPath xpath = setupXPath();
		XPathFactory factory = mock(XPathFactory.class);
		when(factory.newXPath()).thenReturn(xpath);
		
		//The evaluation of the parameters.		
		XPathExpression parametersExpression = mock(XPathExpression.class);
		when(xpath.compile("/function/references/reference")).thenReturn(parametersExpression);
		when(parametersExpression.evaluate(any(Document.class), eq(XPathConstants.NODESET))).thenReturn(null);
		
		//the url resource
		URL resource = new URL("http://www.w3schools.com:80/xml/note.xml");
		
		// run
		XmlDocumentationParser parser = setupParser(factory);
		parser.parse(resource, "functionName");
	}
		
	
	/**
	 * Test the parser with all possible parametervalues.
	 * @throws XPathExpressionException
	 * @throws ParsingException
	 * @throws MalformedURLException
	 */
	@Test
	public void testParseAllParameters() throws XPathExpressionException, ParsingException, MalformedURLException{
		// mock
		//The document
		Document doc = mock(Document.class);
		
		//The parser
		XPath xpath = setupXPath();
		XPathFactory factory = mock(XPathFactory.class);
		when(factory.newXPath()).thenReturn(xpath);
		XmlDocumentationParser parser = setupParser(factory);
		
		//The nodelist:
		Node node = mock(Node.class);
		NodeList nodeList = mock(NodeList.class);
		when(nodeList.getLength()).thenReturn(1);
		when(nodeList.item(0)).thenReturn(node);
		
		//We mock the parseParameter function
		Parameter param = mock(Parameter.class);
		doReturn(param).when(parser).parseParameter(node);
		
		XPathExpression parametersExpression = mock(XPathExpression.class);
		when(xpath.compile("/function/parameters/param")).thenReturn(parametersExpression);
		when(parametersExpression.evaluate(any(Document.class), eq(XPathConstants.NODESET))).thenReturn(nodeList);
		
		//the url resource
		URL resource = new URL("http://www.w3schools.com:80/xml/note.xml");
		
		// run
		parser.parseParameters(doc, xpath);
	}
	
	/**
	 * Test the parser with all possible example values.
	 * @throws XPathExpressionException
	 * @throws ParsingException
	 * @throws MalformedURLException
	 */
	@Test
	public void testParseAllExamples() throws XPathExpressionException, ParsingException, MalformedURLException{
		// mock
		
		//The xpath
		
		XPath xpath = setupXPath();
		XPathFactory factory = mock(XPathFactory.class);
		when(factory.newXPath()).thenReturn(xpath);
		
		//The nodelist we return:
		
		//node in the nodelist
		//This one is a fully correct node
		
		
		//The nodelist:
		Node node = mock(Node.class);
		NodeList nodeList = mock(NodeList.class);
		when(nodeList.getLength()).thenReturn(1);
		when(nodeList.item(0)).thenReturn(node);
	
		
		XPathExpression parametersExpression = mock(XPathExpression.class);
		when(xpath.compile("/function/examples/example")).thenReturn(parametersExpression);
		when(parametersExpression.evaluate(any(Document.class), eq(XPathConstants.NODESET))).thenReturn(nodeList);
		
		//the url resource
		URL resource = new URL("http://www.w3schools.com:80/xml/note.xml");
		
		// run
		XmlDocumentationParser parser = setupParser(factory);
		parser.parse(resource, "functionName");
	}
	
	/**
	 * Test the parser with all possible reference values.
	 * @throws XPathExpressionException
	 * @throws ParsingException
	 * @throws MalformedURLException
	 */
	@Test
	public void testParseAllReferences() throws XPathExpressionException, ParsingException, MalformedURLException{
		// mock
		
		//The xpath
		
		XPath xpath = setupXPath();
		XPathFactory factory = mock(XPathFactory.class);
		when(factory.newXPath()).thenReturn(xpath);
		
		//The nodelist we return:
		
		//node in the nodelist
		//This one is a fully correct reference
		Node correctReference = mock(Node.class);
		when(correctReference.getTextContent()).thenReturn("package.construct");
	
		//node in the nodelist
		//This node is an incorrect reference.
		Node incorrectReference = mock(Node.class);
		when(incorrectReference.getTextContent()).thenReturn("package_construct");
		
		//This node is an empty reference
		Node emptyReference = mock(Node.class);
		
		//The nodelist:
		NodeList nodeList = mock(NodeList.class);
		when(nodeList.getLength()).thenReturn(3);
		when(nodeList.item(0)).thenReturn(correctReference);
		when(nodeList.item(1)).thenReturn(incorrectReference);
		when(nodeList.item(2)).thenReturn(emptyReference);
		
		XPathExpression parametersExpression = mock(XPathExpression.class);
		when(xpath.compile("/function/references/reference")).thenReturn(parametersExpression);
		when(parametersExpression.evaluate(any(Document.class), eq(XPathConstants.NODESET))).thenReturn(nodeList);
		
		//the url resource
		URL resource = new URL("http://www.w3schools.com:80/xml/note.xml");
		
		// run
		XmlDocumentationParser parser = setupParser(factory);
		parser.parse(resource, "functionName");
	}
	
	/**
	 * Test the parser on how well it processes search tag parsing.
	 * @throws XPathExpressionException
	 * @throws MalformedURLException
	 * @throws ParsingException
	 */
	@Test
	public void testParseSearchTags() throws XPathExpressionException, MalformedURLException, ParsingException{
		// mock
		
		//The xpath
		
		XPath xpath = setupXPath();
		XPathFactory factory = mock(XPathFactory.class);
		when(factory.newXPath()).thenReturn(xpath);
		
		//The nodelist we return:
		
		//node in the nodelist
		//This one is a fully correct reference
		Node correctSearchTag = mock(Node.class);
		when(correctSearchTag.getTextContent()).thenReturn("math, abs, absolute");

		XPathExpression parametersExpression = mock(XPathExpression.class);
		when(xpath.compile("/function/searchTags")).thenReturn(parametersExpression);
		when(parametersExpression.evaluate(any(Document.class), eq(XPathConstants.NODE))).thenReturn(correctSearchTag);
		
		//the url resource
		URL resource = new URL("http://www.w3schools.com:80/xml/note.xml");
		
		// run
		XmlDocumentationParser parser = setupParser(factory);
		parser.parse(resource, "functionName");
	}
		
		
	private XPath setupXPath() throws XPathExpressionException{
		XPath xpath = mock(XPath.class);
		
		//the xpath querying:
		XPathExpression expression = mock(XPathExpression.class);
		
		//what evaluating the expression should return.		Node node = mock(Node.class);
		NodeList nodeList = mock(NodeList.class);
		Node node = mock(Node.class);

		
		when(xpath.compile(anyString())).thenReturn(expression);
		when(expression.evaluate(any(Document.class), eq(XPathConstants.NODE))).thenReturn(node);
		when(expression.evaluate(any(Document.class), eq(XPathConstants.NODESET))).thenReturn(nodeList);
		when(expression.evaluate(any(Document.class), eq(XPathConstants.STRING))).thenReturn("a string");
		
		return xpath;
	}
	
	private XmlDocumentationParser setupParser(XPathFactory factory) throws ParsingException{
		XmlDocumentationParser parser = spy(new XmlDocumentationParser(factory));
		doReturn(new HashSet<String>(Arrays.asList())).when(parser).parseSearchTags(any(), any());
		return parser;
	}

}
