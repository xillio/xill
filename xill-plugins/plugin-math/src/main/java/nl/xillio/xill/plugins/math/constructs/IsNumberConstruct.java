package nl.xillio.xill.plugins.math.constructs;

import com.google.inject.Inject;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.plugins.math.services.math.MathOperations;
import nl.xillio.xill.plugins.math.services.math.MathOperationsImpl;

/**
 * This construct is to check whether the given MetaExpression is a number or not.
 *
 * Created by Pieter Soels on 3/12/2015.
 */
public class IsNumberConstruct extends Construct {

    @Inject
    private MathOperationsImpl mathService;

    @Override
    public ConstructProcessor prepareProcess(final ConstructContext context) {
        return new ConstructProcessor(
                value -> process(value, mathService),
                new Argument("value"));
    }

    static MetaExpression process(final MetaExpression value, final MathOperations math) {
        return fromValue(math.isNumber(value));
    }
}
