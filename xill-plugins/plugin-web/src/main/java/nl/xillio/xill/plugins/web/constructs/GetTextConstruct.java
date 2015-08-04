package nl.xillio.xill.plugins.web.constructs;

import java.util.List;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.web.NodeVariableService;
import nl.xillio.xill.plugins.web.PageVariableService;
import nl.xillio.xill.plugins.web.services.web.WebService;

import org.openqa.selenium.WebElement;

import com.google.inject.Inject;

/**
 * Gets the text content from provided web element
 */
public class GetTextConstruct extends Construct {

	@Inject
	private NodeVariableService nodeVariableService;

	@Inject
	private PageVariableService pageVariableService;

	@Inject
	private WebService webService;

	@Override
	public ConstructProcessor prepareProcess(final ConstructContext context) {
		return new ConstructProcessor(
			(element) -> process(element, webService, nodeVariableService, pageVariableService),
			new Argument("element"));
	}

	/**
	 * @param elementVar
	 *        input variable (should be of a NODE type or list of NODE type variables)
	 * @return string variable that contains the text(s) of the provided web element(s)
	 */
	public static MetaExpression process(final MetaExpression elementVar, final WebService webService, final NodeVariableService nodeVariableService, final PageVariableService pageVariableService) {
		assertNotNull(elementVar, "element");

		String output = "";
		if (elementVar.getType() == LIST) {
			@SuppressWarnings("unchecked")
			List<MetaExpression> list = (List<MetaExpression>) elementVar.getValue();
			for (MetaExpression item : list) {
				output += processItem(item, webService, nodeVariableService, pageVariableService);
			}
		} else {
			output = processItem(elementVar, webService, nodeVariableService, pageVariableService);
		}

		return fromValue(output);
	}

	private static String processItem(final MetaExpression var, final WebService webService, final NodeVariableService nodeVariableService, final PageVariableService pageVariableService) {
		WebElement element = null;
		if (nodeVariableService.checkType(var)) {
			element = nodeVariableService.get(var);
		} else if (pageVariableService.checkType(var)) {
			element = (WebElement) pageVariableService.getDriver(var);
		} else {
			throw new RobotRuntimeException("Invalid variable type.");
		}

		String text = "";
		if (webService.getTagName(element).equals("input") || webService.getTagName(element).equals("textarea")) {
			text = webService.getAttribute(element, "value");
		} else {
			text = webService.getText(element);
		}
		return text;
	}
}
