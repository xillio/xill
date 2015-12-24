package nl.xillio.xill.plugins.exiftool.constructs;

import com.google.inject.Inject;
import nl.xillio.exiftool.ProcessPool;
import nl.xillio.exiftool.query.ExifReadResult;
import nl.xillio.exiftool.query.ExifTags;
import nl.xillio.exiftool.query.Projection;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.components.MetaExpressionIterator;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.api.errors.RobotRuntimeException;

import java.io.IOException;
import java.nio.file.Path;

/**
 * This construct will run a query on the exiftool wrapper.
 *
 * @author Thomas Biesaart
 */
public class ScanFolderConstruct extends Construct {

    private final ProcessPool processPool;

    @Inject
    public ScanFolderConstruct(ProcessPool processPool) {
        this.processPool = processPool;
    }

    @Override
    public ConstructProcessor prepareProcess(ConstructContext context) {
        return new ConstructProcessor(
                (path, projection, options) -> process(path, projection, options, context),
                new Argument("folderPath", ATOMIC),
                new Argument("projection", emptyObject(), OBJECT),
                new Argument("options", emptyObject(), OBJECT)
        );
    }

    MetaExpression process(MetaExpression folderPath, MetaExpression projection, MetaExpression options, ConstructContext context) {
        Path file = getFile(context, folderPath.getStringValue()).toPath();

        MetaExpression result = fromValue("exif[" + file.toAbsolutePath().toString() + "]");

        ExifReadResult readResult = getResult(file);

        MetaExpressionIterator<ExifTags> iterator = new MetaExpressionIterator<>(readResult, MetaExpression::parseObject);
        result.storeMeta(iterator);

        return result;
    }

    private ExifReadResult getResult(Path file) {
        try {
            return processPool.getAvailable().readFieldsForFolder(file, new Projection());
        } catch (IOException e) {
            throw new RobotRuntimeException("Could not read folder: " + e.getMessage(), e);
        }
    }
}
