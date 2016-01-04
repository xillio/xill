package nl.xillio.xill.plugins.exiftool.services;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.biesaart.utils.Log;
import nl.xillio.exiftool.*;
import nl.xillio.exiftool.query.FileQueryOptions;
import nl.xillio.exiftool.query.FolderQueryOptions;
import nl.xillio.exiftool.query.QueryOptions;
import nl.xillio.exiftool.query.TagNameConvention;
import nl.xillio.xill.api.components.ExpressionDataType;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import org.slf4j.Logger;

import java.util.Map;

/**
 * This class is responsible for converting MetaExpression to QueryOptions.
 *
 * @author Thomas Biesaart
 */
@Singleton
public class OptionsFactory {
    private static final Logger LOGGER = Log.get();
    private final ProjectionFactory projectionFactory;

    @Inject
    public OptionsFactory(ProjectionFactory projectionFactory) {
        this.projectionFactory = projectionFactory;
    }

    @SuppressWarnings("unchecked")
    public FolderQueryOptions buildFolderOptions(MetaExpression options) {
        if (options.getType() != ExpressionDataType.OBJECT) {
            throw new IllegalArgumentException("Options must be of type OBJECT");
        }

        FolderQueryOptions folderQueryOptions = new FolderQueryOptionsImpl();
        Map<String, MetaExpression> map = options.getValue();

        map.forEach((key, value) -> processFolder(folderQueryOptions, key.toLowerCase(), value));

        return folderQueryOptions;
    }

    @SuppressWarnings("unchecked")
    public FileQueryOptions buildFileOptions(MetaExpression options) {
        if (options.getType() != ExpressionDataType.OBJECT) {
            throw new IllegalArgumentException("Options must be of type OBJECT");
        }

        FileQueryOptions folderQueryOptions = new FileQueryOptionsImpl();
        Map<String, MetaExpression> map = options.getValue();

        map.forEach((key, value) -> processFile(folderQueryOptions, key.toLowerCase(), value));

        return folderQueryOptions;
    }

    private void processFolder(FolderQueryOptions folderQueryOptions, String option, MetaExpression value) {
        switch (option) {
            case "recursive":
                folderQueryOptions.setRecursive(value.getBooleanValue());
                break;
            case "extensions":
                try {
                    folderQueryOptions.setExtensionFilter(projectionFactory.build(value));
                } catch (IllegalArgumentException e) {
                    throw new RobotRuntimeException("Invalid extension projection: " + e.getMessage(), e);
                }
                break;
            default:
                process(folderQueryOptions, option, value);
        }
    }

    private void processFile(FileQueryOptions fileQueryOptions, String option, MetaExpression value) {
        switch (option) {
            default:
                process(fileQueryOptions, option, value);
        }
    }

    private void process(QueryOptions queryOptions, String option, MetaExpression value) {
        switch (option) {
            case "nameconvention":
                queryOptions.setTagNameConvention(getConvention(value.getStringValue()));
                break;
            default:
                LOGGER.warn("Unknown option [" + option + "]");
        }
    }

    private TagNameConvention getConvention(String tagName) {
        switch (tagName) {
            case "capitalword":
            case "cw":
                return new CapitalWordNameConvention();
            case "uppercamelcase":
            case "ucc":
                return new UpperCamelCaseNameConvention();
            case "lowercamelcase":
            case "lcc":
            default:
                return new LowerCamelCaseNameConvention();
        }
    }
}
