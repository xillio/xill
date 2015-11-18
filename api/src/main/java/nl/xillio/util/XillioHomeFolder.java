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
     * Get the home directory for xill 3.0
     */
    public static File forXill3() {
        return new File(new XillioHomeFolder(), "xill/3.0");
    }


    /**
     * Get the home directory for xill IDE 3.0
     */
    public static File forXillIDE() {
        return new File(new XillioHomeFolder(), "xill_ide/3.0");
    }
}
