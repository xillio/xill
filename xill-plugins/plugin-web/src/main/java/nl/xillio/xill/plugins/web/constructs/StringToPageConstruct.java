package nl.xillio.xill.plugins.web.constructs;

import java.io.File;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.web.PageVariableService;

import org.apache.commons.io.FileUtils;

import com.google.inject.Inject;

/**
 * It loads web page from a provided string (the string represents HTML code of a web page)
 */
public class StringToPageConstruct extends Construct {
	@Inject
	PageVariableService pageVariableService;

	@Override
	public ConstructProcessor prepareProcess(final ConstructContext context) {
		return new ConstructProcessor(
			(content) -> process(content, pageVariableService),
			new Argument("content"));
	}

	/**
	 * @param contentVar
	 *        input string variable (HTML code of a web page)
	 * @return PAGE variable
	 */
	public static MetaExpression process(final MetaExpression contentVar, final PageVariableService pageVariableService) {
		String content = contentVar.getStringValue();

		try {
			File htmlFile = File.createTempFile("ct_sel", ".html");
			htmlFile.deleteOnExit();
			FileUtils.writeStringToFile(htmlFile, content);
			String uri = "file:///" + htmlFile.getAbsolutePath();
			return LoadPageConstruct.process(fromValue(uri), NULL, pageVariableService);
		} catch (Exception e) {
			throw new RobotRuntimeException(e.getClass().getSimpleName(), e);
		}
	}

}
