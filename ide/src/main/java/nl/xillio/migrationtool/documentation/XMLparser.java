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

import nl.xillio.xill.api.errors.RobotRuntimeException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * <p>
 * The class which parses and XML file and returns a {@link FunctionDocument} containing the info in the XML.
 * </p>
 * <p>
 * The class parses using XPath commands.
 * </p>
 *
 * @author Ivor van der Hoog.
 *
 */
public class XMLparser {
	private static final Logger log = LogManager.getLogger();

	/**
	 * This function parses XML given by a package and can return a
	 * FunctionDocument containing the information.
	 *
	 * @param xml
	 *        An {@link InputStream} containing the xml.
	 * @param packet
	 *        A {@link String} with the package name.
	 * @param version
	 *        A {@link String} with the version name.
	 * @return The parsed {@link FunctionDocument} contained in the stream
	 */
	public FunctionDocument parseXML(final InputStream xml, final String packet, final String version) throws NullPointerException {
		DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder;
		try {
			docBuilder = docBuilderFactory.newDocumentBuilder();
			Document doc;
			try {
				doc = docBuilder.parse(xml);

				FunctionDocument func = new FunctionDocument();

				// Create XPathFactory object
				XPathFactory xpathFactory = XPathFactory.newInstance();

				// Create XPath object
				XPath xpath = xpathFactory.newXPath();

				// Parse the description
				try{
				XPathExpression descriptionExpr = xpath.compile("/function/description/text()");
				func.setDescription(((String) descriptionExpr.evaluate(doc, XPathConstants.STRING)).trim());
				}
				catch(NullPointerException | IllegalArgumentException | XPathExpressionException e){
					throw new RobotRuntimeException("Invalid XML in a description in the following package: " + packet);
				}
				
				// Parse the parameters
				try{
				NodeList params = (NodeList) xpath.compile("/function/parameters/param").evaluate(doc, XPathConstants.NODESET);
				for(int t = 0; t < params.getLength(); ++t){
					Node currentParameter = params.item(t);
					String par = currentParameter.getAttributes().getNamedItem("name").getNodeValue();
					if(currentParameter.getAttributes().getNamedItem("default") == null){
						func.addParameter(par);
					}
					else{
						func.addParameter(par,currentParameter.getAttributes().getNamedItem("default").getNodeValue() );
					}
				}
				}
				catch(NullPointerException | IllegalArgumentException | XPathExpressionException e){
					throw new RobotRuntimeException("Invalid XML in a parameter in the following package: " + packet);
				}
				
				try{
				// Parse the examples
				XPathExpression examplesExpr = xpath.compile("/function/examples/example/text()");
				XPathExpression exampleDescriptionsExpr = xpath.compile("/function/examples/example/@description");
				NodeList examples = (NodeList) examplesExpr.evaluate(doc, XPathConstants.NODESET);
				NodeList exampleDescriptions = (NodeList) exampleDescriptionsExpr.evaluate(doc, XPathConstants.NODESET);
				for (int t = 0; t < examples.getLength(); ++t) {
					func.addExample(exampleDescriptions.item(t).getTextContent().trim(),
						examples.item(t).getTextContent().trim());
				}
				}
				catch(NullPointerException | IllegalArgumentException | XPathExpressionException e){
					throw new RobotRuntimeException("Invalid XML in an example in the following package: " + packet);
				}

				// Parse the references
				try{
				XPathExpression tagsExpr = xpath.compile("/function/references/reference/text()");
				XPathExpression tagsPackageExpr = xpath.compile("/function/references/reference/@package");
				NodeList tags = (NodeList) tagsExpr.evaluate(doc, XPathConstants.NODESET);
				NodeList tagsPackage = (NodeList) tagsPackageExpr.evaluate(doc, XPathConstants.NODESET);
				for (int t = 0; t < tags.getLength(); ++t) {
					func.addLink(tagsPackage.item(t).getTextContent().trim(), tags.item(t).getTextContent().trim());
				}
				}
				catch(NullPointerException | IllegalArgumentException | XPathExpressionException e){
					log.error("Invalid XML in a reference in the following package: " + packet);
				}


				// Parse the searchTags
				try{
				XPathExpression searchTagsExpr = xpath.compile("/function/searchTags/searchTag");
				NodeList searchTags = (NodeList) searchTagsExpr.evaluate(doc, XPathConstants.NODESET);
				for (int t = 0; t < searchTags.getLength(); ++t) {
					func.addSearchTag(searchTags.item(0).getTextContent().trim());
				}
				}
				catch(NullPointerException | IllegalArgumentException | XPathExpressionException e){
					throw new RobotRuntimeException("Invalid XML in a searchTag in the following package: " + packet);
				}

				// Set the version and the package of the FunctionDocument and add a
				// searchTag containing the Package name.
				func.setVersion(version);
				func.setPackage(packet);
				func.addSearchTag(packet);
			
				return func;
			} catch (SAXException e) {
				log.error("failed to parse an xmlFile in: " + packet);
			}

		} catch (ParserConfigurationException | IOException | NullPointerException e) {
			log.error("failed to parse an xmlFile in: " + packet);
		}

		return null;
	}
}
