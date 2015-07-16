package nl.xillio.xill.plugins.web.constructs;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.phantomjs.PhantomJSDriver;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.web.PageVariable;

public class ScreenshotConstruct extends Construct {

	@Override
	public String getName() {
		return "screenshot";
	}

	@Override
	public ConstructProcessor prepareProcess(final ConstructContext context) {
		return new ConstructProcessor(ScreenshotConstruct::process, new Argument("page"), new Argument("filename"));
	}

	public static MetaExpression process(final MetaExpression pageVar, final MetaExpression filenameVar) {

		String filename = filenameVar.getStringValue();
		if (filename.isEmpty()) {
			throw new RobotRuntimeException("Invalid variable value. Filename is empty!");
		}

		if (!PageVariable.checkType(pageVar)) {
			throw new RobotRuntimeException("Invalid variable type. Page NODE type expected!");
		}
		// else

		try {
			PhantomJSDriver driver = (PhantomJSDriver) PageVariable.getDriver(pageVar);
			File srcFile = driver.getScreenshotAs(OutputType.FILE);
			FileUtils.copyFile(srcFile, new File(filename));
		} catch (Exception e) {
			throw new RobotRuntimeException(e.getClass().getSimpleName(), e);
		}

		return NULL;
	}

}
