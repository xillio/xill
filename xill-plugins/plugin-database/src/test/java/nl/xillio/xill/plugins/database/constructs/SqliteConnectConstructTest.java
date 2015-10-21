package nl.xillio.xill.plugins.database.constructs;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.LinkedHashMap;

import org.testng.Assert;
import org.testng.annotations.Test;

import nl.xillio.xill.ConstructTest;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.components.RobotID;
import nl.xillio.xill.plugins.database.services.DatabaseService;
import nl.xillio.xill.plugins.database.services.DatabaseServiceFactory;

/**
 * Test the (@link SqliteConnectConstruct}.
 */
public class SqliteConnectConstructTest extends ConstructTest {

	/**
	 * <p>
	 * Test the process's normal usage
	 * </p>
	 * 
	 * @throws ReflectiveOperationException
	 * 
	 */
	@Test
	public void testProcessNormalUsage() throws ReflectiveOperationException {
		// mock

		// the factory
		DatabaseService service = mock(DatabaseService.class);
		DatabaseServiceFactory factory = mock(DatabaseServiceFactory.class);
		when(factory.getService(anyString())).thenReturn(service);


		MetaExpression options = mockExpression(ATOMIC);
		when(options.getValue()).thenReturn(new LinkedHashMap<>());

		MetaExpression file = mockExpression(ATOMIC, true, 5, ":memory:");
		// run
		MetaExpression output = SqliteConnectConstruct.process(file, factory);

		// assert
		Assert.assertEquals(output.getStringValue(), ":memory:");
	}

}
