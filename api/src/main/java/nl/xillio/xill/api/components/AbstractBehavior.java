package nl.xillio.xill.api.components;

import nl.xillio.xill.api.io.IOStream;

/**
 * This class represents a null implementation of the expression class.
 *
 * @author Thomas biesaart
 */
class AbstractBehavior implements Expression {
    private IOStream emptyStream;

    @Override
    public Number getNumberValue() {
        return Double.NaN;
    }

    @Override
    public String getStringValue() {
        return "";
    }

    @Override
    public boolean getBooleanValue() {
        return false;
    }

    @Override
    public boolean isNull() {
        return false;
    }

    @Override
    public IOStream getBinaryValue() {
        if (emptyStream == null) {
            emptyStream = new EmptyIOStream();
        }
        return emptyStream;
    }

    @Override
    public String toString() {
        return getStringValue();
    }
}
