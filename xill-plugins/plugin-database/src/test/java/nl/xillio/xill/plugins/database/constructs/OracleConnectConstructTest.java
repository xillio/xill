package nl.xillio.xill.plugins.database.constructs;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.LinkedHashMap;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.components.RobotID;
import nl.xillio.xill.plugins.database.services.DatabaseService;
import nl.xillio.xill.plugins.database.services.DatabaseServiceFactory;
import nl.xillio.xill.testutils.ConstructTest;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Test the {@link OracleConnectConstruct}.
 *
 */
public class OracleConnectConstructTest extends ConstructTest {
	/**
	 * <p>
	 * Test the process's normal usage whilst using SID.
	 * </p>
	 * <p>
	 * Note that we also run connect
	 * </p>
	 * 
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException
	 */
	@Test
	public void testProcessNormalUsageUseSID() throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		// mock

		// the factory
		DatabaseService service = mock(DatabaseService.class);
		DatabaseServiceFactory factory = mock(DatabaseServiceFactory.class);
		when(factory.getService(anyString())).thenReturn(service);

		RobotID robotID = mock(RobotID.class);

		MetaExpression options = mockExpression(ATOMIC);
		when(options.getValue()).thenReturn(new LinkedHashMap<>());

		MetaExpression[] args = {
				mockExpression(ATOMIC, false, 0, "host"),
				mockExpression(ATOMIC, false, 1531, "port"),
				mockExpression(ATOMIC, false, 0, "database"),
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
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException
	 */
	@Test
	public void testProcessNormalUsageNoSID() throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		// mock

		// the factory
		DatabaseService service = mock(DatabaseService.class);
		DatabaseServiceFactory factory = mock(DatabaseServiceFactory.class);
		when(factory.getService(anyString())).thenReturn(service);

		RobotID robotID = mock(RobotID.class);

		MetaExpression options = mockExpression(ATOMIC);
		when(options.getValue()).thenReturn(new LinkedHashMap<>());

		MetaExpression[] args = {
				mockExpression(ATOMIC, false, 0, "host"),
				mockExpression(ATOMIC, false, 1531, "port"),
				mockExpression(ATOMIC, false, 0, "database"),
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
