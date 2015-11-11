package nl.xillio.xill.components.operators;

import nl.xillio.util.MathUtils;
import nl.xillio.xill.api.components.Processable;

/**
 * This class represents the - operator
 */
public class Subtract extends BinaryNumberOperator {

    public Subtract(final Processable left, final Processable right) {
        super(left, right, MathUtils::subtract);
    }

}
