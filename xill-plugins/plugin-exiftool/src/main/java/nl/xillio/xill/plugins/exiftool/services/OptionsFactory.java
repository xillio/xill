package nl.xillio.xill.plugins.exiftool.services;

import com.google.inject.Singleton;
import nl.xillio.exiftool.FolderQueryOptionsImpl;
import nl.xillio.exiftool.LowerCamelCaseNameConvention;
import nl.xillio.exiftool.query.FolderQueryOptions;
import nl.xillio.exiftool.query.QueryOptions;
import nl.xillio.exiftool.query.TagNameConvention;
import nl.xillio.xill.api.components.ExpressionDataType;
import nl.xillio.xill.api.components.MetaExpression;

import java.util.Map;

/**
 * This class is responsible for converting MetaExpression to QueryOptions.
 *
 * @author Thomas Biesaart
 */
@Singleton
public class OptionsFactory {

    @SuppressWarnings("unchecked")
    public FolderQueryOptions buildFolderOptions(MetaExpression options) {
        if (options.getType() != ExpressionDataType.OBJECT) {
            throw new IllegalArgumentException("Options must be of type OBJECT");
        }

        FolderQueryOptions folderQueryOptions = new FolderQueryOptionsImpl();
        Map<String, MetaExpression> map = (Map<String, MetaExpression>) options.getValue();

        map.forEach((key, value) -> processFolder(folderQueryOptions, key, value));

        return folderQueryOptions;
    }

    private void processFolder(FolderQueryOptions folderQueryOptions, String option, MetaExpression value) {
        switch (option) {
            case "recursive":
                folderQueryOptions.setRecursive(value.getBooleanValue());
                break;
            case "extensions":
                folderQueryOptions.setExtensionFilter(value.getStringValue());
                break;
            default:
                process(folderQueryOptions, option, value);
        }
    }

    private void process(QueryOptions queryOptions, String option, MetaExpression value) {
        switch (option) {
            case "nameConvention":
                queryOptions.setTagNameConvention(getConvention(value.getStringValue()));
                break;
        }
    }

    private TagNameConvention getConvention(String tagName) {
        switch (tagName) {
            case "lowerCamelCase":
            case "lcc":
            default:
                return new LowerCamelCaseNameConvention();
        }
    }
}
