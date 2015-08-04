package nl.xillio.xill.plugins.web.constructs;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.web.PhantomJSConstruct;

import org.openqa.selenium.WebElement;

/**
 * Simulates selection of an item in HTML list
 */
public class SelectConstruct extends PhantomJSConstruct {

	@Override
	public ConstructProcessor prepareProcess(final ConstructContext context) {
		return new ConstructProcessor(
			(element, select) -> process(element, select),
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
	public static MetaExpression process(final MetaExpression elementVar, final MetaExpression selectVar) {

		if (!checkNodeType(elementVar)) {
			throw new RobotRuntimeException("Invalid variable type. NODE type expected!");
		}
		// else

		boolean select = selectVar.getBooleanValue();

		WebElement element = getNode(elementVar);

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
