package nl.xillio.xill.plugins.system.services.info;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import nl.xillio.xill.api.construct.ConstructContext;

/**
 * This is the default implementation of the {@link SystemInfoService}
 */
@Singleton
public class SystemInfoServiceImpl implements SystemInfoService {

    @Inject
    private FileSystemInfo fileSystem;

    @Inject
    private RuntimeInfo runtimeInfo;

    @Override
    public FileSystemInfo getFileSystemInfo() {
        return fileSystem;
    }

    @Override
    public RuntimeInfo getRuntimeInfo() {
        return runtimeInfo;
    }

    @Override
    public RobotRuntimeInfo getRobotRuntimeInfo(final ConstructContext context) {
        return new RobotRuntimeInfo(context);
    }

}
