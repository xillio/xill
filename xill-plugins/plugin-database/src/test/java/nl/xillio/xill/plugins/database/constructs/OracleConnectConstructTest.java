package nl.xillio.xill.plugins.database.constructs;

import nl.xillio.xill.TestUtils;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.components.RobotID;
import nl.xillio.xill.plugins.database.services.DatabaseService;
import nl.xillio.xill.plugins.database.services.DatabaseServiceFactory;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.LinkedHashMap;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Test the {@link OracleConnectConstruct}.
 */
public class OracleConnectConstructTest extends TestUtils {
    /**
     * <p>
     * Test the process's normal usage whilst using SID.
     * </p>
     * <p>
     * Note that we also run connect
     * </p>
     *
     * @throws ReflectiveOperationException
     */
    @Test
    public void testProcessNormalUsageUseSID() throws ReflectiveOperationException {
        // mock

        // the factory
        DatabaseService service = mock(DatabaseService.class);
        DatabaseServiceFactory factory = mock(DatabaseServiceFactory.class);
        when(factory.getService(anyString())).thenReturn(service);

        RobotID robotID = mock(RobotID.class);

        MetaExpression options = mockExpression(ATOMIC);
        when(options.getValue()).thenReturn(new LinkedHashMap<>());

        MetaExpression[] args = {
                mockExpression(ATOMIC, false, 0, "database"),
                mockExpression(ATOMIC, false, 0, "host"),
                mockExpression(ATOMIC, false, 1531, "port"),
                mockExpression(ATOMIC, true, 0, "useSID"),
                mockExpression(ATOMIC, false, 0, "user"),
                mockExpression(ATOMIC, false, 0, "pass"),
                options
        };

        // run
        MetaExpression output = OracleConnectConstruct.process(args, factory, robotID);

        // assert
        Assert.assertEquals(output.getStringValue(), "host:1531:database");
    }

    /**
     * <p>
     * Test the process's normal usage when not using SID.
     * </p>
     * <p>
     * Note that we also run connect
     * </p>
     *
     * @throws ReflectiveOperationException
     */
    @Test
    public void testProcessNormalUsageNoSID() throws ReflectiveOperationException {
        // mock

        // the factory
        DatabaseService service = mock(DatabaseService.class);
        DatabaseServiceFactory factory = mock(DatabaseServiceFactory.class);
        when(factory.getService(anyString())).thenReturn(service);

        RobotID robotID = mock(RobotID.class);

        MetaExpression options = mockExpression(ATOMIC);
        when(options.getValue()).thenReturn(new LinkedHashMap<>());

        MetaExpression[] args = {
                mockExpression(ATOMIC, false, 0, "database"),
                mockExpression(ATOMIC, false, 0, "host"),
                mockExpression(ATOMIC, false, 1531, "port"),
                mockExpression(ATOMIC, false, 0, "useSID"),
                mockExpression(ATOMIC, false, 0, "user"),
                mockExpression(ATOMIC, false, 0, "pass"),
                options
        };

        // run
        MetaExpression output = OracleConnectConstruct.process(args, factory, robotID);

        // assert
        Assert.assertEquals(output.getStringValue(), "host:1531/database");
    }
}
