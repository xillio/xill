package nl.xillio.xill.api.components;

import nl.xillio.xill.api.io.IOStream;

import java.util.Objects;

/**
 * This class represents the behavior of an expression that contains binary data.
 *
 * @author Thomas biesaart
 */
class BinaryBehavior extends AbstractBehavior {

    private final IOStream value;
    private final String reference;

    BinaryBehavior(IOStream value) {
        this(value, null);
    }

    BinaryBehavior(IOStream value, String description) {
        Objects.requireNonNull(value);
        this.value = value;

        if (description == null) {
            reference = "[Stream]";
        } else {
            reference = "[Stream: " + description + "]";
        }
    }


    @Override
    public String getStringValue() {
        return reference;
    }

    @Override
    public boolean getBooleanValue() {
        return true;
    }

    @Override
    public IOStream getBinaryValue() {
        return value;
    }
}
