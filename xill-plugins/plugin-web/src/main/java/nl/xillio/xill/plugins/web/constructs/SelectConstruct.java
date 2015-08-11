package nl.xillio.xill.plugins.web.constructs;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.web.data.WebVariable;
import nl.xillio.xill.plugins.web.services.web.WebService;

/**
 * Simulates selection of an item in HTML list
 */
public class SelectConstruct extends PhantomJSConstruct {

	@Override
	public ConstructProcessor prepareProcess(final ConstructContext context) {
		return new ConstructProcessor(
			(element, select) -> process(element, select, webService),
			new Argument("element", ATOMIC),
			new Argument("select", ATOMIC));
	}

	/**
	 * @param elementVar
	 *        input variable (should be of a NODE type) - web element
	 * @param selectVar
	 *        input boolean variable
	 * @param webService
	 *        The service we're using for accesing the web.
	 * @return null variable
	 */
	public static MetaExpression process(final MetaExpression elementVar, final MetaExpression selectVar, final WebService webService) {

		if(elementVar.isNull()){
			return NULL;
		}
		
		if (!checkNodeType(elementVar)) {
			throw new RobotRuntimeException("Invalid variable type. NODE type expected!");
		}
		else {

			boolean select = selectVar.getBooleanValue();

			WebVariable element = getNode(elementVar);

			try {
				// Check if we need to click
				if (select != webService.isSelected(element)) {
					webService.click(element);
				}
			} catch (Exception e) {
				throw new RobotRuntimeException("Failed to access NODE correctly", e);
			}
		}
		return NULL;
	}
}
