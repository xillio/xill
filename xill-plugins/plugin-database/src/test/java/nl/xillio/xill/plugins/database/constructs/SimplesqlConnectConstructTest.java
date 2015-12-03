package nl.xillio.xill.plugins.database.constructs;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.LinkedHashMap;

import nl.xillio.xill.TestUtils;
import nl.xillio.xill.api.construct.ExpressionBuilderHelper;
import org.testng.Assert;
import org.testng.annotations.Test;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.components.RobotID;
import nl.xillio.xill.plugins.database.services.DatabaseService;
import nl.xillio.xill.plugins.database.services.DatabaseServiceFactory;

/**
 * Test the {@link SimplesqlConnectConstruct}.
 *
 */
public class SimplesqlConnectConstructTest extends TestUtils {

	/**
	 * <p>
	 * Test the process with normal usage
	 * </p>
	 * <p>
	 * Note that we also run connect
	 * </p>
	 * 
	 * @throws ReflectiveOperationException
	 */
	@Test
	public void testProcessNormalUsage() throws ReflectiveOperationException {
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
				mockExpression(ATOMIC, false, 0, "user"),
				mockExpression(ATOMIC, false, 0, "pass"),
				options
		};
		// run
		MetaExpression output = SimplesqlConnectConstruct.process(args, "database", factory, robotID);

		// assert
		Assert.assertEquals(output.getStringValue(), "host:1531/database");
	}

}
