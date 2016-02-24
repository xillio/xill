package nl.xillio.xill.api.components;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * <p>
 * This class represents a written list in a script e.g. [1,2,3,4].
 * </p>
 * Values:
 * <ul>
 * <li><b>{@link String}: </b> the JSON representation</li>
 * <li><b>{@link Boolean}: </b> {@code false} if the list is {@code null}, {@code true} otherwise (even if empty)</li>
 * <li><b>{@link Number}: </b> {@link Double#NaN}</li>
 * </ul>
 */

class ListExpression extends CollectionExpression {

    private final List<? extends MetaExpression> value;

    /**
     * @param value the value to set
     */
    public ListExpression(final List<MetaExpression> value) {
        this.value = value;

        setValue(value);
        //Register references
        value.forEach(MetaExpression::registerReference);
    }

    @Override
    public Collection<Processable> getChildren() {
        return new ArrayList<>(value);
    }

    @Override
    public Number getNumberValue() {
        return Double.NaN;
    }
}
