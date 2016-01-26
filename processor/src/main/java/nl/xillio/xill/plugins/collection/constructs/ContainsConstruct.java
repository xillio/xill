package nl.xillio.xill.plugins.collection.constructs;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.api.errors.NotImplementedException;
import nl.xillio.xill.api.errors.RobotRuntimeException;

import java.util.List;
import java.util.Map;

/**
 * Returns true if the value is contained in the given list or object otherwise false.
 *
 * @author Sander Visser
 */
public class ContainsConstruct extends Construct {

    @Override
    public ConstructProcessor prepareProcess(final ConstructContext context) {
        return new ConstructProcessor(
                (input, value) -> process(input, value),
                new Argument("collection", LIST, OBJECT),
                new Argument("value", ATOMIC));
    }

    /**
     * Returns true if the value is contained in the given list or object. Otherwise false.
     *
     * @param input the list or object.
     * @param value the value that is going to be checked.
     * @return true if the list or object contains the value.
     */
    static MetaExpression process(final MetaExpression input, final MetaExpression value) {
        boolean result;
        switch (input.getType()) {
            case OBJECT:
                @SuppressWarnings("unchecked")
                Map<String, MetaExpression> m = (Map<String, MetaExpression>) input.getValue();
                try {
                    result = m.containsValue(value);
                } catch (ClassCastException | NullPointerException e) {
                    throw new RobotRuntimeException("The value handed was no valid MetaExpression", e);
                }
                break;
            case LIST:
                @SuppressWarnings("unchecked")
                List<MetaExpression> l = (List<MetaExpression>) input.getValue();
                try {
                    result = l.contains(value);
                } catch (ClassCastException | NullPointerException e) {
                    throw new RobotRuntimeException("The value handed was no valid MetaExpression", e);
                }
                break;
            default:
                throw new NotImplementedException("This type is not implemented.");

        }

        return fromValue(result);
    }
}
