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
 * Simulates key presses on provided web element (i.e. clear and write text)
 */
public class InputConstruct extends Construct {

	@Inject
	private NodeVariableService nodeVariableService;

	@Override
	public ConstructProcessor prepareProcess(final ConstructContext context) {
		return new ConstructProcessor(
			(element, text) -> process(element, text, nodeVariableService),
			new Argument("element"),
			new Argument("text"));
	}

	/**
	 * @param elementVar
	 *        input variable (should be of a NODE type) - web element
	 * @param textVar
	 *        input string variable - text to be written to web element
	 * @return null variable
	 */
	public static MetaExpression process(final MetaExpression elementVar, final MetaExpression textVar, final NodeVariableService nodeVariableService) {

		if (!nodeVariableService.checkType(elementVar)) {
			throw new RobotRuntimeException("Invalid variable type. NODE type expected!");
		}
		// else

		String text = textVar.getStringValue();

		WebElement element = nodeVariableService.get(elementVar);

		try {
			element.clear();
			element.sendKeys(text);
		} catch (Exception e) {
			throw new RobotRuntimeException(e.getClass().getSimpleName(), e);
		}

		return NULL;
	}

}
