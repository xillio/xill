package nl.xillio.xill.components;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import com.google.common.collect.Lists;

import nl.xillio.xill.api.Debugger;
import nl.xillio.xill.api.components.ExpressionBuilder;
import nl.xillio.xill.api.components.Instruction;
import nl.xillio.xill.api.components.InstructionFlow;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.components.Processable;
import nl.xillio.xill.api.components.RobotID;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.components.instructions.InstructionSet;

/**
 * This class represents the root node of the program structure
 */
public class Robot extends InstructionSet implements nl.xillio.xill.api.components.Robot {
    private final RobotID robotID;
    private final List<nl.xillio.xill.api.components.Robot> libraries = new ArrayList<>();
    private MetaExpression callArgument = ExpressionBuilder.NULL;
    private final static List<nl.xillio.xill.api.components.Robot> initializingRobots = new ArrayList<>();
    private final static List<nl.xillio.xill.api.components.Robot> closingRobots = new ArrayList<>();

    /**
     * @param robotID
     * @param debugger
     */
    public Robot(final RobotID robotID, final Debugger debugger) {
	super(debugger);
	this.robotID = robotID;
    }

    /**
     * Process this robot if necessary
     *
     * @return the result
     */
    @Override
    public InstructionFlow<MetaExpression> process(final Debugger debugger) throws RobotRuntimeException {
	getDebugger().robotStarted(this);

	initializingRobots.add(this);
	for (nl.xillio.xill.api.components.Robot robot : libraries) {
	    robot.initialize();
	}
	initializingRobots.remove(this);

	InstructionFlow<MetaExpression> result = super.process(debugger);

	getDebugger().robotFinished(this);

	return result;
    }

    /**
     * @return the robotID
     */
    public RobotID getRobotID() {
	return robotID;
    }

    @Override
    public Collection<Processable> getChildren() {
	List<Processable> children = new ArrayList<>(super.getChildren());

	children.addAll(libraries);

	return children;
    }

    @Override
    public void initialize() throws RobotRuntimeException {
	for (nl.xillio.xill.api.components.Robot robot : libraries) {
	    if (!initializingRobots.contains(robot)) {
		initializingRobots.add(robot);
		robot.initialize();
		initializingRobots.remove(robot);
	    }
	}

	super.initialize();
    }

    /**
     * Use a BFS algorithm to find a target among the children
     * 
     * @param target
     * @return the path to the target or an empty list if the target wasn't
     *         found.
     */
    @Override
    public List<Processable> pathToInstruction(final Instruction target) {
	Queue<Processable> fringe = new LinkedList<>();
	Set<Processable> visited = new HashSet<>();
	Map<Processable, Processable> parents = new HashMap<>();

	visited.add(this);
	fringe.add(this);
	while (!fringe.isEmpty()) {
	    Processable currentItem = fringe.poll();
	    visited.add(currentItem);

	    if (currentItem == target) {
		// Found the target, let's make the list
		List<Processable> result = new ArrayList<>();

		while (currentItem != null) {
		    result.add(currentItem);
		    currentItem = parents.get(currentItem);
		}

		return Lists.reverse(result);
	    }

	    Processable parent = currentItem;
	    // Seach children
	    currentItem.getChildren().stream().filter(child -> !visited.contains(child)).forEach(child -> {
		visited.add(child);
		fringe.add(child);
		parents.put(child, parent);
	    });
	}

	return new ArrayList<>();
    }

    @Override
    public void close() throws Exception {
	super.close();

	if(closingRobots.contains(this)) {
	    return;
	}
	
	closingRobots.add(this);
	// Close all external robots
	for (nl.xillio.xill.api.components.Robot robot : libraries) {
	    try {
		robot.close();
	    } catch (Exception e) {
		e.printStackTrace();
	    }
	}
	
	closingRobots.remove(this);
    }

    /**
     * Add a library to this robot
     * 
     * @param lib
     */
    public void addLibrary(final Robot lib) {
	libraries.add(lib);
    }

    @Override
    public void setArgument(final MetaExpression expression) {
	callArgument = expression;
    }

    @Override
    public MetaExpression getArgument() {
	return callArgument;
    }
}
