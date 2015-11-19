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
import java.nio.file.AccessDeniedException;
import java.nio.file.NoSuchFileException;
import java.nio.file.NotDirectoryException;
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
		File file = getFile(context, uri.getStringValue());
		boolean isRecursive = recursive.getBooleanValue();
		try {
			MetaExpression result = fromValue("List files " + (isRecursive ? "recursively " : "") + "in " + file.getAbsolutePath());
			Iterator<File> iterator = fileUtils.iterateFiles(file, isRecursive);
			result.storeMeta(new MetaExpressionIterator<>(iterator, entry -> fromValue(entry.getAbsolutePath())));

			return result;

		} catch (NoSuchFileException e) {
            throw new RobotRuntimeException("The specified folder does not exist:  " + e.getMessage(), e);
        } catch (AccessDeniedException e) {
            throw new RobotRuntimeException("Access to the specified folder is denied:  " + e.getMessage(), e);
        } catch (NotDirectoryException e) {
            throw new RobotRuntimeException("The specified folder is not a directory:  " + e.getMessage(), e);
        } catch (IOException e) {
			throw new RobotRuntimeException("An error occurred: " + e.getMessage(), e);
		}
	}
}
