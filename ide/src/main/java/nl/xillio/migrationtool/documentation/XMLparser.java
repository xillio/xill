package nl.xillio.migrationtool.documentation;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * This class is an implementation of the adaptor pattern.
 * It wraps around the SAX implementation of parsing XML and uses that implementation to extract a FunctionDocument from an XML file.
 * 
 * @author Ivor
 */
public class XMLparser {

    /**
     * Reads a stream and parses it to return a FunctionDocument.
     * @param stream
     *            The url from which we parse
     * @return A FunctionDocument
     * @throws SAXException
     * @throws IOException
     */
    public FunctionDocument parseXML(final InputStream stream, final String version) throws SAXException, IOException {

	// Initiate the reader and its handler
	XMLReader xr = XMLReaderFactory.createXMLReader();
	XML_Format_Handler handler = new XML_Format_Handler();

	xr.setContentHandler(handler);
	xr.setErrorHandler(handler);
	xr.parse(new InputSource(stream));
	FunctionDocument docu = ((XML_Format_Handler) xr.getContentHandler()).getFunction();
	docu.setVersion(version);
	return docu;
    }

    private static class XML_Format_Handler extends DefaultHandler implements AutoCloseable {
	String attribute;
	ArrayList<String> parameterTypes;
	// The string we're trying to add
	private StringBuilder xmlText = new StringBuilder();
	// The functionDocument we're creating
	FunctionDocument function = new FunctionDocument();

	/**
	 * @return Returns the FunctionDocument.
	 */
	public FunctionDocument getFunction() {
	    return function;
	}

	@Override
	public void startElement(final String uri, final String localName, final String qName,
		final Attributes attributes) throws SAXException {

	    // Check with which element we're dealing
	    if (qName.equals("param")) {
		attribute = attributes.getValue("name");
		parameterTypes = new ArrayList<String>();
	    }
	    if (qName.equals("example")) {
		attribute = attributes.getValue("description");
	    }

	    // Check whether we're running into a tag we accept, else keep
	    // going.
	    if (qName.equals("name") || qName.equals("param") || qName.equals("description") || qName.equals("example")
		    || qName.equals("application") || qName.equals("tag") || qName.equals("searchTag")
		    || qName.equals("type")) {
		xmlText = new StringBuilder();
	    }

	}

	@Override
	public void endElement(final String uri, final String localName, final String qName) throws SAXException {
	    if (qName.equals("name")) {
		function.setName(xmlText.toString().trim());
	    }
	    if (qName.equals("param")) {
		String types = "";
		for (String type : parameterTypes) {
		    types += type + " ";
		}
		types.substring(0, types.length() - 1);
		function.addParameter(types, attribute);
		attribute = "";
	    }
	    if (qName.equals("type")) {
		parameterTypes.add(xmlText.toString());
	    }
	    if (qName.equals("description")) {
		function.setDescription(xmlText.toString());
	    }
	    if (qName.equals("example")) {
		function.addExample(attribute, xmlText.toString());
		attribute = "";
	    }
	    if (qName.equals("application")) {
		function.addApplication(xmlText.toString());
	    }
	    if (qName.equals("tag")) {
		function.addLink(xmlText.toString());
	    }
	    if (qName.equals("searchTag")) {
		function.addSearchTag(xmlText.toString());
	    }
	    xmlText = new StringBuilder();
	}

	// Parses the characters in the way the Internet recommends it.

	@Override
	public void characters(final char ch[], final int start, final int length) throws SAXException {

	    xmlText.append(ch, start, length);
	}

	@Override
	public void close() throws Exception {
	    xmlText.setLength(0);

	}
    }

}
