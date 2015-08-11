package nl.xillio.xill.plugins.web.constructs;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.web.data.WebVariable;
import nl.xillio.xill.plugins.web.services.web.WebService;

/**
 * Returns information if provided web element is selected or not
 */
public class SelectedConstruct extends PhantomJSConstruct {

	@Override
	public ConstructProcessor prepareProcess(final ConstructContext context) {
		return new ConstructProcessor(
			element -> process(element, webService),
			new Argument("element", ATOMIC));
	}

	/**
	 * @param elementVar
	 *        input variable (should be of a NODE type) - web element
	 * @param webService
	 *        the service we're using.
	 * @return boolean variable (true=selected, false=not selected)
	 */
	public static MetaExpression process(final MetaExpression elementVar, final WebService webService) {

		if (!checkNodeType(elementVar)) {
			throw new RobotRuntimeException("Invalid variable type. NODE type expected!");
		}

		WebVariable element = getNode(elementVar);

		try {
			return fromValue(webService.isSelected(element));
		} catch (Exception e) {
			throw new RobotRuntimeException("Failed to access NODE correctly", e);
		}
	}
}
