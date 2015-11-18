package nl.xillio.xill.plugins.document.data;

import nl.xillio.udm.builders.DocumentBuilder;
import nl.xillio.xill.api.components.MetaExpression;

/**
 * This class is responsible for mapping an expression to a builder.
 *
 * @author Thomas Biesaart
 */
public class MetaExpressionUDMMapper implements UDMDocument {
    private final MetaExpression expression;

    public MetaExpressionUDMMapper(MetaExpression expression) {
        this.expression = expression;
    }

    @Override
    public void applyTo(DocumentBuilder builder) {

    }
}
