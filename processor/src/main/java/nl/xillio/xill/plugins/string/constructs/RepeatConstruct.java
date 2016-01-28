package nl.xillio.xill.plugins.string.constructs;

import com.google.inject.Inject;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.plugins.string.services.string.StringUtilityService;

/**
 * Repeats the string the provided number of times.
 *
 * @author Sander
 */
public class RepeatConstruct extends Construct {
    @Inject
    private StringUtilityService stringService;

    @Override
    public ConstructProcessor prepareProcess(final ConstructContext context) {
        return new ConstructProcessor(
                (string, count) -> process(string, count, stringService),
                new Argument("string", ATOMIC),
                new Argument("count", ATOMIC));
    }

    static MetaExpression process(final MetaExpression string, final MetaExpression value, final StringUtilityService stringService) {
        assertNotNull(string, "string");
        assertNotNull(value, "value");

        String repeatedString = stringService.repeat(string.getStringValue(), value.getNumberValue().intValue());

        return fromValue(repeatedString);
    }
}
