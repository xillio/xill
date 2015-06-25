package nl.xillio.xill.components.operators;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import nl.xillio.xill.api.Debugger;
import nl.xillio.xill.api.components.InstructionFlow;
import nl.xillio.xill.api.components.Literal;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.components.Processable;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.components.instructions.Instruction;
import nl.xillio.xill.components.instructions.VariableDeclaration;

import com.google.common.collect.Lists;

/**
 * This {@link Instruction} represents the assignment of an expression value to a variable
 */
public class Assign implements Processable {

	private final VariableDeclaration variableDeclaration;
	private final Processable expression;
	private final List<Processable> path;

	/**
	 * Create a new {@link Assign}
	 *
	 * @param variableDeclaration
	 *        The declaration to assign to
	 * @param path
	 * @param expression
	 *        The expression to assign
	 */
	public Assign(final VariableDeclaration variableDeclaration, final List<Processable> path, final Processable expression) {
		this.variableDeclaration = variableDeclaration;
		this.path = Lists.reverse(path);
		this.expression = expression;
	}

	@SuppressWarnings("unchecked")
	@Override
	public InstructionFlow<MetaExpression> process(final Debugger debugger) throws RobotRuntimeException {
		MetaExpression value = expression.process(debugger).get();

		// First we check if there is a path
		if (path.isEmpty()) {
			// Assign atomically
			variableDeclaration.setVariable(value);
		} else {

			// Seems like we have a path
			switch (variableDeclaration.getVariable().getType()) {
				case LIST:
					List<MetaExpression> listValue = (List<MetaExpression>) variableDeclaration.getVariable().getValue();
					assign(listValue, 0, value, debugger);
					break;
				case OBJECT:
					Map<String, MetaExpression> mapValue = (Map<String, MetaExpression>) variableDeclaration.getVariable().getValue();
					assign(mapValue, 0, value, debugger);
					break;
				default:
					throw new RobotRuntimeException("Cannot assign to atomic variable using a path.");

			}
		}

		return InstructionFlow.doResume(value);
	}

	@SuppressWarnings("unchecked")
	private void assign(final List<MetaExpression> target, final int pathID, final MetaExpression value, final Debugger debugger) throws RobotRuntimeException {

		int index = path.get(pathID).process(debugger).get().getNumberValue().intValue();

		if (path.size() - 1 == pathID) {
			// This is the value to write to
			if (target.size() > index) {
				// Change the value
				target.set(index, value);
			} else {
				// The list is too small
				while (target.size() < index) {
					target.add(Literal.NULL);
				}

				target.add(value);
			}

			return;
		}

		// We need to go deeper
		MetaExpression currentValue = target.get(index).process(debugger).get();

		switch (currentValue.getType()) {
			case LIST:
				assign((List<MetaExpression>) currentValue.getValue(), pathID + 1, value, debugger);
				break;
			case OBJECT:
				assign((Map<String, MetaExpression>) currentValue.getValue(), pathID + 1, value, debugger);
				break;
			default:
				throw new IllegalStateException("Can only assign to children of Object and List types.");
		}

	}

	@SuppressWarnings("unchecked")
	private void assign(final Map<String, MetaExpression> target, final int pathID, final MetaExpression value, final Debugger debugger) throws RobotRuntimeException {
		String index = path.get(pathID).process(debugger).get().getStringValue();

		if (path.size() - 1 == pathID) {
			target.put(index, value);
			return;
		}

		// We need to go deeper
		MetaExpression currentValue = target.get(index).process(debugger).get();

		switch (currentValue.getType()) {
			case LIST:
				assign((List<MetaExpression>) currentValue.getValue(), pathID + 1, value, debugger);
				break;
			case OBJECT:
				assign((Map<String, MetaExpression>) currentValue.getValue(), pathID + 1, value, debugger);
				break;
			default:
				throw new IllegalStateException("Can only assign to children of Object and List types.");
		}
	}

	@Override
	public Collection<Processable> getChildren() {
		// The variable declaration is not a child as it exists elsewhere

		List<Processable> children = new ArrayList<>(path);
		children.add(expression);

		return children;
	}

}
