package nl.xillio.xill.components.operators;

import nl.xillio.util.MathUtils;
import nl.xillio.xill.api.components.Processable;

/**
 * This class represents the * operator
 */
public class Modulo extends BinaryNumberOperator {

    public Modulo(final Processable left, final Processable right) {
        super(left, right, MathUtils::modulo);
    }

}
