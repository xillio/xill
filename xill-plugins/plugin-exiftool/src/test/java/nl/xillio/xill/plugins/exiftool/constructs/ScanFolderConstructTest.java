package nl.xillio.xill.plugins.exiftool.constructs;

import nl.xillio.events.EventHost;
import nl.xillio.exiftool.ProcessPool;
import nl.xillio.xill.TestUtils;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.components.MetaExpressionIterator;
import nl.xillio.xill.api.components.RobotID;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.api.events.RobotStoppedAction;
import nl.xillio.xill.plugins.exiftool.services.OptionsFactory;
import nl.xillio.xill.plugins.exiftool.services.ProjectionFactory;
import org.testng.annotations.Test;

import java.io.File;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.testng.Assert.assertTrue;

/**
 * Tests whether the resulting MetaExpression has an iterator and whether the processPool is cleaned when stopping a robot
 *
 * @author Pieter Dirk Soels
 */
public class ScanFolderConstructTest extends TestUtils {

    @Test
    public void testPrepareProcess() throws Exception {
        // Mock
        ProcessPool pool = mock(ProcessPool.class, RETURNS_DEEP_STUBS);

        // Initialize
        EventHost<RobotStoppedAction> event = new EventHost<>();
        ScanFolderConstruct construct = new ScanFolderConstruct(pool, mock(ProjectionFactory.class, RETURNS_DEEP_STUBS), mock(OptionsFactory.class, RETURNS_DEEP_STUBS));
        ConstructContext constructContext = new ConstructContext(RobotID.dummyRobot(), RobotID.dummyRobot(), construct, null, UUID.randomUUID(), null, event);
        File file = new File(".");
        setFileResolverReturnValue(file);

        // Run
        ConstructProcessor processor = construct.prepareProcess(constructContext);
        MetaExpression result = ConstructProcessor.process(processor, fromValue("."));

        // Assert
        assertTrue(result.hasMeta(MetaExpressionIterator.class));

        // Verify
        event.invoke(new RobotStoppedAction(null, null));
        verify(pool, times(1)).clean();
    }
}