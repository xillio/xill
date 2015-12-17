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

/**
 * This Construct will create a folder and return the absolute path to that folder
 */
@Singleton
public class CreateFolderConstruct extends Construct {

	@Inject
	private FileUtilities fileUtils;

	@Override
	public ConstructProcessor prepareProcess(final ConstructContext context) {
		return new ConstructProcessor(
						uri -> process(context, fileUtils, uri),
						new Argument("uri", ATOMIC));
	}

	static MetaExpression process(final ConstructContext context, final FileUtilities fileUtils, final MetaExpression uri) {

		File folder = getFile(context, uri.getStringValue());

		try {
			fileUtils.createFolder(folder);
		} catch (IOException e) {
			context.getRootLogger().error("Failed to create " + folder.getAbsolutePath(), e);
		}
		return fromValue(folder.getAbsolutePath());
	}
}
