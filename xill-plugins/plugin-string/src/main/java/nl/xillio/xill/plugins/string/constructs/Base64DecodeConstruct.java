package nl.xillio.xill.plugins.string.constructs;

import java.io.FileNotFoundException;
import java.io.IOException;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.string.services.string.StringService;
import nl.xillio.xill.plugins.string.services.string.UrlService;

import com.google.inject.Inject;

/**
 *
 * <p>
 * Base-64 decode the provided string, and store it in the provided file.
 * </p>
 *
 * @author Sander
 *
 */
public class Base64DecodeConstruct extends Construct {
	@Inject
	StringService stringService;
	@Inject
	UrlService urlService;

	@Override
	public ConstructProcessor prepareProcess(final ConstructContext context) {
		return new ConstructProcessor(
			(content, filename) -> process(content, filename, stringService, urlService),
			new Argument("content", ATOMIC),
			new Argument("filename", ATOMIC));
	}

	static MetaExpression process(final MetaExpression contentVar, final MetaExpression filenameVar, final StringService stringService, final UrlService urlService) {

		assertNotNull(contentVar, "content");
		assertNotNull(filenameVar, "filename");

		String content = contentVar.getStringValue();
		String filename = filenameVar.getStringValue();

		byte[] data = stringService.parseBase64Binary(content);

		try {
			urlService.write(filename, data);
		} catch (FileNotFoundException e) {
			throw new RobotRuntimeException("The file could not be found or the filename is invalid: '" + filename + "'");
		} catch (IOException e) {
			throw new RobotRuntimeException("IO Exception");
		}
		return NULL;
	}
}
