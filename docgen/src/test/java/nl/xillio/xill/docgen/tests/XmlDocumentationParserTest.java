package nl.xillio.xill.docgen.tests;

import static org.mockito.Mockito.*;

import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import javax.xml.xpath.XPath;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;




import nl.xillio.xill.docgen.exceptions.ParsingException;
import nl.xillio.xill.docgen.impl.XmlDocumentationParser;

import org.testng.Assert;
import org.testng.annotations.Test;

public class XmlDocumentationParserTest {
	
	/**
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
		XmlDocumentationParser parser = new XmlDocumentationParser(factory);
		parser.parse(resource, "functionName");
	}
	
	/**
	 * Test the parser then it cannot find a description node.
	 * @throws XPathExpressionException
	 * @throws ParsingException
	 * @throws MalformedURLException
	 */
	@Test(expectedExceptions = ParsingException.class, expectedExceptionsMessageRegExp = "Failed to parse the description")
	public void testFailingToParseDescription() throws XPathExpressionException, ParsingException, MalformedURLException{
		// mock
		
		//The xpath
		
		XPath xpath = setupXPath();
		XPathFactory factory = mock(XPathFactory.class);
		when(factory.newXPath()).thenReturn(xpath);
		
		//The evaluation of the description.		
		XPathExpression descriptionExpression = mock(XPathExpression.class);
		when(xpath.compile("/function/description/text()")).thenReturn(descriptionExpression);
		when(descriptionExpression.evaluate(any(Document.class), eq(XPathConstants.STRING))).thenReturn(null);
		
		//the url resource
		URL resource = new URL("http://www.w3schools.com:80/xml/note.xml");
		
		// run
		XmlDocumentationParser parser = new XmlDocumentationParser(factory);
		parser.parse(resource, "functionName");
	}
	
	/**
	 * Test the parser when it fails to find a parameters node.
	 * @throws XPathExpressionException
	 * @throws ParsingException
	 * @throws MalformedURLException
	 */
	@Test(expectedExceptions = ParsingException.class, expectedExceptionsMessageRegExp = "Failed to parse parameters")
	public void testFailingToParseAllParameters() throws XPathExpressionException, ParsingException, MalformedURLException{
		// mock
		
		//The xpath
		
		XPath xpath = setupXPath();
		XPathFactory factory = mock(XPathFactory.class);
		when(factory.newXPath()).thenReturn(xpath);
		
		//The evaluation of the parameters.		
		XPathExpression parametersExpression = mock(XPathExpression.class);
		when(xpath.compile("/function/parameters/param")).thenReturn(parametersExpression);
		when(parametersExpression.evaluate(any(Document.class), eq(XPathConstants.NODESET))).thenReturn(null);
		
		//the url resource
		URL resource = new URL("http://www.w3schools.com:80/xml/note.xml");
		
		// run
		XmlDocumentationParser parser = new XmlDocumentationParser(factory);
		parser.parse(resource, "functionName");
	}
	
	/**
	 * Test the parser when it fails to find an examples node.
	 * @throws XPathExpressionException
	 * @throws ParsingException
	 * @throws MalformedURLException
	 */
	@Test(expectedExceptions = ParsingException.class, expectedExceptionsMessageRegExp = "Failed to parse examples")
	public void testFailingToParseAllExamples() throws XPathExpressionException, ParsingException, MalformedURLException{
		// mock
		
		//The xpath
		
		XPath xpath = setupXPath();
		XPathFactory factory = mock(XPathFactory.class);
		when(factory.newXPath()).thenReturn(xpath);
		
		//The evaluation of the parameters.		
		XPathExpression parametersExpression = mock(XPathExpression.class);
		when(xpath.compile("/function/examples/example")).thenReturn(parametersExpression);
		when(parametersExpression.evaluate(any(Document.class), eq(XPathConstants.NODESET))).thenReturn(null);
		
		//the url resource
		URL resource = new URL("http://www.w3schools.com:80/xml/note.xml");
		
		// run
		XmlDocumentationParser parser = new XmlDocumentationParser(factory);
		parser.parse(resource, "functionName");
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
		XmlDocumentationParser parser = new XmlDocumentationParser(factory);
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
		
		//The xpath
		
		XPath xpath = setupXPath();
		XPathFactory factory = mock(XPathFactory.class);
		when(factory.newXPath()).thenReturn(xpath);
		
		//The nodelist we return:
		
		//the first node
		//This one is a fully correct node
		Node correctParameter = mock(Node.class);
		Node nameAttribute = mock(Node.class);
		NamedNodeMap correctParameterMap = mock(NamedNodeMap.class);
		when(correctParameter.getAttributes()).thenReturn(correctParameterMap);
		when(correctParameterMap.getNamedItem(any())).thenReturn(nameAttribute);
		when(nameAttribute.getNodeValue()).thenReturn("a name");
		
		//the second node
		//this node has no attributes and hence cannot be read as a parameter
		Node failedParameter = mock(Node.class);
		NamedNodeMap failedParameterMap = mock(NamedNodeMap.class);
		when(failedParameter.getAttributes()).thenReturn(failedParameterMap);
		
		//The nodelist:
		NodeList nodeList = mock(NodeList.class);
		when(nodeList.getLength()).thenReturn(2);
		when(nodeList.item(0)).thenReturn(correctParameter);
		when(nodeList.item(1)).thenReturn(failedParameter);
		
		XPathExpression parametersExpression = mock(XPathExpression.class);
		when(xpath.compile("/function/parameters/param")).thenReturn(parametersExpression);
		when(parametersExpression.evaluate(any(Document.class), eq(XPathConstants.NODESET))).thenReturn(nodeList);
		
		//the url resource
		URL resource = new URL("http://www.w3schools.com:80/xml/note.xml");
		
		// run
		XmlDocumentationParser parser = new XmlDocumentationParser(factory);
		parser.parse(resource, "functionName");
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
		Node filledExample = mock(Node.class);
		NodeList exampleContent = mock(NodeList.class);
		when(filledExample.getChildNodes()).thenReturn(exampleContent);
		when(exampleContent.getLength()).thenReturn(1);
		when(exampleContent.item(0)).thenReturn(filledExample);
		
		//The nodelist:
		NodeList nodeList = mock(NodeList.class);
		when(nodeList.getLength()).thenReturn(1);
		when(nodeList.item(0)).thenReturn(filledExample);
	
		
		XPathExpression parametersExpression = mock(XPathExpression.class);
		when(xpath.compile("/function/examples/example")).thenReturn(parametersExpression);
		when(parametersExpression.evaluate(any(Document.class), eq(XPathConstants.NODESET))).thenReturn(nodeList);
		
		//the url resource
		URL resource = new URL("http://www.w3schools.com:80/xml/note.xml");
		
		// run
		XmlDocumentationParser parser = new XmlDocumentationParser(factory);
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
		XmlDocumentationParser parser = new XmlDocumentationParser(factory);
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
		XmlDocumentationParser parser = new XmlDocumentationParser(factory);
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

}
