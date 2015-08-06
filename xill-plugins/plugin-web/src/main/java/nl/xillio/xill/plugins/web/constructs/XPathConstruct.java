package nl.xillio.xill.plugins.web.constructs;

import java.util.ArrayList;
import java.util.List;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.web.PhantomJSConstruct;
import nl.xillio.xill.plugins.web.services.web.StringService;
import nl.xillio.xill.plugins.web.services.web.WebService;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.InvalidSelectorException;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.google.inject.Inject;

/**
 * Select web element(s) on the page according to provided XPath selector
 */
public class XPathConstruct extends PhantomJSConstruct {
	private static final Logger log = LogManager.getLogger();
	
	@Inject
	private StringService stringService;

	@Override
	public ConstructProcessor prepareProcess(final ConstructContext context) {
		return new ConstructProcessor(
			(element, xpath) -> process(element, xpath, stringService, webService),
			new Argument("element"),
			new Argument("xpath"));
	}

	/**
	 * @param elementVar
	 *        input variable (should be of a NODE or PAGE type)
	 * @param xpathVar
	 *        string variable specifying XPath selector
	 * @param stringService
	 * 					The string service we're using.
	 * @param webService
	 * 					The web service we're using.
	 * @return NODE variable or list of NODE variables or null variable (according to count of selected web elements - more/1/0)
	 */
	public static MetaExpression process(final MetaExpression elementVar, final MetaExpression xpathVar, final StringService stringService, final WebService webService) {

		String query = xpathVar.getStringValue();

		if (checkPageType(elementVar)) {
			return processSELNode(getPageDriver(elementVar), getPageDriver(elementVar), query, stringService, webService);
		} else if (checkNodeType(elementVar)) {
			return processSELNode(getNodeDriver(elementVar), getNode(elementVar), query, stringService, webService);
		} else {
			throw new RobotRuntimeException("Unsupported variable type!");
		}
	}

	private static MetaExpression processSELNode(final WebDriver driver, final SearchContext node, String query, final StringService stringService, final WebService webService) {

		try {
			
			boolean textquery = stringService.endsWith(query, "/text()");
			boolean attributequery = stringService.matches(query, "^.*@\\w+$");
			String attribute = null;

			if (textquery) {
				try{
				query = stringService.subString(query, 0, query.length() - 7);
				}
				catch(IndexOutOfBoundsException e){
					log.error("An indexOutoFBoundsException occurred on: " + query);
				}
			}

			if (attributequery) {
				try{
				attribute = stringService.subString(query, 0, stringService.lastIndexOf(query, '@') + 1);
				query = stringService.subString(query, 0, stringService.lastIndexOf(query, '/'));
				}
				catch(IndexOutOfBoundsException e){
					log.error("An indexOutoFBoundsException occurred on: " + query);
				}
			}

			List<WebElement> results = webService.findElementsWithXpath(node, query);

			if (results.size() == 0) {
				return NULL;
			} else if (results.size() == 1) {
				return parseSELVariable(driver, results.get(0), textquery, attribute, webService);
			} else {
				ArrayList<MetaExpression> list = new ArrayList<MetaExpression>();

				for (WebElement he : results) {
					list.add(parseSELVariable(driver, he, textquery, attribute, webService));
				}

				return fromValue(list);
			}
		} catch (InvalidSelectorException e) {
			throw new RobotRuntimeException("Invalid XPath", e);
		}
	}

	private static MetaExpression parseSELVariable(final WebDriver driver, final WebElement element, final boolean textquery, final String attribute, final WebService webService) {
		if (textquery) {
			return fromValue(webService.getAttribute(element, "innerHTML"));
		}

		if (attribute != null) {
			String val = webService.getAttribute(element, attribute);
			if (val == null) {
				return NULL;
			}
			return fromValue(val);
		}

		return createNode(driver, element, webService);
	}

}
