package nl.xillio.xill.services.inject;

/**
 * This exception is generally thrown when a {@link Factory} fails to create an instance using {@link Factory#get()}.
 */
public class FactoryBuilderException extends RuntimeException {

    private static final long serialVersionUID = -2311064385511102301L;

    /**
     * Constructs a new runtime exception with the specified detail message and cause.
     *
     * @param message the message
     * @param e       the cause
     */
    public FactoryBuilderException(String message, Throwable e) {
        super(message, e);
    }


}
