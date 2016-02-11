package nl.xillio.xill.components;

import com.google.common.collect.Lists;
import nl.xillio.events.EventHost;
import nl.xillio.xill.api.Debugger;
import nl.xillio.xill.api.components.*;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.api.events.RobotStartedAction;
import nl.xillio.xill.api.events.RobotStoppedAction;
import nl.xillio.xill.components.instructions.InstructionSet;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

/**
 * This class represents the root node of the program structure
 */
public class Robot extends InstructionSet implements nl.xillio.xill.api.components.Robot {
    private final RobotID robotID;
    private final List<nl.xillio.xill.api.components.Robot> libraries = new ArrayList<>();
    private MetaExpression callArgument;

    private static final Logger LOGGER = LogManager.getLogger();

    /**
     * Events for signalling that a robot has started or stopped
     */
    private EventHost<RobotStartedAction> startEvent;
    private EventHost<RobotStoppedAction> endEvent;

    private final static List<nl.xillio.xill.api.components.Robot> initializingRobots = new ArrayList<>();
    private final static List<nl.xillio.xill.api.components.Robot> closingRobots = new ArrayList<>();
    private final UUID compilerSerialId;

    /**
     * @param robotID
     * @param debugger
     * @param compilerSerialId
     */
    public Robot(final RobotID robotID, final Debugger debugger, EventHost<RobotStartedAction> startEvent, EventHost<RobotStoppedAction> endEvent, UUID compilerSerialId) {
        super(debugger);
        this.robotID = robotID;
        this.startEvent = startEvent;
        this.endEvent = endEvent;
        this.compilerSerialId = compilerSerialId;
    }

    /**
     * Process this robot if necessary
     *
     * @return the result
     */
    @Override
    public InstructionFlow<MetaExpression> process(final Debugger debugger) throws RobotRuntimeException {
        getDebugger().robotStarted(this);
        startEvent.invoke(new RobotStartedAction(this));

        initializingRobots.add(this);
        for (nl.xillio.xill.api.components.Robot robot : libraries) {
            robot.initializeAsLibrary();
        }
        initializingRobots.remove(this);

        InstructionFlow<MetaExpression> result = super.process(debugger);

        endEvent.invoke(new RobotStoppedAction(this, compilerSerialId));
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

    /**
     * Use a BFS algorithm to find a target among the children
     *
     * @param target
     * @return the path to the target or an empty list if the target wasn't
     * found.
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
    public void close() {
        super.close();

        if (closingRobots.contains(this)) {
            return;
        }

        closingRobots.add(this);
        // Close all external robots
        for (nl.xillio.xill.api.components.Robot robot : libraries) {
            try {
                robot.close();
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
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
        if (callArgument == null) {
            return ExpressionBuilderHelper.NULL;
        }
        return callArgument;
    }

    @Override
    public boolean hasArgument() {
        return callArgument != null;
    }

    @Override
    public UUID getCompilerSerialId() {
        return compilerSerialId;
    }

    @Override
    public void initializeAsLibrary() throws RobotRuntimeException {
        for (nl.xillio.xill.api.components.Robot robot : libraries) {
            if (!initializingRobots.contains(robot)) {
                initializingRobots.add(robot);
                robot.initializeAsLibrary();
                initializingRobots.remove(robot);
            }
        }
        super.initialize();
    }
}
