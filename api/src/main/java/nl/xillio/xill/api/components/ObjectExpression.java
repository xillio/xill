package nl.xillio.xill.api.components;

import nl.xillio.xill.api.Debugger;
import nl.xillio.xill.api.errors.RobotRuntimeException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * <p>
 * This class represents a written object in a script e.g. { "keyValue": 6 }.
 * </p>
 * <p>
 * Values:
 * <ul>
 * <li><b>{@link String}: </b> a JSON representation</li>
 * <li><b>{@link Boolean}: </b> false if the object is null else true</li>
 * <li><b>{@link Number}: </b> the number of members in this object</li>
 * </ul>
 */
public class ObjectExpression extends MetaExpression {

    private final LinkedHashMap<String, MetaExpression> value;

    /**
     * @param object the value to set
     */
    public ObjectExpression(final LinkedHashMap<String, MetaExpression> object) {
        value = object;

        setValue(value);
        object.values().forEach(MetaExpression::registerReference);
    }

    @Override
    public InstructionFlow<MetaExpression> process(final Debugger debugger) throws RobotRuntimeException {
        return InstructionFlow.doResume(this);
    }

    @Override
    public Collection<Processable> getChildren() {
        List<Processable> children = new ArrayList<>(value.values());

        return children;
    }

    @Override
    public Number getNumberValue() {
        return value.size();
    }

    @Override
    public String getStringValue() {
        return toString();
    }

    @Override
    public boolean getBooleanValue() {
        return true;
    }

    @Override
    public boolean isNull() {
        return false;
    }
}
