package nl.xillio.xill.components.operators;

import nl.xillio.xill.api.components.Processable;

/**
 * This class represents the %lt;= operator
 */
public final class SmallerThanOrEquals extends BinaryNumberComparisonOperator {

    public SmallerThanOrEquals(Processable left, Processable right) {
        super(left, right);
    }

    @Override
    protected boolean translate(int comparisonResult) {
        return comparisonResult <= 0;
    }

}
