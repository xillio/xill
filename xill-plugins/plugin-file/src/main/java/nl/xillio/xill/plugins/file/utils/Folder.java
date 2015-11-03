package nl.xillio.xill.plugins.file.utils;

import java.io.File;

/**
 * This class represents various pieces of information about a folder
 */
public class Folder extends File {
    /**
     * Create a new Folder object
     *
     * @param pathname   the path to the folder
     * @throws IllegalArgumentException if the passed path isn't a folder
     */
    public Folder(String pathname) {
        super(pathname);

        if (!isDirectory()) {
            throw new IllegalArgumentException(getAbsolutePath() + " is not a folder.");
        }
    }
}
