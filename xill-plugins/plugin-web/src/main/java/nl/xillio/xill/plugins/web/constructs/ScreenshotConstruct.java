package nl.xillio.xill.plugins.web.constructs;

import java.io.File;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.web.PhantomJSConstruct;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.phantomjs.PhantomJSDriver;

/**
 * Capture screenshot of currently loaded page and save it to a .png file
 */
public class ScreenshotConstruct extends PhantomJSConstruct {

	@Override
	public ConstructProcessor prepareProcess(final ConstructContext context) {
		return new ConstructProcessor(
			(page, fileName) -> process(page, fileName),
			new Argument("page"),
			new Argument("filename"));
	}

	/**
	 * @param pageVar
	 *        input variable (should be of a PAGE type)
	 * @param filenameVar
	 *        input string variable - output .png filepath
	 * @return null variable
	 */
	public static MetaExpression process(final MetaExpression pageVar, final MetaExpression filenameVar) {

		String filename = filenameVar.getStringValue();
		if (filename.isEmpty()) {
			throw new RobotRuntimeException("Invalid variable value. Filename is empty!");
		}

		if (!checkPageType(pageVar)) {
			throw new RobotRuntimeException("Invalid variable type. Page NODE type expected!");
		}
		// else

		try {
			PhantomJSDriver driver = (PhantomJSDriver) getPageDriver(pageVar);
			File srcFile = driver.getScreenshotAs(OutputType.FILE);
			FileUtils.copyFile(srcFile, new File(filename));
		} catch (Exception e) {
			throw new RobotRuntimeException(e.getClass().getSimpleName(), e);
		}

		return NULL;
	}

}
