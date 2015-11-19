package nl.xillio.migrationtool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import nl.xillio.xill.api.Breakpoint;
import nl.xillio.xill.api.components.RobotID;

/**
 * This class is responsible for the administration of breakpoints
 */
public class BreakpointPool {
	private final Map<RobotID, List<Integer>> breakpoints = new HashMap<>();
	private static final Logger log = LogManager.getLogger(BreakpointPool.class);

	/**
	 * Get all breakpoints in a certain robot
	 * 
	 * @param robot
	 * @return a List of line numbers
	 */
	public List<Integer> get(final RobotID robot) {
		List<Integer> bps = breakpoints.get(robot);

		if (bps == null) {
			return new ArrayList<>();
		}

		return bps;
	}

	/**
	 * Add a breakpoint to the pool
	 * 
	 * @param robot
	 * @param line
	 */
	public void add(final RobotID robot, final int line) {
		List<Integer> bpList = breakpoints.get(robot);

		if (bpList == null) {
			bpList = new ArrayList<>();
			breakpoints.put(robot, bpList);
		}

		bpList.add(line);
	}

	/**
	 * @return A list of all breakpoints
	 */
	public List<Breakpoint> get() {
		List<Breakpoint> bpList = new ArrayList<>();

		breakpoints.entrySet().forEach(entry -> entry.getValue().forEach(line -> bpList.add(new Breakpoint(entry.getKey(), line))));

		return bpList;
	}

	/**
	 * Clear breakpoints in a certain robot
	 * 
	 * @param robot
	 */
	public void clear(final RobotID robot) {
		breakpoints.remove(robot);
	}

	/**
	 * The singleton instance of {@link BreakpointPool}
	 */
	public static final BreakpointPool INSTANCE = new BreakpointPool();

	private BreakpointPool() {}

	/**
	 * Remove all breakpoints
	 */
	public void clear() {
		breakpoints.clear();
	}
}
