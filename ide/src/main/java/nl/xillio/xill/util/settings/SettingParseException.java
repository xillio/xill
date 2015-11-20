package nl.xillio.xill.util.settings;

import java.io.IOException;

/**
 * Specific exception class for the setting parsing
 *
 * Created by Anwar on 11/20/2015.
 */
public class SettingParseException extends IOException {

    public SettingParseException(String message, Throwable cause) {
        super(message, cause);
    }

    public SettingParseException(String message) { super(message); }
}
