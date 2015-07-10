package nl.xillio.migrationtool.documentation;
import java.io.IOException;
import java.io.InputStream;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


/**
 * The class which parses and XML file and returns a {@link FunctionDocument} containing the info in the XML. <BR> <BR>
 * The class parses using XPath commands.
 * @author Ivor
 *
 */
public class XMLparser {
	
	/**
	 * 
	 */
	public XMLparser()
	{
		
	}
	
	/**
	 * This function parses XML given by a package and can return a FunctionDocument containing the information.
	 * @param xml
	 * 			An {@link InputStream} containing the xml.
	 * @param packet
	 * 			A {@link String} with the package name.
	 * @param version
	 * 			A {@link String} with the version name.
	 * @return
	 */
	public FunctionDocument parseXML(InputStream xml, final String packet, final String version)
	{
	    DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
	    DocumentBuilder docBuilder;
		try {
			docBuilder = docBuilderFactory.newDocumentBuilder(); 
			Document doc = docBuilder.parse (xml);
			FunctionDocument func = new FunctionDocument();
			
			// Create XPathFactory object
			XPathFactory xpathFactory = XPathFactory.newInstance();
			// Create XPath object
			XPath xpath = xpathFactory.newXPath();
			
			//Parse the name and name the function
			XPathExpression nameExpr = xpath.compile("/function/name/text()");
			func.setName((String) nameExpr.evaluate(doc, XPathConstants.STRING));
			
			//Parse the description
			XPathExpression descriptionExpr = xpath.compile("/function/description/text()");
			func.setDescription((String) descriptionExpr.evaluate(doc, XPathConstants.STRING));
			
			//Parse the parameters
			XPathExpression paramsDefaultExpr = xpath.compile("/function/parameters/param/text()");
			XPathExpression paramsNameExpr 	  =	xpath.compile("/function/parameters/param/@name");
			NodeList parameterDefaults 		  = (NodeList) paramsDefaultExpr.evaluate(doc, XPathConstants.NODESET);
			NodeList parameterNames 		  = (NodeList) paramsNameExpr.evaluate(doc, XPathConstants.NODESET);
			for(int t = 0; t < parameterNames.getLength(); ++t)	{
				func.addParameter(parameterDefaults.item(t).getNodeValue(), parameterNames.item(t).getNodeValue());
			}
			
			//Parse the examples
			XPathExpression examplesExpr			= xpath.compile("/function/examples/example/text()");
			XPathExpression exampleDescriptionsExpr = xpath.compile("/function/examples/example/@description");
			NodeList examples 						= (NodeList) examplesExpr.evaluate(doc, XPathConstants.NODESET);
			NodeList exampleDescriptions 			= (NodeList) exampleDescriptionsExpr.evaluate(doc, XPathConstants.NODESET);
			for(int t = 0; t < examples.getLength(); ++t){
				func.addExample(exampleDescriptions.item(t).getNodeValue(), examples.item(t).getNodeValue());
			}
			
			//Parse the tags
			XPathExpression tagsExpr 		= xpath.compile("/function/tags/tag/text()");
			XPathExpression tagsPackageExpr = xpath.compile("/function/tags/tag/@package");
			NodeList tags 					= (NodeList) tagsExpr.evaluate(doc, XPathConstants.NODESET);
			NodeList tagsPackage 			= (NodeList) tagsPackageExpr.evaluate(doc, XPathConstants.NODESET);
			for(int t = 0; t < tags.getLength(); ++t){
				func.addLink(tagsPackage.item(t).getNodeValue(), tags.item(t).getNodeValue());
			}
			
			//Parse the searchTags
			XPathExpression searchTagsExpr = xpath.compile("/function/searchTags/searchTag");
			NodeList searchTags 		   = (NodeList) searchTagsExpr.evaluate(doc, XPathConstants.NODESET);
			for(int t = 0; t < searchTags.getLength(); ++t){
				func.addSearchTag(searchTags.item(0).getNodeValue());
			}
			
			//Set the version and the package of the FunctionDocument and add a searchTag containing the Package name.
			func.setVersion(version);
			func.setPackage(packet);
			func.addSearchTag(packet);
			return func;
		} catch (ParserConfigurationException | SAXException | IOException e) {
			e.printStackTrace();
		} catch (XPathExpressionException e) {
			e.printStackTrace();
		}
	   
		return null;
	}
}
