package nl.xillio.xill.plugins.file.constructs;

import com.google.inject.Inject;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.plugins.file.services.files.FileUtilities;

import java.io.File;
import java.io.IOException;

/**
 * Appends content in the form of text to a file, creating it if it doesn't exist. Returns the absolute path to the file.
 */
public class AppendToConstruct extends Construct {
	
	@Inject
	private FileUtilities fileUtils;

	@Override
	public ConstructProcessor prepareProcess(final ConstructContext context) {
		return new ConstructProcessor(
						(uri, content) -> process(context, fileUtils, uri, content),
						new Argument("uri", ATOMIC),
						new Argument("content", ATOMIC));
	}

	static MetaExpression process(final ConstructContext context, final FileUtilities fileUtils, final MetaExpression uri, final MetaExpression content) {
		File file = getFile(context.getRobotID(), uri.getStringValue());
		try {
			fileUtils.appendStringToFile(content.getStringValue(), file);
		} catch (IOException e) {
			context.getRootLogger().error("Failed to write to file: " + e.getMessage(), e);
		}
		return fromValue(file.getAbsolutePath());
	}
}
