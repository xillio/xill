package nl.xillio.xill.plugins.file.constructs;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import me.biesaart.utils.FileUtilsService;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.api.errors.RobotRuntimeException;

/**
 * Read text from a plain text file using the given encoding
 */
@Singleton
public class GetTextConstruct extends Construct {

	@Inject
	private FileUtilsService fileUtils;

	@Override
	public ConstructProcessor prepareProcess(final ConstructContext context) {
		return new ConstructProcessor(
			(file, encoding) -> process(context, fileUtils, file, encoding),
			new Argument("file", ATOMIC),
			new Argument("encoding", NULL, ATOMIC));
	}

	static MetaExpression process(final ConstructContext context, final FileUtilsService fileUtils, final MetaExpression file, final MetaExpression encoding) {
		File target = getFile(context, file.getStringValue());

		// Get the charset safely, if it was given
		Charset charset = null;
		if (!encoding.isNull()) {
			try {
				charset = Charset.forName(encoding.getStringValue());
			} catch (IllegalArgumentException e) {
				throw new RobotRuntimeException("Encoding not supported: " + e.getMessage(), e);
			}
		}

		try {
			// Read the file
			String text;
			if (charset != null) {
				text = fileUtils.readFileToString(target, charset);
			} else {
				text = fileUtils.readFileToString(target);
			}

			return fromValue(text);
		} catch (IOException e) {
			throw new RobotRuntimeException("Failed to get text: " + e.getMessage(), e);
		}
	}
}
