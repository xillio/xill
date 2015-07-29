package nl.xillio.xill.plugins.file.constructs;

import java.io.File;
import java.io.IOException;

import com.google.inject.Inject;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.file.services.extraction.TextExtractor;
import nl.xillio.xill.plugins.file.services.files.FileUtilities;

/**
 *
 */
public class GetTextConstruct extends Construct {

	@Inject
	private TextExtractor textExtractor;
	@Inject
	private FileUtilities fileUtilities;

	@Override
	public ConstructProcessor prepareProcess(final ConstructContext context) {
		return new ConstructProcessor(
		  (file, timeout) -> process(context, textExtractor, fileUtilities, file, timeout),
		  new Argument("file", ATOMIC),
		  new Argument("timeout", fromValue(2000)));
	}

	static MetaExpression process(final ConstructContext context, final TextExtractor extractor, final FileUtilities fileUtils,
	    final MetaExpression file, final MetaExpression timeout) {

		File target = fileUtils.buildFile(context.getRobotID(), file.getStringValue());
		try {
			String text = extractor.extractText(target);

			return fromValue(text);
		} catch (UnsupportedOperationException | IOException e) {
			throw new RobotRuntimeException("Failed to extract text: " + e.getMessage(), e);
		}
	}
}
