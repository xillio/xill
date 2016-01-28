package nl.xillio.xill.plugins.math.services.math;

import com.google.inject.ImplementedBy;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.plugins.math.MathXillPlugin;

/**
 * This interface represents some of the operations for the {@link MathXillPlugin}
 */
@ImplementedBy(MathOperationsImpl.class)
public interface MathOperations {

    /**
     * Get the absolute value
     *
     * @param value the value
     * @return the absolute value
     */
    public double abs(Number value);

    /**
     * Round a number to it's nearest integer
     *
     * @param value the value
     * @return the rounded integer or long
     */
    public long round(Number value);

    /**
     * Returns a random double between 0 and 1.
     *
     * @return A random double between 0 and 1.
     */
    public double random();

    /**
     * Returns a random long between 0 and a given value.
     *
     * @param value The max value.
     * @return A long between 0 and max value.
     */
    public long random(long value);

    /**
     * Returns true or false depending on if the given value is a number or not.
     *
     * @param value The given object to check.
     * @return A boolean which is true if value is a number, otherwise false.
     */
    public boolean isNumber(MetaExpression value);
}
