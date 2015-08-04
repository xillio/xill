package nl.xillio.xill.plugins.web.constructs;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.web.NodeVariableService;
import nl.xillio.xill.plugins.web.services.web.WebService;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.google.inject.Inject;

/**
 * It will focus the provided web element on the web page
 */
public class FocusConstruct extends Construct {

	@Inject
	private NodeVariableService nodeVariableService;

	@Inject
	private WebService webService;

	@Override
	public ConstructProcessor prepareProcess(final ConstructContext context) {
		return new ConstructProcessor(
			(element) -> process(element, webService, nodeVariableService),
			new Argument("element"));
	}

	/**
	 * @param elementVar
	 *        input variable (should be of a NODE type)
	 * @return null variable
	 */
	static MetaExpression process(final MetaExpression elementVar, final WebService webService, final NodeVariableService nodeVariableService) {

		if (!nodeVariableService.checkType(elementVar)) {
			throw new RobotRuntimeException("Invalid variable type. NODE type expected!");
		}
		// else

		WebElement element = nodeVariableService.get(elementVar);
		WebDriver page = nodeVariableService.getDriver(elementVar);

		webService.moveToElement(page, element);

		return NULL;
	}
}
