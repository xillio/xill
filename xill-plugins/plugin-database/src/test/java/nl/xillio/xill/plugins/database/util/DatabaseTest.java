package nl.xillio.xill.plugins.database.util;

import nl.xillio.xill.plugins.database.services.BaseDatabaseService;
import nl.xillio.xill.plugins.database.services.MssqlDatabaseServiceImpl;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.testng.Assert.*;

/**
 * Unit tests for the Database enum
 * @author Daan Knoope
 */
public class DatabaseTest {

	/**
	 * Sets up a sample database and checks its properties
	 * @throws Exception
	 */
	@Test
	public void testDatabase() throws Exception{
		Database db = Database.MSSQL;
		assertEquals(db.getName(), "mssql");
		assertEquals(db.getDriverClass(), "net.sourceforge.jtds.jdbc.Driver");
		assertEquals(db.getServiceClass(), MssqlDatabaseServiceImpl.class);
	}

	/**
	 * Unit test to check if the correct database service is returned for each type
	 * @throws Exception
	 */
	@Test
	public void testFindServiceClass() throws Exception{
		List<String> names = Arrays.asList("oracle", "mssql", "sqlite", "mysql");
		List<Class<? extends BaseDatabaseService>> expectedResults = Arrays.asList(Database.ORACLE.getServiceClass(), Database.MSSQL.getServiceClass(), Database.SQLITE.getServiceClass(),
						Database.MYSQL.getServiceClass());
		List<Class<? extends  BaseDatabaseService>> returnedResults = names.stream().map(Database::findServiceClass).collect(Collectors.toList());
		assertEquals(returnedResults, expectedResults);
	}

	@Test (expectedExceptions = IllegalArgumentException.class, expectedExceptionsMessageRegExp = "DBMS type is not supported")
	public void testFindServiceClassUnsupportedType() throws Exception{
		Database.findServiceClass("googledatabase");
	}
}
