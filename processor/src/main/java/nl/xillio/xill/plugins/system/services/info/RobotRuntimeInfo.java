package nl.xillio.xill.plugins.system.services.info;

import com.google.common.util.concurrent.Service;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.services.PropertiesProvider;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * This class represents a {@link Service} that provides information about the currently running robot
 */
public class RobotRuntimeInfo implements PropertiesProvider {

    private final Map<String, Object> properties = new LinkedHashMap<>();
    private final String robotPath;
    private final String rootRobotPath;
    private final String projectPath;

    /**
     * Create a new {@link RobotRuntimeInfo} for a {@link ConstructContext}
     *
     * @param context the context
     */
    public RobotRuntimeInfo(final ConstructContext context) {
        robotPath = context.getRobotID().getPath().getAbsolutePath();
        rootRobotPath = context.getRootRobot().getPath().getAbsolutePath();
        projectPath = context.getRobotID().getProjectPath().getAbsolutePath();

        properties.put("robotPath", robotPath);
        properties.put("rootRobotPath", rootRobotPath);
        properties.put("projectPath", projectPath);
    }

    @Override
    public Map<String, Object> getProperties() {
        return properties;
    }

    /**
     * @return the robotPath
     */
    public String getRobotPath() {
        return robotPath;
    }

    /**
     * @return the rootRobotPath
     */
    public String getRootRobotPath() {
        return rootRobotPath;
    }

    /**
     * @return the projectPath
     */
    public String getProjectPath() {
        return projectPath;
    }

}
