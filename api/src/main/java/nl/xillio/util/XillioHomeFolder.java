package nl.xillio.util;

import java.io.File;

/**
 * This class is a utility to address the xillio home directory.
 */
public class XillioHomeFolder extends File {
    private XillioHomeFolder() {
        super(System.getProperty("user.home"), ".xillio/");
    }

    /**
     * Get the home directory for xill3
     */
    public static File forXill3() {
        return new File(new XillioHomeFolder(), "xill3");
    }
}
