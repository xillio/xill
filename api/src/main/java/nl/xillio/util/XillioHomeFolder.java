package nl.xillio.util;

import java.io.File;

/**
 * This is a utility class to handle the xillio home directory.
 */
public class XillioHomeFolder extends File {
    private static final String VERSION = "3.0";

    private XillioHomeFolder() {
        super(System.getProperty("user.home"), ".xillio/");
    }

    /**
     * Get the home directory for xill 3.0.
     * @return the file
     */
    public static File forXill3() {
        return new File(new XillioHomeFolder(), "xill/" + VERSION);
    }


    /**
     * Get the home directory for xill IDE 3.0.
     * @return the file
     */
    public static File forXillIDE() {
        return new File(new XillioHomeFolder(), "xill_ide/" + VERSION);
    }


    /**
     * Get the home directory for xill Server.
     * @return the file
     */
    public static File forXillServer() {
        return new File(new XillioHomeFolder(), "xill_server/" + VERSION);
    }


}
