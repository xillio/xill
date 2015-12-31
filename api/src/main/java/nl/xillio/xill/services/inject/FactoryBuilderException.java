package nl.xillio.xill.services.inject;

/**
 * This exception is generally thrown when a {@link Factory} failed to create an instance using {@link Factory#get()}
 */
public class FactoryBuilderException extends RuntimeException {

    private static final long serialVersionUID = -2311064385511102301L;

    /**
     * Create a new {@link FactoryBuilderException}
     *
     * @param message the message
     * @param e       the cause
     */
    public FactoryBuilderException(String message, Throwable e) {
        super(message, e);
    }


}
