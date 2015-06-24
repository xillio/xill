package nl.xillio.xill.components.operators;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import nl.xillio.xill.api.Debugger;
import nl.xillio.xill.api.components.InstructionFlow;
import nl.xillio.xill.api.components.Literal;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.components.Processable;
import nl.xillio.xill.api.errors.NotImplementedException;
import nl.xillio.xill.api.errors.RobotRuntimeException;

/**
 * this class represents the list[0] object["keyValue"] and object.keyValue operations.
 */
public class FromList implements Processable {

	private final Processable list;
	private final Processable index;

	/**
	 * @param list
	 * @param index
	 */
	public FromList(final Processable list, final Processable index) {
		this.list = list;
		this.index = index;
	}

	@SuppressWarnings("unchecked")
	@Override
	public InstructionFlow<MetaExpression> process(final Debugger debugger) throws RobotRuntimeException {
		MetaExpression list = this.list.process(debugger).get();
		MetaExpression index = this.index.process(debugger).get();

		switch (list.getType()) {
			case LIST:
				try {
					return InstructionFlow.doResume(((List<MetaExpression>) list.getValue()).get(index.getNumberValue().intValue()));
				} catch (IndexOutOfBoundsException e) {
					return InstructionFlow.doResume(Literal.NULL);
				}
			case OBJECT:
				MetaExpression result = ((Map<String, MetaExpression>) list.getValue()).get(index.getStringValue());
				if (result == null) {
					return InstructionFlow.doResume(Literal.NULL);
				}
				return InstructionFlow.doResume(result);
			default:
				throw new NotImplementedException("This type has not been implemented.");
		}
	}

	@Override
	public Collection<Processable> getChildren() {
		return Arrays.asList(list, index);
	}

}
