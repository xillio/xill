package nl.xillio.xill.plugins.web.constructs;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.web.PhantomJSConstruct;

import org.openqa.selenium.WebElement;

/**
 * Returns information if provided web element is selected or not
 */
public class SelectedConstruct extends PhantomJSConstruct {

	@Override
	public ConstructProcessor prepareProcess(final ConstructContext context) {
		return new ConstructProcessor(
			(element) -> process(element),
			new Argument("element"));
	}

	/**
	 * @param elementVar
	 *        input variable (should be of a NODE type) - web element
	 * @return boolean variable (true=selected, false=not selected)
	 */
	public static MetaExpression process(final MetaExpression elementVar) {

		if (!checkNodeType(elementVar)) {
			throw new RobotRuntimeException("Invalid variable type. NODE type expected!");
		}
		// else

		WebElement element = getNode(elementVar);

		try {
			return fromValue(element.isSelected());
		} catch (Exception e) {
			throw new RobotRuntimeException(e.getClass().getSimpleName(), e);
		}
	}
}
