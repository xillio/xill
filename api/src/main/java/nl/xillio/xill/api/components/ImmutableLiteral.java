package nl.xillio.xill.api.components;

import nl.xillio.xill.api.data.MetadataExpression;

import java.util.ArrayList;
import java.util.Collection;

/**
 * This class represents a constant literal in the xill language
 */
class ImmutableLiteral extends MetaExpression {

    private final Expression value;

    /**
     * @param value the value to set
     */
    public ImmutableLiteral(final Expression value) {
        this.value = value;
        setValue(value);
    }

    @Override
    public Number getNumberValue() {
        return value.getNumberValue();
    }

    @Override
    public String getStringValue() {
        return value.getStringValue();
    }

    @Override
    public boolean getBooleanValue() {
        return value.getBooleanValue();
    }

    @Override
    public boolean isNull() {
        return value.isNull();
    }

    @Override
    public IOStream getBinaryValue() {
        return value.getBinaryValue();
    }

    @Override
    public Collection<Processable> getChildren() {
        return new ArrayList<>();
    }

    @Override
    public void close() {
        // Stub to prevent closing of literals

        // Clear out the meta pool
        closeMetaPool();

        // Make sure the reference count doesn't drop below 0
        resetReferences();
    }

    @Override
    public void storeMeta(MetadataExpression object) {
        // No OP
    }
}
