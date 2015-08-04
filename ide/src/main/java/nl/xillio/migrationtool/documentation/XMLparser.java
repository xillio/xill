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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
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
	public FunctionDocument parseXML(final InputStream xml, final String packet, final String version) {
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
				XPathExpression descriptionExpr = xpath.compile("/function/description/text()");
				func.setDescription(((String) descriptionExpr.evaluate(doc, XPathConstants.STRING)).trim());

				// Parse the parameters
				XPathExpression paramsNameExpr = xpath.compile("/function/parameters/param/@name");
				XPathExpression paramsDefExpr = xpath.compile("/function/parameters/param/@default");
				NodeList parameterNames = (NodeList) paramsNameExpr.evaluate(doc, XPathConstants.NODESET);
				NodeList defaultValues = (NodeList) paramsDefExpr.evaluate(doc, XPathConstants.NODESET);
				for (int t = 0; t < parameterNames.getLength(); ++t) {
					String parameterName = parameterNames.item(t).getTextContent().trim();
					if (defaultValues.item(t) != null) {
						String defaultValue = defaultValues.item(t).getTextContent().trim();
						func.addParameter(parameterName, defaultValue);
					} else {
						func.addParameter(parameterName);
					}
				}

				// Parse the examples
				XPathExpression examplesExpr = xpath.compile("/function/examples/example/text()");
				XPathExpression exampleDescriptionsExpr = xpath.compile("/function/examples/example/@description");
				NodeList examples = (NodeList) examplesExpr.evaluate(doc, XPathConstants.NODESET);
				NodeList exampleDescriptions = (NodeList) exampleDescriptionsExpr.evaluate(doc, XPathConstants.NODESET);
				for (int t = 0; t < examples.getLength(); ++t) {
					func.addExample(exampleDescriptions.item(t).getTextContent().trim(),
						examples.item(t).getTextContent().trim());
				}

				// Parse the tags
				XPathExpression tagsExpr = xpath.compile("/function/references/reference/text()");
				XPathExpression tagsPackageExpr = xpath.compile("/function/references/reference/@package");
				NodeList tags = (NodeList) tagsExpr.evaluate(doc, XPathConstants.NODESET);
				NodeList tagsPackage = (NodeList) tagsPackageExpr.evaluate(doc, XPathConstants.NODESET);
				for (int t = 0; t < tags.getLength(); ++t) {
					func.addLink(tagsPackage.item(t).getTextContent().trim(), tags.item(t).getTextContent().trim());
				}

				// Parse the searchTags
				XPathExpression searchTagsExpr = xpath.compile("/function/searchTags/searchTag");
				NodeList searchTags = (NodeList) searchTagsExpr.evaluate(doc, XPathConstants.NODESET);
				for (int t = 0; t < searchTags.getLength(); ++t) {
					func.addSearchTag(searchTags.item(0).getTextContent().trim());
				}

				// Set the version and the package of the FunctionDocument and add a
				// searchTag containing the Package name.
				func.setVersion(version);
				func.setPackage(packet);
				func.addSearchTag(packet);
			
				return func;
			} catch (SAXException e) {
				log.error(xml);
				e.printStackTrace();
			}

		} catch (ParserConfigurationException | IOException e) {
			e.printStackTrace();
		} catch (XPathExpressionException e) {
			e.printStackTrace();
		}

		return null;
	}
}
