package nl.xillio.xill.plugins.file.constructs;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.file.services.files.FileUtilities;

import java.io.File;
import java.io.IOException;

/**
 * This construct returns the size of a file or throws an error when the file was not found
 */
@Singleton
public class GetSizeConstruct extends Construct {

	@Inject
	private FileUtilities fileUtils;

	@Override
	public ConstructProcessor prepareProcess(final ConstructContext context) {
		return new ConstructProcessor((uri) -> process(context, fileUtils, uri), new Argument("uri", ATOMIC));
	}

	static MetaExpression process(final ConstructContext context, final FileUtilities fileUtils, final MetaExpression uri) {
		File file = fileUtils.buildFile(context.getRobotID(), uri.getStringValue());
		try {
			return fromValue(fileUtils.getByteSize(file));
		} catch (IOException e) {
			throw new RobotRuntimeException("Failed to get size of file: " + e.getMessage(), e);
		}
	}
}
