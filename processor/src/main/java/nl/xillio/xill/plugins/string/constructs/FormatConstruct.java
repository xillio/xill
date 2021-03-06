package nl.xillio.xill.plugins.string.constructs;

import com.google.inject.Inject;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.api.errors.InvalidUserInputException;
import nl.xillio.xill.api.errors.OperationFailedException;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.string.exceptions.FailedToGetMatcherException;
import nl.xillio.xill.plugins.string.services.string.RegexService;
import nl.xillio.xill.plugins.string.services.string.StringUtilityService;

import java.util.ArrayList;
import java.util.IllegalFormatException;
import java.util.List;
import java.util.MissingFormatArgumentException;
import java.util.regex.Matcher;
import java.util.regex.PatternSyntaxException;

/**
 * <p>
 * Formats the string with the provided values.
 * </p>
 * <p>
 * Does not support Time/Date.
 * </p>
 *
 * @author Sander
 */
public class FormatConstruct extends Construct {
    @Inject
    private RegexService regexService;
    @Inject
    private StringUtilityService stringService;

    /**
     * Create a new {@link FormatConstruct}
     */
    public FormatConstruct() {
    }

    @Override
    public ConstructProcessor prepareProcess(final ConstructContext context) {
        return new ConstructProcessor(
                (textVar, valueVar) -> process(textVar, valueVar, regexService, stringService),
                new Argument("text", ATOMIC),
                new Argument("values", LIST));
    }

    static MetaExpression process(final MetaExpression textVar, final MetaExpression valueVar, final RegexService regexService, final StringUtilityService stringService) {
        assertNotNull(textVar, "text");

        List<MetaExpression> formatList = new ArrayList<>();
        List<Object> list = new ArrayList<>();
        @SuppressWarnings("unchecked")
        List<MetaExpression> numberList = (List<MetaExpression>) valueVar.getValue();

        try {
            Matcher matcher = regexService.getMatcher("%[[^a-zA-Z%]]*([a-zA-Z]|[%])", textVar.getStringValue(), RegexConstruct.REGEX_TIMEOUT);
            List<String> tryFormat = regexService.tryMatch(matcher);
            for (String s : tryFormat) {
                formatList.add(fromValue(s));
            }
        } catch (PatternSyntaxException e) {
            throw new RobotRuntimeException("SyntaxError in the system provided pattern: " + e.getMessage(), e);
        } catch (IllegalArgumentException | FailedToGetMatcherException e) {
            throw new RobotRuntimeException("Illegal argument handed when trying to match: " + e.getMessage(), e);
        }

        // Cast the MetaExpressions to the right type.
        int count = 0;
        String typeString;
        for (int j = 0; j < numberList.size() - count; j++) {
            if (j >= formatList.size()) {
                break;
            }
            typeString = formatList.get(j).getStringValue();
            switch (typeString.charAt(typeString.length() - 1)) {
                case 'd':
                case 'o':
                case 'x':
                case 'X':
                case 'h':
                case 'H':
                    list.add(numberList.get(j + count).getNumberValue().intValue());
                    break;
                case 'e':
                case 'E':
                case 'f':
                case 'g':
                case 'G':
                case 'a':
                case 'A':
                    list.add(numberList.get(j + count).getNumberValue().floatValue());
                    break;
                case 'c':
                case 'C':
                    list.add(numberList.get(j + count).getStringValue().charAt(0));
                    break;
                case 's':
                case 'S':
                    list.add(numberList.get(j + count).getStringValue());
                    break;
                case 'b':
                case 'B':
                    list.add(numberList.get(j + count).getBooleanValue());
                    break;
                case '%':
                    count--;
                    break;
                case 't':
                case 'T':
                    throw new OperationFailedException("format a date/time", "Date/Time conversions are not supported.", "Use Date package for formatting the date/time.");
                default:
                    throw new InvalidUserInputException("Unexpected conversion type.", typeString, "A supported conversion type.", "use String;\nString.format(\"%3$2s %1$2s %1$2s %2$2s\" , [\"a\", \"b\", \"c\"] );");
            }
        }
        try {
            return fromValue(stringService.format(textVar.getStringValue(), list));
        } catch (MissingFormatArgumentException e) {
            throw new InvalidUserInputException("Not enough arguments: " + e.getMessage(), valueVar.getStringValue(), "A correct list of arguments.", "use String;\nString.format(\"%3$2s %1$2s %1$2s %2$2s\" , [\"a\", \"b\", \"c\"] );", e);
        } catch (IllegalFormatException e) {
            throw new InvalidUserInputException("Illegal format handed: " + e.getMessage(), textVar.getStringValue(), "A valid format specifier.", "use String;\nString.format(\"%3$2s %1$2s %1$2s %2$2s\" , [\"a\", \"b\", \"c\"] );", e);
        }
    }
}
