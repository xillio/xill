package nl.xillio.xill.plugins.concurrency.data;

import nl.xillio.xill.api.components.MetaExpression;

/**
 * This class represents the configuration of a single worker in the pipeline.
 *
 * @author Thomas Biesaart
 * @author Titus Nachbauer
 */
public class WorkerConfiguration {
    private String robot;
    private int threadCount = Runtime.getRuntime().availableProcessors();
    private int outputQueueSize = 100;
    private MetaExpression configuration;

    public String getRobot() {
        return robot;
    }

    public void setRobot(String robot) {
        this.robot = robot;
    }

    public int getThreadCount() {
        return threadCount;
    }

    public void setThreadCount(int threadCount) {
        this.threadCount = threadCount;
    }

    public int getOutputQueueSize() {
        return outputQueueSize;
    }

    public void setOutputQueueSize(int outputQueueSize) {
        this.outputQueueSize = outputQueueSize;
    }

    public MetaExpression getConfiguration() {
        return configuration;
    }

    public void setConfiguration(MetaExpression configuration) {
        this.configuration = configuration;
    }
}
