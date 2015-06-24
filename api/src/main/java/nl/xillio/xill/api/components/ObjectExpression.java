package nl.xillio.xill.api.components;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import nl.xillio.xill.api.Debugger;
import nl.xillio.xill.api.errors.RobotRuntimeException;

/**
 * This class represents a written object in a script e.g. { "keyValue": 6 }
 */
public class ObjectExpression implements Processable {

	private final Map<Processable, Processable> value;

	/**
	 * @param object
	 */
	public ObjectExpression(final Map<Processable, Processable> object) {
		value = object;
	}

	@Override
	public InstructionFlow<MetaExpression> process(final Debugger debugger) throws RobotRuntimeException {
		Map<String, MetaExpression> result = new HashMap<String, MetaExpression>();

		for (Entry<Processable, Processable> entry : value.entrySet()) {
			try {

				result.put(entry.getKey().process(debugger).get().getStringValue(),
					entry.getValue().process(debugger).get());

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
			public void close() throws Exception {
				for (Expression expression : result.values()) {
					expression.close();
				}
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
