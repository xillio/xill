package nl.xillio.xill.plugins.web.constructs;

import java.util.ArrayList;
import java.util.List;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.web.PhantomJSConstruct;

import org.openqa.selenium.By;
import org.openqa.selenium.InvalidSelectorException;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * Select web element(s) on the page according to provided XPath selector
 */
public class XPathConstruct extends PhantomJSConstruct {

	@Override
	public ConstructProcessor prepareProcess(final ConstructContext context) {
		return new ConstructProcessor(
			(element, xpath) -> process(element, xpath),
			new Argument("element"),
			new Argument("xpath"));
	}

	/**
	 * @param elementVar
	 *        input variable (should be of a NODE or PAGE type)
	 * @param xpathVar
	 *        string variable specifying XPath selector
	 * @return NODE variable or list of NODE variables or null variable (according to count of selected web elements - more/1/0)
	 */
	public static MetaExpression process(final MetaExpression elementVar, final MetaExpression xpathVar) {

		String query = xpathVar.getStringValue();

		if (checkPageType(elementVar)) {
			return processSELNode(getPageDriver(elementVar), getPageDriver(elementVar), query);
		} else if (checkNodeType(elementVar)) {
			return processSELNode(getNodeDriver(elementVar), getNode(elementVar), query);
		} else {
			throw new RobotRuntimeException("Unsupported variable type!");
		}
	}

	private static MetaExpression processSELNode(final WebDriver driver, final SearchContext node, String query) {

		try {

			boolean textquery = query.endsWith("/text()");
			boolean attributequery = query.matches("^.*@\\w+$");
			String attribute = null;

			if (textquery) {
				query = query.substring(0, query.length() - 7);
			}

			if (attributequery) {
				attribute = query.substring(query.lastIndexOf('@') + 1);
				query = query.substring(0, query.lastIndexOf('/'));
			}

			List<WebElement> results = node.findElements(By.xpath(query));

			if (results.size() == 0) {
				// log.debug("No results");
				return NULL;
			} else if (results.size() == 1) {
				return parseSELVariable(driver, results.get(0), textquery, attribute);
			} else {
				ArrayList<MetaExpression> list = new ArrayList<MetaExpression>();

				for (WebElement he : results) {
					list.add(parseSELVariable(driver, he, textquery, attribute));
				}

				return fromValue(list);
			}
		} catch (InvalidSelectorException e) {
			throw new RobotRuntimeException("Invalid XPath", e);
		}
	}

	private static MetaExpression parseSELVariable(final WebDriver driver, final WebElement e, final boolean textquery, final String attribute) {
		if (textquery) {
			return fromValue(e.getAttribute("innerHTML"));
		}

		if (attribute != null) {
			String val = e.getAttribute(attribute);
			if (val == null) {
				return NULL;
			}
			return fromValue(val);
		}

		return createNode(driver, e);
	}

}
