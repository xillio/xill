package nl.xillio.xill.api.components;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import nl.xillio.xill.api.Debugger;
import nl.xillio.xill.api.errors.RobotRuntimeException;

/**
 * This class represents a written object in a script e.g. { "keyValue": 6 }.
 * <br/>
 * Values:
 * <ul>
 * <li><b>{@link String}: </b> a JSON representation</li>
 * <li><b>{@link Boolean}: </b> false if the object is null else true</li>
 * <li><b>{@link Number}: </b> the number of members in this object</li>
 * </ul>
 */
public class ObjectExpression implements Processable {

    private final Map<? extends Processable, ? extends Processable> value;

    /**
     * @param object
     */
    public ObjectExpression(final Map<? extends Processable, ? extends Processable> object) {
	value = object;
    }

    @Override
    public InstructionFlow<MetaExpression> process(final Debugger debugger) throws RobotRuntimeException {
	Map<String, MetaExpression> result = new LinkedHashMap<String, MetaExpression>();

	for (Entry<? extends Processable, ? extends Processable> entry : value.entrySet()) {
	    try {
		MetaExpression child = entry.getValue().process(debugger).get();
		child.registerReference();
		result.put(entry.getKey().process(debugger).get().getStringValue(), child);

	    } catch (RobotRuntimeException e) {
		debugger.handle(e);
	    }
	}

	MetaExpression list = new MetaExpression() {

	    @Override
	    public Number getNumberValue() {
		return result.size();
	    }

	    @Override
	    public String getStringValue() {
		return toString();
	    }

	    @Override
	    public boolean getBooleanValue() {
		return isNull();
	    }

	    @Override
	    public boolean isNull() {
		return false;
	    }

	    @Override
	    public Collection<Processable> getChildren() {
		return Arrays.asList();
	    }
	};
	list.setValue(result);

	return InstructionFlow.doResume(list);
    }

    @Override
    public Collection<Processable> getChildren() {
	List<Processable> children = new ArrayList<>(value.values());
	children.addAll(value.keySet());

	return children;
    }
}
