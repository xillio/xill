package nl.xillio.xill.plugins.web.constructs;

import java.util.ArrayList;
import java.util.List;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.api.construct.ExpressionBuilderHelper;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.web.NodeVariableService;
import nl.xillio.xill.plugins.web.PageVariableService;

import org.openqa.selenium.By;
import org.openqa.selenium.InvalidElementStateException;
import org.openqa.selenium.InvalidSelectorException;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.google.inject.Inject;

/**
 * Select web element(s) on the page according to provided CSS Path selector
 */
public class CSSPathConstruct extends Construct {

	@Inject
	private NodeVariableService nodeVariableService;

	@Inject
	private PageVariableService pageVariableService;

	@Override
	public String getName() {
		return "cssPath";
	}

	@Override
	public ConstructProcessor prepareProcess(final ConstructContext context) {
		return new ConstructProcessor(
			(element, csspath) -> process(element, csspath, nodeVariableService, pageVariableService),
			new Argument("element"),
			new Argument("csspath"));
	}

	/**
	 * @param elementVar
	 *        input variable (should be of a NODE or PAGE type)
	 * @param cssPathVar
	 *        string variable specifying CSS Path selector
	 * @return NODE variable or list of NODE variables or null variable (according to count of selected web elements - more/1/0)
	 */
	private static MetaExpression process(final MetaExpression elementVar, final MetaExpression cssPathVar, final NodeVariableService nodeVariableService, final PageVariableService pageVariableService) {

		String query = cssPathVar.getStringValue();

		if (elementVar.isNull()) {
			return NULL;
		} else if (nodeVariableService.checkType(elementVar)) {
			return processSELNode(nodeVariableService.getDriver(elementVar), nodeVariableService.get(elementVar), query, nodeVariableService);
		} else if (pageVariableService.checkType(elementVar)) {
			return processSELNode(pageVariableService.getDriver(elementVar), pageVariableService.getDriver(elementVar), query, nodeVariableService);
		} else {
			throw new RobotRuntimeException("Invalid variable type. PAGE or NODE type expected!");
		}
	}

	private static MetaExpression processSELNode(final WebDriver driver, final SearchContext node, final String selector, final NodeVariableService nodeVariableService) {

		try {
			List<WebElement> results = node.findElements(By.cssSelector(selector));

			if (results.isEmpty()) {
				return NULL;
			} else if (results.size() == 1) {
				return nodeVariableService.create(driver, results.get(0));
			} else {
				ArrayList<MetaExpression> list = new ArrayList<MetaExpression>();

				for (WebElement he : results) {
					list.add(nodeVariableService.create(driver, he));
				}

				return ExpressionBuilderHelper.fromValue(list);
			}
		} catch (InvalidElementStateException e) {
			throw new RobotRuntimeException("Invalid CSSPath", e);
		} catch (InvalidSelectorException e) {
			throw new RobotRuntimeException("Invalid CSSPath", e);
		}
	}

}
