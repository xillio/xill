package nl.xillio.xill.plugins.web.constructs;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.web.services.web.WebService;

import com.google.inject.Inject;

/**
 * It will focus the provided web element on the web page
 */
public class FocusConstruct extends PhantomJSConstruct {

	@Inject
	private WebService webService;

	@Override
	public ConstructProcessor prepareProcess(final ConstructContext context) {
		return new ConstructProcessor(
			element -> process(element, webService),
			new Argument("element", ATOMIC));
	}

	/**
	 * @param elementVar
	 *        input variable (should be of a NODE type)
	 * @return null variable
	 */
	static MetaExpression process(final MetaExpression elementVar, final WebService webService) {

		if (!checkNodeType(elementVar)) {
			throw new RobotRuntimeException("Invalid variable type. NODE type expected!");
		}
		else {
			try {
				webService.moveToElement(getNode(elementVar));
			} catch (Exception e) {
				throw new RobotRuntimeException("Failed to focus on element.", e);
			}
			return NULL;
		}
	}
}
