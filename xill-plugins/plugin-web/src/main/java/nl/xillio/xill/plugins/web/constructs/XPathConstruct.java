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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.InvalidSelectorException;

/**
 * Select web element(s) on the page according to provided XPath selector
 */
public class XPathConstruct extends PhantomJSConstruct {
	private static final Logger log = LogManager.getLogger();

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
				try {
					query = query.substring(0, query.length() - 7);
				} catch (IndexOutOfBoundsException e) {
					log.error("An indexOutoFBoundsException occurred on: " + query);
				}
			} else if (attributequery) {
				try {
					attribute = query.substring(query.indexOf('@') + 1);
					query = query.substring(0, query.lastIndexOf('/'));
				} catch (IndexOutOfBoundsException e) {
					log.error("An indexOutoFBoundsException occurred on: " + query);
				}
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
				throw new RobotRuntimeException("Failed to create node.");
			}
		}
	}
}
