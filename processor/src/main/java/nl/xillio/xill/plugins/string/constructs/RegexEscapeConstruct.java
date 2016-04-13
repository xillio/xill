package nl.xillio.xill.plugins.string.constructs;

import com.google.inject.Inject;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.plugins.string.services.string.RegexService;
import nl.xillio.xill.plugins.string.services.string.StringUtilityService;

/**
 * Escapes a string so that it can be used as a literal in a regex
 *
 * @author Geert Konijnendijk
 */
public class RegexEscapeConstruct extends Construct {

    @Inject
    RegexService regexService;

    @Override
    public ConstructProcessor prepareProcess(ConstructContext context) {
        return new ConstructProcessor((toEscape) -> process(toEscape, regexService),
                new Argument("toEscape", ATOMIC));
    }

    static MetaExpression process(MetaExpression toEscape, RegexService service) {
        String escaped = service.escapeRegex(toEscape.getStringValue());
        return fromValue(escaped);
    }
}
