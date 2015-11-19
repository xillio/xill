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
import nl.xillio.xill.plugins.file.utils.Folder;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.Iterator;
import java.util.LinkedHashMap;

/**
 * Creates an iterator that iterates over all files in a specific folder. If recursive is set to true the iterator will
 * also cover all files in the subdirectories
 */
@Singleton
public class IterateFoldersConstruct extends Construct {

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
		File file = getFile(context, uri.getStringValue());
		boolean isRecursive = recursive.getBooleanValue();
		try {
			MetaExpression result = fromValue("List folders " + (isRecursive ? "recursively " : "") + "in " + file.getAbsolutePath());
			Iterator<Folder> iterator = fileUtils.iterateFolders(file, isRecursive);
			result.storeMeta(new MetaExpressionIterator<>(iterator, IterateFoldersConstruct::createExpression));

			return result;

		} catch (IOException e) {
			if (e instanceof NoSuchFileException) {
				throw new RobotRuntimeException("The specified folder does not exist:  " + e.getMessage(), e);
			} else if (e instanceof AccessDeniedException) {
				throw new RobotRuntimeException("Access to the specified folder is denied:  " + e.getMessage(), e);
			} else if (e instanceof NotDirectoryException) {
				throw new RobotRuntimeException("The specified folder is not a directory:  " + e.getMessage(), e);
			}
			throw new RobotRuntimeException("An error occurred: " + e.getMessage(), e);
		}
	}

	/**
	 * Build a MetaExpression from a Folder
	 *
	 * @param folder the folder
	 * @return the MetaExpression
	 */
	private static MetaExpression createExpression(Folder folder) {
		LinkedHashMap<String, MetaExpression> value = new LinkedHashMap<>();

		Path path = folder.toPath();

		value.put("path", fromValue(folder.getAbsolutePath()));
		value.put("canRead", fromValue(Files.isReadable(path)));
		value.put("canWrite", fromValue(Files.isWritable(path)));
		value.put("isAccessible", fromValue(Files.isReadable(path) & Files.isWritable(path) & Files.isExecutable(path)));

		if (folder.getParentFile() != null) {
			value.put("parent", fromValue(folder.getParentFile().getAbsolutePath()));
		}

		return fromValue(value);
	}
}
