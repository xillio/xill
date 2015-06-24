package nl.xillio.xill.components.operators;

import java.util.Arrays;
import java.util.Collection;

import nl.xillio.xill.api.Debugger;
import nl.xillio.xill.api.components.AtomicExpression;
import nl.xillio.xill.api.components.InstructionFlow;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.components.Processable;
import nl.xillio.xill.api.errors.RobotRuntimeException;

/**
 * This class represents the :: operator
 */
public class Concat implements Processable {

	private final Processable[] parts;

	/**
	 * @param parts
	 */
	public Concat(final Processable... parts) {
		this.parts = parts;
	}

	@Override
	public InstructionFlow<MetaExpression> process(final Debugger debugger) throws RobotRuntimeException {
		StringBuilder sb = new StringBuilder();

		for (Processable expression : parts) {
			sb.append(expression.process(debugger).get().getStringValue());
		}

		String result = sb.toString();

		return InstructionFlow.doResume(new AtomicExpression(result));
	}

	@Override
	public Collection<Processable> getChildren() {
		return Arrays.asList(parts);
	}

}
