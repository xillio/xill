package nl.xillio.xill.plugins.excel;

/**
 * This exception is thrown whenever a sheet is requested that does not exist.
 *
 * @author Thomas Biesaart
 */
public class NoSuchSheetException extends Exception {
    public NoSuchSheetException(String message) {
        super(message);
    }
}
