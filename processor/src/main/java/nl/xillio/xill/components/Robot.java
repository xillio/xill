package nl.xillio.xill.components;

import com.google.common.collect.Lists;
import me.biesaart.utils.Log;
import nl.xillio.events.EventHost;
import nl.xillio.xill.api.Debugger;
import nl.xillio.xill.api.components.*;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.api.events.RobotStartedAction;
import nl.xillio.xill.api.events.RobotStoppedAction;
import nl.xillio.xill.components.instructions.FunctionDeclaration;
import nl.xillio.xill.components.instructions.InstructionSet;
import nl.xillio.xill.components.instructions.VariableDeclaration;
import org.slf4j.Logger;

import java.util.*;

/**
 * This class represents the root node of the program structure
 */
public class Robot extends InstructionSet implements nl.xillio.xill.api.components.Robot {
    private final RobotID robotID;
    private final List<Robot> libraries = new ArrayList<>();
    private MetaExpression callArgument;

    private List<Instruction> libraryProcessedInstructions = new ArrayList<>();

    private static final Logger LOGGER = Log.get();

    /**
     * Events for signalling that a robot has started or stopped
     */
    private EventHost<RobotStartedAction> startEvent;
    private EventHost<RobotStoppedAction> endEvent;

    private final UUID compilerSerialId;

    /**
     * Create a {@link Robot}-object.
     *
     * @param robotID          The ID of the robot.
     * @param debugger         The processor associated with the code in this robot.
     * @param startEvent       The event indicating the start of the execution of a robot.
     * @param endEvent         The event indicating the halting of a robot.
     * @param compilerSerialId Serial ID of the compiler.
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

        // Initialize all libraries and their children
        for (Robot robot : getReferencedLibraries()) {
            // Skip initialization of the root robot in case of circular references
            if (robot != this) {
                robot.initializeAsLibrary();
            }
        }

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
     * @param target The item to be found.
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

        // Close all libraries and their children
        for (Robot robot : getReferencedLibraries()) {
            try {
                robot.closeLibrary();
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
    }

    /**
     * Add a library to this robot
     *
     * @param lib The library to be added.
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
        for (nl.xillio.xill.components.instructions.Instruction instruction : getInstructions()) {
            if (instruction instanceof VariableDeclaration || instruction instanceof FunctionDeclaration) {
                if (!getDebugger().shouldStop()) {
                    instruction.process(getDebugger());
                    libraryProcessedInstructions.add(instruction);
                }
            }
        }
    }

    /**
     * Close variables and functions in an initialized library
     */
    public void closeLibrary() {
        for (Instruction instruction : libraryProcessedInstructions) {
            try {
                instruction.close();
            } catch (Exception e) {
                LOGGER.error("Could not close instruction in a library", e);
            }
        }
    }

    /**
     * Construct a set of all robots referenced by this robot using Depth First Search.
     *
     * @return A set of all referenced robots
     */
    private Set<Robot> getReferencedLibraries() {
        Set<Robot> referencedLibraries = new HashSet<>();
        walkLibraries(referencedLibraries);
        return referencedLibraries;
    }

    /**
     * One step in a Depth First Search of included robots. Should only be called by {@link Robot#getReferencedLibraries()}.
     *
     * @param referencedLibraries The set to add libraries to
     */
    private void walkLibraries(Set<Robot> referencedLibraries) {
        for (Robot library : libraries) {
            // Don't continue down this branch if the library was already added
            if (referencedLibraries.add(library)) {
                library.walkLibraries(referencedLibraries);
            }
        }
    }
}
