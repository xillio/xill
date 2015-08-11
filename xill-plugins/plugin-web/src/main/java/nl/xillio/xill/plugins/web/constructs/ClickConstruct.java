package nl.xillio.xill.plugins.web.constructs;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.web.services.web.WebService;

/**
 * Simulates click on the provided web element on the web page
 */
public class ClickConstruct extends PhantomJSConstruct {

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
		
		if(elementVar.isNull()){
			return NULL;
		}

		if (!checkNodeType(elementVar)) {
			throw new RobotRuntimeException("Invalid variable type. NODE type expected!");
		}
		// else
		try {
			webService.click(getNode(elementVar));
		} catch (Exception e) {
			throw new RobotRuntimeException("Stale element clicked.", e);
		}

		return NULL;
	}
}
