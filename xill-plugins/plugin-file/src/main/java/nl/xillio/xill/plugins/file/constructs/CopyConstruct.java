package nl.xillio.xill.plugins.file.constructs;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.plugins.file.services.files.FileUtilities;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;

/**
 * This construct will copy a file or folder to a target destination.
 */
@Singleton
public class CopyConstruct extends Construct {

	@Inject
	private FileUtilities fileUtils;

	@Override
	public ConstructProcessor prepareProcess(final ConstructContext context) {
		return new ConstructProcessor(
						(source, target) -> process(context, fileUtils, source, target),
						new Argument("source", ATOMIC),
						new Argument("target", ATOMIC));
	}

	static MetaExpression process(final ConstructContext context, final FileUtilities fileUtils, final MetaExpression source, final MetaExpression target) {

		File sourceFile = getFile(context, source.getStringValue());
		File targetFile = getFile(context, target.getStringValue());
		boolean success = true;
		try {
			fileUtils.copy(sourceFile, targetFile);
		} catch (IOException e) {
			context.getRootLogger().error("Failed to copy " + sourceFile.getName() + " to " + targetFile.getName() + ": " + e.getMessage(), e);
			success = false;
		}

		//Build the result
		LinkedHashMap<String, MetaExpression> result = new LinkedHashMap<>();
		result.put("from", fromValue(sourceFile.getAbsolutePath()));
		result.put("into", fromValue(targetFile.getAbsolutePath()));
		result.put("success", fromValue(success));
		return fromValue(result);
	}
}
