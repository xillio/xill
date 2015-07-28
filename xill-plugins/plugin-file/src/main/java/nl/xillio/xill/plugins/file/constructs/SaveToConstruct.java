package nl.xillio.xill.plugins.file.constructs;

import java.io.File;
import java.io.IOException;

import com.google.inject.Inject;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.plugins.file.services.fileUtils.FileUtilities;

/**
 *
 */
public class SaveToConstruct extends Construct {

	@Inject
	private FileUtilities fileUtils;

	@Override
	public ConstructProcessor prepareProcess(final ConstructContext context) {
		return new ConstructProcessor(
		  (content, uri) -> process(context, fileUtils, uri, content),
		  new Argument("content", ATOMIC),
		  new Argument("uri", ATOMIC));
	}

	static MetaExpression process(final ConstructContext context, final FileUtilities fileUtils, final MetaExpression uri, final MetaExpression content) {
		File file = new File(uri.getStringValue());
		try {
			fileUtils.saveStringToFile(content.getStringValue(), file);
		} catch (IOException e) {
			context.getRootLogger().error("Failed to write to file: " + e.getMessage(), e);
		}
		return fromValue(file.getAbsolutePath());
	}
}
