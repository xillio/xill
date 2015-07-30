package nl.xillio.xill.plugins.file.utils;

import java.io.File;

/**
 * This class represents various pieces of information about a folder
 */
public class Folder extends File {
    private final boolean accessible;

    /**
     * Create a new Folder object
     *
     * @param pathname   the path to the folder
     * @param accessible weather the Folder was accessible
     * @throws IllegalArgumentException if the passed path isn't a folder
     */
    public Folder(String pathname, boolean accessible) {
        super(pathname);
        this.accessible = accessible;

        if (!isDirectory()) {
            throw new IllegalArgumentException(getAbsolutePath() + " is not a folder.");
        }
    }

    /**
     * Check if this folder was accessible for the scraper
     *
     * @return true if and only if the iterator was able to access this folder
     */
    public boolean isAccessible() {
        return accessible;
    }
}
