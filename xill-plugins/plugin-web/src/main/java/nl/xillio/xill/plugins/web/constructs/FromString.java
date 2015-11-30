package nl.xillio.xill.plugins.web.constructs;

import java.io.File;
import java.io.IOException;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.web.PhantomJSConstruct;
import nl.xillio.xill.plugins.web.data.OptionsFactory;
import nl.xillio.xill.plugins.web.services.web.FileService;
import nl.xillio.xill.plugins.web.services.web.WebService;

import com.google.inject.Inject;

/**
 * It loads web page from a provided string (the string represents HTML code of a web page)
 */
public class FromString extends PhantomJSConstruct {
	@Inject
	private FileService fileService;
	@Inject
	private OptionsFactory optionsFactory;

	@Override
	public ConstructProcessor prepareProcess(final ConstructContext context) {
		return new ConstructProcessor(
			content -> process(content, optionsFactory, fileService, webService),
			new Argument("content", ATOMIC));
	}

	/**
	 * @param contentVar
	 *        input string variable (HTML code of a web page)
	 * @param optionsFactory
	 *        The factory for creating options the {@link LoadPageConstruct} will be using.
	 * @param fileService
	 *        The service for files we're using.
	 * @param webService
	 *        The webservice the {@link LoadPageConstruct} will be using.
	 * @return PAGE variable
	 */
	public static MetaExpression process(final MetaExpression contentVar, final OptionsFactory optionsFactory, final FileService fileService, final WebService webService) {
		String content = contentVar.getStringValue();

		try {
			File htmlFile = fileService.createTempFile("ct_sel", ".html");
			fileService.writeStringToFile(htmlFile, content);
			String uri = "file:///" + fileService.getAbsolutePath(htmlFile);
			return LoadPageConstruct.process(fromValue(uri), NULL, optionsFactory, webService);
		} catch (IOException e) {
			throw new RobotRuntimeException("An IO error occurred.", e);
		} catch (Exception e) {
			throw new RobotRuntimeException("Failed to load the generated page.", e);
		}
	}

}
