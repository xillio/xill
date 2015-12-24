package nl.xillio.exiftool.query;

/**
 * This interface represents the base interface for all queries that relate to folders.
 *
 * @author Thomas Biesaart
 */
public interface FolderQueryOptions extends QueryOptions {

    boolean isRecursive();

    void setRecursive(boolean recursive);

    String getExtensionFilter();

    void setExtensionFilter(String filter);
}
