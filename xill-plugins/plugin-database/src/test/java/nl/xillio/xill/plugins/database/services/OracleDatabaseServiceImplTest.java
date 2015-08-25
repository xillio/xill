package nl.xillio.xill.plugins.database.services;

import static nl.xillio.xill.plugins.database.services.DatabaseServiceTestUtils.baseDatabaseServiceStubs;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.notNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertSame;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import nl.xillio.xill.plugins.database.util.Tuple;
import oracle.jdbc.driver.OracleConnection;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * Tests the Oracle specific database service
 * 
 * @author Geert Konijnendijk
 *
 */
public class OracleDatabaseServiceImplTest {

	private OracleDatabaseServiceImpl service;

	@BeforeClass
	public void setup() {
		service = new OracleDatabaseServiceImpl();
	}

	/**
	 * Test the createConnection method
	 * 
	 * @throws SQLException
	 */
	@Test
	public void testCreateConnection() throws SQLException {
		// Mock
		Connection con = mock(OracleConnection.class);
		String connectionURL = "URL";
		Properties properties = new Properties();
		String user = "user", pass = "pass";
		Tuple<String, String>[] options = new Tuple[] {new Tuple<String, String>("key", "value")};
		OracleDatabaseServiceImpl spyService = spy(service);
		baseDatabaseServiceStubs(spyService, con, connectionURL);
		doReturn(properties).when(spyService).createProperties(any());

		// Run
		Connection returnedCon = spyService.createConnection("db", user, pass, options);

		// Verify
		verify(spyService).createConnectionURL(notNull(String.class), eq(user), eq(pass), eq(options[0]));
		verify(spyService).createProperties(options);
		verify(spyService).connect(connectionURL, properties);

		// Assert
		assertSame(returnedCon, con, "Different connection was returned");
	}

	/**
	 * Test the createConnectionURL method with a password but without a user
	 * 
	 * @throws SQLException
	 */
	@Test(expectedExceptions = IllegalArgumentException.class, expectedExceptionsMessageRegExp = "User and pass should be both null or both non-null")
	public void testCreateConnectionURLHalfLogin() throws SQLException {
		// Mock

		// Run
		String URL = service.createConnectionURL("db", null, "pass");

		// Verify

		// Assert
	}

	/**
	 * Test the createConnectionURL method with login
	 * 
	 * @throws SQLException
	 */
	@Test
	public void testCreateConnectionURLLogin() throws SQLException {
		// Mock

		// Run
		String URL = service.createConnectionURL("db", "user", "pass");

		// Verify

		// Assert
		assertEquals(URL, "jdbc:oracle:thin:user/pass@db", "Incorrect URL created");
	}

	/**
	 * Test the createConnectionURL method without login
	 * 
	 * @throws SQLException
	 */
	public void testCreateConnectionURLNoLogin() throws SQLException {
		// Mock

		// Run
		String URL = service.createConnectionURL("db", null, null);

		// Verify

		// Assert
		assertEquals(URL, "jdbc:oracle:thin:db", "Incorrect URL created");
	}

	// TODO: createProperties and createSelectQuery

}
