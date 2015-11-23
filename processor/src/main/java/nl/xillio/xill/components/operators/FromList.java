package nl.xillio.xill.components.operators;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import nl.xillio.xill.api.Debugger;
import nl.xillio.xill.api.components.InstructionFlow;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.components.Processable;
import nl.xillio.xill.api.construct.ExpressionBuilderHelper;
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
		MetaExpression listMeta = this.list.process(debugger).get();
		MetaExpression indexMeta = this.index.process(debugger).get();

		switch (listMeta.getType()) {
			case LIST:
				try {
					if(Double.isNaN(indexMeta.getNumberValue().doubleValue())){
						throw new RobotRuntimeException("The list does not contain any element called '" + indexMeta.getStringValue() + "' (a list does not have named elements).");
					}
					MetaExpression result = ((List<MetaExpression>) listMeta.getValue()).get(indexMeta.getNumberValue().intValue());
					return InstructionFlow.doResume(result);
				} catch (IndexOutOfBoundsException e) {
					return InstructionFlow.doResume(ExpressionBuilderHelper.NULL);
				}
			case OBJECT:
				MetaExpression result = ((Map<String, MetaExpression>) listMeta.getValue()).get(indexMeta.getStringValue());
				if (result == null) {
					return InstructionFlow.doResume(ExpressionBuilderHelper.NULL);
				}
				return InstructionFlow.doResume(result);
			case ATOMIC:
				throw new RobotRuntimeException("Cannot get member of ATOMIC value.");
			default:
				throw new NotImplementedException("This type has not been implemented.");
		}
	}

	@Override
	public Collection<Processable> getChildren() {
		return Arrays.asList(list, index);
	}

}
