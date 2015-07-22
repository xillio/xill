package nl.xillio.xill.api.components;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import nl.xillio.xill.api.Debugger;
import nl.xillio.xill.api.errors.RobotRuntimeException;

/**
 * This class represents a written list in a script e.g. [1,2,3,4].<br/>
 * Values:
 * <ul>
 * <li><b>{@link String}: </b> the JSON representation</li>
 * <li><b>{@link Boolean}: </b> false if the list is null else true (even if empty)</li>
 * <li><b>{@link Number}: </b> the length of the list</li>
 * </ul>
 */
public class ListExpression implements Processable {
	private static int hashCounter = 0;

	private final List<? extends Processable> value;

	private final int id;

	/**
	 * @param value
	 *        the value to set
	 */
	public ListExpression(final List<? extends Processable> value) {
		this.value = value;
		this.id = hashCounter++;
	}

	@Override
	public InstructionFlow<MetaExpression> process(final Debugger debugger) throws RobotRuntimeException {
		List<MetaExpression> result = new ArrayList<>();

		for (Processable process : value) {
			try {
				MetaExpression child = process.process(debugger).get();
				child.registerReference();
				result.add(child);

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
			
			@Override
			public int hashCode() {
				return id;
			}
		};
		list.setValue(result);

		return InstructionFlow.doResume(list);
	}

	@Override
	public Collection<Processable> getChildren() {
		return new ArrayList<>(value);
	}
}
 