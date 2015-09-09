package nl.xillio.xill.plugins.xml.services;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import nl.xillio.xill.api.errors.RobotRuntimeException;

import org.apache.logging.log4j.LogManager;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.apache.logging.log4j.Logger;

import com.google.inject.Singleton;

/**
 * This class is the main implementation of the {@link XsdService}
 *
 * @author Zbynek Hochmann
 */

@Singleton
public class XsdServiceImpl implements XsdService, ErrorHandler {
	static final String JAXP_SCHEMA_LANGUAGE = "http://java.sun.com/xml/jaxp/properties/schemaLanguage";
	static final String JAXP_SCHEMA_SOURCE = "http://java.sun.com/xml/jaxp/properties/schemaSource";
	static final String W3C_XML_SCHEMA = "http://www.w3.org/2001/XMLSchema";
	private static final Logger LOGGER = LogManager.getLogger();

	private final DocumentBuilderFactory dbf;
	private LinkedList<String> messages = new LinkedList<String>();

	/**
	 * Constructor of @XsdServiceImpl class
	 * It prepares and set up DocumentBuilderFactory
	 */
	public XsdServiceImpl() {
		dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(true);
		dbf.setValidating(true);

		try {
			dbf.setAttribute(JAXP_SCHEMA_LANGUAGE, W3C_XML_SCHEMA);
		} catch (IllegalArgumentException e) {
			LOGGER.error("JAXP DocumentBuilderFactory attribute not recognized: " + JAXP_SCHEMA_LANGUAGE, e);
		}
	}

	@Override
	public boolean xsdCheck(final File xmlFile, final File xsdFile, final Logger logger) {
		messages.clear();

		dbf.setAttribute(JAXP_SCHEMA_SOURCE, xsdFile);
		try {
			DocumentBuilder db = dbf.newDocumentBuilder();
			db.setErrorHandler(this);
			db.parse(xmlFile);
		} catch (IOException e) {
			throw new RobotRuntimeException("XSD check error\n" + e.getMessage(), e);
		} catch (Exception e) {
			logger.warn("XSD check failed\n" + e.getMessage() + (e.getCause() != null ? e.getCause().getMessage() : ""), e);
			return false;
		}

		boolean result = messages.isEmpty();
		if (!result) {
			final String text[] = {"XSD check failed\n"};
			messages.forEach(v -> {
				text[0] += v;
				text[0] += "\n";
			});
			logger.warn(text[0]);
		}
		return result;
	}

	@Override
	public void error(final SAXParseException e) throws SAXException {
		message(e);
	}

	@Override
	public void fatalError(final SAXParseException e) throws SAXException {
		message(e);
	}

	@Override
	public void warning(final SAXParseException e) throws SAXException {
		message(e);
	}

	private void message(final SAXParseException e) {
		messages.add("Line " + e.getLineNumber() + ", Char " + e.getColumnNumber() + ": " + e.getMessage());
	}

}