package nl.xillio.xill.components.expressions;

import java.util.Arrays;
import java.util.Collection;

import nl.xillio.xill.api.Debugger;
import nl.xillio.xill.api.components.InstructionFlow;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.components.Processable;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.components.Robot;

/**
 * This class represents calling another robot
 */
public class GetArgumentExpression implements Processable {

	private final Robot robot;

	/**
	 * Create a new {@link GetArgumentExpression}
	 *
	 * @param robot
	 *
	 */
	public GetArgumentExpression(final Robot robot) {
		this.robot = robot;
	}

	@Override
	public InstructionFlow<MetaExpression> process(final Debugger debugger) throws RobotRuntimeException {
		return InstructionFlow.doResume(robot.getArgument());
	}

	@Override
	public Collection<Processable> getChildren() {
		return Arrays.asList();
	}
}
