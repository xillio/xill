package nl.xillio.xill.api.components;

import nl.xillio.xill.api.io.IOStream;

import java.util.ArrayList;
import java.util.Collection;

/**
 * This {@link MetaExpression} represents an expression that holds an {@link ExpressionDataType#ATOMIC} value.
 */
class AtomicExpression extends MetaExpression {

    private final Expression expressionValue;

    /**
     * Creates a new atomic expression that hosts an {@link Expression}.
     *
     * @param value the value to set
     */
    public AtomicExpression(final Expression value) {
        setValue(value);

        // Save to prevent casting
        expressionValue = value;
    }

    /**
     * Creates a new atomic expression with a {@link BooleanBehavior}.
     *
     * @param value the value to set
     */
    public AtomicExpression(final boolean value) {
        this(new BooleanBehavior(value));
    }

    /**
     * Creates a new atomic expression with a {@link NumberBehavior}.
     *
     * @param value the value to set
     */
    public AtomicExpression(final Number value) {
        this(new NumberBehavior(value));
    }

    /**
     * Creates a new atomic with a {@link StringBehavior}.
     *
     * @param value the value to set
     */
    public AtomicExpression(final String value) {
        this(new StringBehavior(value));
    }

    @Override
    public Number getNumberValue() {
        return expressionValue.getNumberValue();
    }

    @Override
    public String getStringValue() {
        return expressionValue.getStringValue();
    }

    @Override
    public boolean getBooleanValue() {
        return expressionValue.getBooleanValue();
    }

    @Override
    public boolean isNull() {
        return expressionValue.isNull();
    }

    @Override
    public IOStream getBinaryValue() {
        return expressionValue.getBinaryValue();
    }

    @Override
    public void close() {
        super.close();
        expressionValue.close();
    }

    @Override
    public Collection<Processable> getChildren() {
        return new ArrayList<>();
    }

}
