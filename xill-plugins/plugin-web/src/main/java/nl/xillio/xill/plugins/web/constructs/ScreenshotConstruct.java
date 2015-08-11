package nl.xillio.xill.plugins.web.constructs;

import java.io.File;
import java.io.IOException;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.web.data.WebVariable;
import nl.xillio.xill.plugins.web.services.web.FileService;
import nl.xillio.xill.plugins.web.services.web.WebService;

import com.google.inject.Inject;

/**
 * Capture screenshot of currently loaded page and save it to a .png file
 */
public class ScreenshotConstruct extends PhantomJSConstruct {
	@Inject
	private FileService fileService;

	@Override
	public ConstructProcessor prepareProcess(final ConstructContext context) {
		return new ConstructProcessor(
			(page, fileName) -> process(page, fileName, fileService, webService),
			new Argument("page", ATOMIC),
			new Argument("filename", ATOMIC));
	}

	/**
	 * @param pageVar
	 *        input variable (should be of a PAGE type)
	 * @param fileService
	 *        The service we're using for files.
	 * @param webService
	 *        The service we're using for accessing the web.
	 * @param fileNameVar
	 *        input string variable - output .png filepath
	 * @return null variable
	 */
	public static MetaExpression process(final MetaExpression pageVar, final MetaExpression fileNameVar, final FileService fileService, final WebService webService) {

		String fileName = fileNameVar.getStringValue();
		if (fileName.isEmpty()) {
			throw new RobotRuntimeException("Invalid variable value. Filename is empty!");
		}

		if (!checkPageType(pageVar)) {
			throw new RobotRuntimeException("Invalid variable type. Node PAGE type expected!");
		}
		else {
			WebVariable driver = getPage(pageVar);

			try {
				File srcFile = webService.getScreenshotAsFile(driver);
				File desFile = fileService.makeFile(fileName);
				fileService.copyFile(srcFile, desFile);
			} catch (IOException e) {
				throw new RobotRuntimeException("Failed to copy to: " + fileName);
			} catch (Exception e) {
				throw new RobotRuntimeException("Failed to access page without errors.");
			}
		}

		return NULL;
	}

}
