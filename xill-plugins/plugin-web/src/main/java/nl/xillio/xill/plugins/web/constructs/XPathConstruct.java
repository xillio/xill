package nl.xillio.xill.plugins.web.constructs;

import java.util.ArrayList;
import java.util.List;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.web.data.WebVariable;
import nl.xillio.xill.plugins.web.services.web.WebService;

import org.openqa.selenium.InvalidSelectorException;

/**
 * Select web element(s) on the page according to provided XPath selector
 */
public class XPathConstruct extends PhantomJSConstruct {
	
	@Override
	public ConstructProcessor prepareProcess(final ConstructContext context) {
		return new ConstructProcessor(
			(element, xpath) -> process(element, xpath, webService),
			new Argument("element", ATOMIC),
			new Argument("xpath", ATOMIC));
	}

	/**
	 * @param elementVar
	 *        input variable (should be of a NODE or PAGE type)
	 * @param xpathVar
	 *        string variable specifying XPath selector
	 * @param webService
	 *        The web service we're using.
	 * @return NODE variable or list of NODE variables or null variable (according to count of selected web elements - more/1/0)
	 */
	public static MetaExpression process(final MetaExpression elementVar, final MetaExpression xpathVar, final WebService webService) {

		String query = xpathVar.getStringValue();

		if (checkPageType(elementVar)) {
			return processSELNode(getPage(elementVar), query, webService);
		} else if (checkNodeType(elementVar)) {
			return processSELNode(getNode(elementVar), query, webService);
		} else {
			throw new RobotRuntimeException("Unsupported variable type. PAGE or NODE type expected!");
		}
	}

	private static MetaExpression processSELNode(final WebVariable driver, String query, final WebService webService) {

		try {

			boolean textquery = query.endsWith("/text()");
			boolean attributequery = query.matches("^.*@\\w+$");
			String attribute = null;

			if (textquery) {
				query = stripTextTag(query);
			} else if (attributequery) {
				attribute = getAttribute(query);
				query = stripAttributeQuery(query);
			}

			List<WebVariable> results = webService.findElementsWithXpath(driver, query);

			if (results.size() == 0) {
				return NULL;
			} else if (results.size() == 1) {
				return parseSELVariable(driver, results.get(0), textquery, attribute, webService);
			} else {
				ArrayList<MetaExpression> list = new ArrayList<MetaExpression>();

				for (WebVariable he : results) {
					list.add(parseSELVariable(driver, he, textquery, attribute, webService));
				}

				return fromValue(list);
			}
		} catch (InvalidSelectorException e) {
			throw new RobotRuntimeException("Invalid XPath", e);
		}
	}

	/**
	 * Strips the query from the /text()
	 * 
	 * @param query
	 *        The query we need to strip.
	 * @return
	 *         The stripped query
	 */
	private static String stripTextTag(final String query) {
		try {
			return query.substring(0, query.length() - 7);
		} catch (IndexOutOfBoundsException e) {
			throw new RobotRuntimeException("An indexOutOfBoundsException occurred on: " + query, e);
		}
	}

	/**
	 * Gets the attribute part of the xpath
	 * 
	 * @param xpath
	 *        The xpath we want to extract the attribute from.
	 * @return
	 *         The attribute name.
	 */
	private static String getAttribute(final String xpath) {
		try {
			return xpath.substring(xpath.indexOf('@') + 1);
		} catch (IndexOutOfBoundsException e) {
			throw new RobotRuntimeException("An indexOutOfBoundsException occurred on: " + xpath + " when extracting the attribute.", e);
		}
	}

	/**
	 * Strips an attribute xpath till its core.
	 * 
	 * @param query
	 *        The query we want to strip.
	 * @return
	 *         The stripped query.
	 */
	private static String stripAttributeQuery(final String query) {
		try {
			return query.substring(0, query.lastIndexOf('/'));
		} catch (IndexOutOfBoundsException e) {
			throw new RobotRuntimeException("An indexOutOfBoundsException occurred on: " + query + " when indexing \\.", e);
		}
	}

	private static MetaExpression parseSELVariable(final WebVariable driver, final WebVariable element, final boolean textquery, final String attribute, final WebService webService) {
		if (textquery) {
			return fromValue(webService.getAttribute(element, "innerHTML"));
		} else if (attribute != null) {
			String val = webService.getAttribute(element, attribute);
			if (val == null) {
				return NULL;
			}
			return fromValue(val);
		} else {
			try {
				return createNode(driver, element, webService);
			} catch (Exception e) {
				throw new RobotRuntimeException("Failed to create node.", e);
			}
		}
	}
}
