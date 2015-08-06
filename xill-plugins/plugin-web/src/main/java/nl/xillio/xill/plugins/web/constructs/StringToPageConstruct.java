package nl.xillio.xill.plugins.web.constructs;

import java.io.File;
import java.io.IOException;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.web.PhantomJSConstruct;
import nl.xillio.xill.plugins.web.services.web.FileService;
import nl.xillio.xill.plugins.web.services.web.WebService;

import org.apache.commons.io.FileUtils;

import com.google.inject.Inject;

/**
 * It loads web page from a provided string (the string represents HTML code of a web page)
 */
public class StringToPageConstruct extends PhantomJSConstruct {
	@Inject
	private FileService fileService;

	@Override
	public ConstructProcessor prepareProcess(final ConstructContext context) {
		return new ConstructProcessor(
			(content) -> process(content, fileService, webService),
			new Argument("content"));
	}

	/**
	 * @param contentVar
	 *        input string variable (HTML code of a web page)
	 * @param fileService 
	 * @param webService 
	 * @return PAGE variable
	 */
	public static MetaExpression process(final MetaExpression contentVar, FileService fileService, WebService webService) {
		String content = contentVar.getStringValue();

		try {
			File htmlFile = fileService.createTempFile("ct_sel", ".html");
			fileService.writeStringToFile(htmlFile, content);
			String uri = "file:///" + fileService.getAbsolutePath(htmlFile);
			return LoadPageConstruct.process(fromValue(uri), NULL, webService);
		} catch (IOException e) {
			throw new RobotRuntimeException("An IO error occurred.");
		}
		catch (Exception e){
			throw new RobotRuntimeException("An error occurred.", e);
		}
	}

}
