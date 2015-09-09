package nl.xillio.xill.plugins.string.constructs;

import java.io.IOException;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.string.services.string.StringUtilityService;
import nl.xillio.xill.plugins.string.services.string.UrlUtilityService;

import com.google.inject.Inject;

/**
 * <p>
 * Returns the base-64 encoded string of the provided file.
 * </p>
 *
 * @author Sander
 *
 */
public class Base64EncodeConstruct extends Construct {
	@Inject
	StringUtilityService stringService;

	@Inject
	UrlUtilityService urlUtilityService;

	@Override
	public ConstructProcessor prepareProcess(final ConstructContext context) {
		return new ConstructProcessor(
			(file) -> process(file, stringService, urlUtilityService),
			new Argument("file", ATOMIC));
	}

	static MetaExpression process(final MetaExpression file, final StringUtilityService stringService, final UrlUtilityService urlUtilityService) {
		assertNotNull(file, "file");

		byte[] data = null;

		try {
			String filename = file.getStringValue();
			data = urlUtilityService.readFileToByteArray(filename);
		} catch (IOException e) {
			throw new RobotRuntimeException("IO Exception");
		}

		if (data != null && data.length > 0) {
			String content = stringService.printBase64Binary(data);
			if (content != null && !content.equals("")) {
				return fromValue(content);
			}
		}

		return NULL;

	}

}