package nl.xillio.xill.plugins.exiftool.constructs;

import com.google.inject.Inject;
import nl.xillio.exiftool.ExifTool;
import nl.xillio.exiftool.ProcessPool;
import nl.xillio.exiftool.query.ExifReadResult;
import nl.xillio.exiftool.query.ExifTags;
import nl.xillio.exiftool.query.FolderQueryOptions;
import nl.xillio.exiftool.query.Projection;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.components.MetaExpressionIterator;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.exiftool.data.ExifQuery;
import nl.xillio.xill.plugins.exiftool.services.OptionsFactory;
import nl.xillio.xill.plugins.exiftool.services.ProjectionFactory;

import java.io.IOException;
import java.nio.file.Path;

/**
 * This construct will run a query on the exiftool wrapper.
 *
 * @author Thomas Biesaart
 */
public class ScanFolderConstruct extends Construct {

    private final ProcessPool processPool;
    private final ProjectionFactory projectionFactory;
    private final OptionsFactory optionsFactory;

    @Inject
    public ScanFolderConstruct(ProcessPool processPool, ProjectionFactory projectionFactory, OptionsFactory optionsFactory) {
        this.processPool = processPool;
        this.projectionFactory = projectionFactory;
        this.optionsFactory = optionsFactory;
    }

    @Override
    public ConstructProcessor prepareProcess(ConstructContext context) {
        context.addRobotStoppedListener(action -> processPool.clean());
        return new ConstructProcessor(
                (path, projection, options) -> process(path, projection, options, context),
                new Argument("folderPath", ATOMIC),
                new Argument("projection", emptyObject(), OBJECT),
                new Argument("options", emptyObject(), OBJECT)
        );
    }

    MetaExpression process(MetaExpression folderPath, MetaExpression projectionExpression, MetaExpression options, ConstructContext context) {
        Path file = getFile(context, folderPath.getStringValue()).toPath();
        Projection projection = projectionFactory.build(projectionExpression);

        MetaExpression result = fromValue("exif[" + file.toAbsolutePath().toString() + "]");
        ExifTool tool = processPool.getAvailable();

        FolderQueryOptions folderQueryOptions = optionsFactory.buildFolderOptions(options);

        ExifReadResult readResult = getResult(file, projection, folderQueryOptions, tool);

        MetaExpressionIterator<ExifTags> iterator = new MetaExpressionIterator<>(readResult, MetaExpression::parseObject);
        result.storeMeta(iterator);
        result.storeMeta(new ExifQuery(tool));

        return result;
    }

    private ExifReadResult getResult(Path file, Projection projection, FolderQueryOptions options, ExifTool tool) {
        try {
            return tool.readFieldsForFolder(file, projection, options);
        } catch (IOException e) {
            throw new RobotRuntimeException("Could not read folder: " + e.getMessage(), e);
        }
    }
}
