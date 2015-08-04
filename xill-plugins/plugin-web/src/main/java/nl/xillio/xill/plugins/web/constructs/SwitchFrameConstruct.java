package nl.xillio.xill.plugins.web.constructs;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.web.PhantomJSConstruct;

import org.openqa.selenium.NoSuchFrameException;
import org.openqa.selenium.WebDriver;

/**
 * Switch current page context to a provided frame
 */
public class SwitchFrameConstruct extends PhantomJSConstruct {

	@Override
	public ConstructProcessor prepareProcess(final ConstructContext context) {
		return new ConstructProcessor(
			(page, frame) -> process(page, frame),
			new Argument("page"),
			new Argument("frame"));
	}

	/**
	 * @param pageVar
	 *        input variable (should be of a PAGE type)
	 * @param frameVar
	 *        input variable - frame specification - string or number or web element (NODE variable)
	 * @return null variable
	 */
	public static MetaExpression process(final MetaExpression pageVar, final MetaExpression frameVar) {

		if (!checkPageType(pageVar)) {
			throw new RobotRuntimeException("Invalid variable type. Page NODE type expected!");
		}
		// else

		WebDriver driver = getPageDriver(pageVar);

		try {
			if (checkNodeType(frameVar)) {
				driver.switchTo().frame(getNode(frameVar));
			} else {
				Object frame = MetaExpression.extractValue(frameVar);
				if (frame instanceof Integer) {
					driver.switchTo().frame((Integer) frame);
				} else if (frame instanceof String) {
					driver.switchTo().frame(frame.toString());
				} else {
					throw new RobotRuntimeException("Invalid variable type of frame parameter!");
				}
			}
		} catch (NoSuchFrameException e) {
			throw new RobotRuntimeException("Requested frame doesn't exist.", e);
		} catch (Exception e) {
			throw new RobotRuntimeException(e.getClass().getSimpleName(), e);
		}

		return NULL;
	}

}
