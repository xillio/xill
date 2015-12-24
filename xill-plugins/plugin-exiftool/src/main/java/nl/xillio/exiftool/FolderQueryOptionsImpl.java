package nl.xillio.exiftool;

import nl.xillio.exiftool.query.FolderQueryOptions;
import nl.xillio.exiftool.query.TagNameConvention;

/**
 * This class represents the options needed for a query on a folder.
 *
 * @author Thomas Biesaart
 */
public class FolderQueryOptionsImpl implements FolderQueryOptions {

    private boolean recursive = true;
    private String extensionFilter = "*";
    private TagNameConvention tagNameConvention = new LowerCamelCaseNameConvention();

    @Override
    public boolean isRecursive() {
        return recursive;
    }

    @Override
    public void setRecursive(boolean recursive) {
        this.recursive = recursive;
    }

    @Override
    public String getExtensionFilter() {
        return extensionFilter;
    }

    @Override
    public void setExtensionFilter(String filter) {
        this.extensionFilter = filter;
    }

    @Override
    public TagNameConvention getTagNameConvention() {
        return tagNameConvention;
    }

    @Override
    public void setTagNameConvention(TagNameConvention tagNameConvention) {
        this.tagNameConvention = tagNameConvention;
    }
}
