package nl.xillio.xill.plugins.string.constructs;

import com.google.inject.Inject;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.plugins.string.services.string.StringUtilityService;

/**
 * Makes the provided text upper case.
 *
 * @author Sander
 */
public class ToUpperConstruct extends Construct {
    @Inject
    StringUtilityService stringService;

    @Override
    public ConstructProcessor prepareProcess(final ConstructContext context) {
        return new ConstructProcessor(
                string -> process(string, stringService),
                new Argument("string", ATOMIC));
    }

    static MetaExpression process(final MetaExpression string, final StringUtilityService stringService) {

        return string.isNull() ? NULL : fromValue(stringService.toUpperCase(string.getStringValue()));
    }
}
