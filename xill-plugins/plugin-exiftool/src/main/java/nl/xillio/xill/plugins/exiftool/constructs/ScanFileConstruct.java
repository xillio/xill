package nl.xillio.xill.plugins.exiftool.constructs;

import com.google.inject.Inject;
import nl.xillio.exiftool.ExifTool;
import nl.xillio.exiftool.ProcessPool;
import nl.xillio.exiftool.query.ExifTags;
import nl.xillio.exiftool.query.FileQueryOptions;
import nl.xillio.exiftool.query.Projection;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.exiftool.services.OptionsFactory;

import java.io.IOException;
import java.nio.file.Path;

/**
 * This construct will run a query on the exiftool wrapper for a single file.
 *
 * @author Thomas Biesaart
 */
public class ScanFileConstruct extends AbstractExifConstruct {

    private final ProcessPool processPool;
    private final OptionsFactory optionsFactory;

    @Inject
    public ScanFileConstruct(ProcessPool processPool, OptionsFactory optionsFactory) {
        this.processPool = processPool;
        this.optionsFactory = optionsFactory;
    }

    @Override
    public ConstructProcessor prepareProcess(ConstructContext context) {
        return new ConstructProcessor(
                (path, projection, options) -> process(path, projection, options, context),
                new Argument("filePath", ATOMIC),
                new Argument("projection", emptyObject(), OBJECT),
                new Argument("options", emptyObject(), OBJECT)
        );
    }

    MetaExpression process(MetaExpression folderPath, MetaExpression projectionExpression, MetaExpression options, ConstructContext context) {
        Path file = getFile(context, folderPath.getStringValue()).toPath();
        Projection projection = getProjection(projectionExpression);
        FileQueryOptions fileQueryOptions = optionsFactory.buildFileOptions(options);

        ExifTags tags = run(file, projection, fileQueryOptions);
        return parseObject(tags);
    }

    private ExifTags run(Path file, Projection projection, FileQueryOptions options) {
        try (ExifTool tool = processPool.getAvailable()) {
            return tool.readFieldsForFile(file, projection, options);
        } catch (IOException e) {
            throw new RobotRuntimeException("Could not read file: " + e.getMessage(), e);
        }
    }
}
