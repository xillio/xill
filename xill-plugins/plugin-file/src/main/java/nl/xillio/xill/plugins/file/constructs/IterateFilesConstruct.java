package nl.xillio.xill.plugins.file.constructs;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.components.MetaExpressionIterator;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.file.services.files.FileUtilities;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

/**
 * Creates an iterator that iterates over all files in a specific folder. If recursive is set to true the iterator will
 * also cover all files in the subdirectories
 */
@Singleton
public class IterateFilesConstruct extends Construct {

	@Inject
	private FileUtilities fileUtils;

	@Override
	public ConstructProcessor prepareProcess(final ConstructContext context) {
		return new ConstructProcessor(
						(uri, content) -> process(context, fileUtils, uri, content),
						new Argument("uri", ATOMIC),
						new Argument("recursive", FALSE, ATOMIC));
	}

	static MetaExpression process(final ConstructContext context, final FileUtilities fileUtils,
					final MetaExpression uri, final MetaExpression recursive) {
		File file = getFile(context.getRobotID(), uri.getStringValue());
		boolean isRecursive = recursive.getBooleanValue();
		try {
			MetaExpression result = fromValue("List files " + (isRecursive ? "recursively " : "") + "in " + file.getAbsolutePath());
			Iterator<File> iterator = fileUtils.iterateFiles(file, isRecursive);
			result.storeMeta(new MetaExpressionIterator<>(iterator, entry -> fromValue(entry.getAbsolutePath())));

			return result;

		} catch (IOException e) {
			throw new RobotRuntimeException("Failed to iterate files: " + e.getMessage(), e);
		}
	}
}
