package nl.xillio.migrationtool;

/**
 * Custom Xillio runtime exception class
 * <p>
 * Created by Anwar on 11/17/2015.
 */
public class XillioRuntimeException extends RuntimeException {

    public XillioRuntimeException() {

    }

    public XillioRuntimeException(String message) {
        super(message);
    }

    public XillioRuntimeException(String message, Throwable t) {
        super(message, t);
    }

    public XillioRuntimeException(Throwable t) {
        super(t);
    }
}
