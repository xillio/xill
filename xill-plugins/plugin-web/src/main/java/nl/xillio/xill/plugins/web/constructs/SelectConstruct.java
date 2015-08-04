package nl.xillio.xill.plugins.web.constructs;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.web.NodeVariableService;

import org.openqa.selenium.WebElement;

import com.google.inject.Inject;

/**
 * Simulates selection of an item in HTML list
 */
public class SelectConstruct extends Construct {
	@Inject
	private NodeVariableService nodeVariableService;

	@Override
	public ConstructProcessor prepareProcess(final ConstructContext context) {
		return new ConstructProcessor(
			(element, select) -> process(element, select, nodeVariableService),
			new Argument("element"),
			new Argument("select"));
	}

	/**
	 * @param elementVar
	 *        input variable (should be of a NODE type) - web element
	 * @param selectVar
	 *        input boolean variable
	 * @return null variable
	 */
	public static MetaExpression process(final MetaExpression elementVar, final MetaExpression selectVar, final NodeVariableService nodeVariableService) {

		if (!nodeVariableService.checkType(elementVar)) {
			throw new RobotRuntimeException("Invalid variable type. NODE type expected!");
		}
		// else

		boolean select = selectVar.getBooleanValue();

		WebElement element = nodeVariableService.get(elementVar);

		try {
			if (select && !element.isSelected() || !select && element.isSelected()) { // if it's <option> tag then "deselect" doesn't work
				element.click();
			}
			return NULL;
		} catch (Exception e) {
			throw new RobotRuntimeException(e.getClass().getSimpleName(), e);
		}
	}
}
