package nl.xillio.xill.plugins.web.constructs;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.web.PhantomJSConstruct;
import nl.xillio.xill.plugins.web.data.WebVariable;
import nl.xillio.xill.plugins.web.services.web.WebService;

import org.openqa.selenium.NoSuchFrameException;

/**
 * Switch current page context to a provided frame
 */
public class SwitchFrameConstruct extends PhantomJSConstruct {

	@Override
	public ConstructProcessor prepareProcess(final ConstructContext context) {
		return new ConstructProcessor(
			(page, frame) -> process(page, frame, webService),
			new Argument("page", ATOMIC),
			new Argument("frame", ATOMIC));
	}

	/**
	 * @param pageVar
	 *        input variable (should be of a PAGE type)
	 * @param frameVar
	 *        input variable - frame specification - string or number or web element (NODE variable)
	 * @param webService
	 *        The service we're using to access the web.
	 * @return null variable
	 */
	public static MetaExpression process(final MetaExpression pageVar, final MetaExpression frameVar, final WebService webService) {

		if (!checkPageType(pageVar)) {
			throw new RobotRuntimeException("Invalid variable type. Page NODE type expected!");
		}
		// else

		WebVariable driver = getPage(pageVar);

		try {
			if (checkNodeType(frameVar)) {
				WebVariable element = getNode(frameVar);
				webService.switchToFrame(driver, element);
			} else {
				Object frame = MetaExpression.extractValue(frameVar);
				if (frame instanceof Integer) {
					webService.switchToFrame(driver, (Integer) frame);
				} else if (frame instanceof String) {
					webService.switchToFrame(driver, frame.toString());
				} else {
					throw new RobotRuntimeException("Invalid variable type of frame parameter!");
				}
			}
		} catch (NoSuchFrameException e) {
			throw new RobotRuntimeException("Requested frame doesn't exist.", e);
		} catch (RobotRuntimeException e) {
			throw e;
		} catch (Exception e) {
			throw new RobotRuntimeException(e.getClass().getSimpleName(), e);
		}

		return NULL;
	}

}
