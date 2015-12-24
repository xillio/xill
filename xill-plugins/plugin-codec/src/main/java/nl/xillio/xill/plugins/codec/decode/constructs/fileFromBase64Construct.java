package nl.xillio.xill.plugins.codec.decode.constructs;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.codec.decode.services.DecoderService;

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
public class fileFromBase64Construct extends Construct {
	@Inject
	DecoderService decoderService;
	@Inject
	UrlUtilityService urlUtilityService;

	@Override
	public ConstructProcessor prepareProcess(final ConstructContext context) {
		return new ConstructProcessor(
			(content, filename) -> process(content, filename, decoderService, urlUtilityService, context),
			new Argument("content", ATOMIC),
			new Argument("filename", ATOMIC));
	}

	static MetaExpression process(final MetaExpression contentVar, final MetaExpression filenameVar, final DecoderService decoderService, final UrlUtilityService urlUtilityService, final ConstructContext context) {

		assertNotNull(contentVar, "content");
		assertNotNull(filenameVar, "filename");

		String content = contentVar.getStringValue();
		File file = getFile(context, filenameVar.getStringValue());

		byte[] data = decoderService.parseBase64Binary(content);

		try {
			urlUtilityService.write(file, data);
		} catch (FileNotFoundException e) {
			throw new RobotRuntimeException("The file could not be found or the filename is invalid: " + e.getMessage(), e);
		} catch (IOException e) {
			throw new RobotRuntimeException("Error writing to file: " + e.getMessage(), e);
		}
		return NULL;
	}
}
