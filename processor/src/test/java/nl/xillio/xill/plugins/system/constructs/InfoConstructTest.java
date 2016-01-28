package nl.xillio.xill.plugins.system.constructs;

import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.plugins.system.services.info.FileSystemInfo;
import nl.xillio.xill.plugins.system.services.info.RobotRuntimeInfo;
import nl.xillio.xill.plugins.system.services.info.RuntimeInfo;
import nl.xillio.xill.plugins.system.services.info.SystemInfoService;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import static org.mockito.Mockito.*;

/**
 * Test the {@link InfoConstruct}
 */
public class InfoConstructTest {

    /**
     * Test the process method under normal conditions
     */
    @Test
    public void testProcess() {
        // Mock context
        FileSystemInfo fileInfo = mock(FileSystemInfo.class);
        when(fileInfo.getRootProperties()).thenReturn(new ArrayList<>());

        RuntimeInfo runInfo = mock(RuntimeInfo.class);
        when(runInfo.getProperties()).thenReturn(new LinkedHashMap<>());

        RobotRuntimeInfo robotInfo = mock(RobotRuntimeInfo.class);
        when(robotInfo.getProperties()).thenReturn(new LinkedHashMap<>());

        ConstructContext context = mock(ConstructContext.class);

        // Only return robot if the right context is passed
        SystemInfoService sysInfo = mock(SystemInfoService.class);
        when(sysInfo.getRobotRuntimeInfo(context)).thenReturn(robotInfo);
        when(sysInfo.getFileSystemInfo()).thenReturn(fileInfo);
        when(sysInfo.getRuntimeInfo()).thenReturn(runInfo);

        // Run the method
        InfoConstruct.process(context, sysInfo);

        // Verify service calls
        verify(sysInfo).getFileSystemInfo();
        verify(sysInfo).getRobotRuntimeInfo(context);
        verify(sysInfo).getRuntimeInfo();

        verify(fileInfo).getRootProperties();
        verify(robotInfo).getProperties();
        verify(runInfo).getProperties();

    }
}
