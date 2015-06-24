package nl.xillio.xill.api.components;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import nl.xillio.xill.api.Debugger;
import nl.xillio.xill.api.errors.RobotRuntimeException;

/**
 * This class represents a written list in a script e.g. [1,2,3,4]
 */
public class ListExpression implements Processable {

	private final List<Processable> value;

	/**
	 * @param value
	 */
	public ListExpression(final List<Processable> value) {
		this.value = value;
	}

	@Override
	public InstructionFlow<MetaExpression> process(final Debugger debugger) throws RobotRuntimeException {
		List<MetaExpression> result = new ArrayList<>();

		for (Processable process : value) {
			try {

				result.add(process.process(debugger).get());

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
				for (Expression expression : result) {
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
		return new ArrayList<>(value);
	}
}
