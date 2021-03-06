package nl.xillio.xill.components.operators;

import nl.xillio.xill.api.components.Processable;

/**
 * This class represents the &gt;= operator
 */
public final class GreaterThanOrEquals extends BinaryNumberComparisonOperator {

    public GreaterThanOrEquals(Processable left, Processable right) {
        super(left, right);
    }

    @Override
    protected boolean translate(int comparisonResult) {
        return comparisonResult >= 0;
    }

}
