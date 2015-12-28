package nl.xillio.exiftool;

import nl.xillio.exiftool.query.FolderQueryOptions;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represents the options needed for a query on a folder.
 *
 * @author Thomas Biesaart
 */
public class FolderQueryOptionsImpl extends AbstractQueryOptions implements FolderQueryOptions {

    private boolean recursive = true;
    private String extensionFilter = "*";

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
    public List<String> buildArguments() {
        List<String> result = new ArrayList<>();
        if (isRecursive()) {
            result.add("-r");
        }
        result.add("-ext");

        result.add(getExtensionFilter());
        return result;
    }
}
