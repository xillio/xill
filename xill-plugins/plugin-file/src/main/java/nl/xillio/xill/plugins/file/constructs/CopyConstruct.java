package nl.xillio.xill.plugins.file.constructs;

import java.io.File;
import java.io.IOException;

import com.google.inject.Inject;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.plugins.file.services.files.FileUtilities;

/**
 *
 */
public class CopyConstruct extends Construct {

	@Inject
	private FileUtilities fileUtils;

	@Override
	public ConstructProcessor prepareProcess(final ConstructContext context) {
		return new ConstructProcessor((source, target) -> process(context, fileUtils, source, target), new Argument("source", ATOMIC), new Argument("target", ATOMIC));
	}

	static MetaExpression process(final ConstructContext context, final FileUtilities fileUtils, final MetaExpression source, final MetaExpression target) {

		File sourceFile = fileUtils.buildFile(context.getRobotID(), source.getStringValue());
		File targetFile = fileUtils.buildFile(context.getRobotID(), target.getStringValue());
		try {
			fileUtils.copy(sourceFile, targetFile);
		} catch (IOException e) {
			context.getRootLogger().error("Failed to copy " + sourceFile.getName() + " to " + targetFile.getName());
		}
		return NULL;
	}
}
