package nl.xillio.xill.plugins.database.services;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Test the {@link DatabaseServiceFactory}.
 *
 */
public class DatabaseServiceFactoryTest {

	/**
	 * Test whether the getService can return an SQLite database.
	 * 
	 * @throws ReflectiveOperationException
	 */
	@Test
	public void testGetSQLiteService() throws ReflectiveOperationException {
		DatabaseServiceFactory factory = new DatabaseServiceFactory();

		DatabaseService service = factory.getService("sqlite");

		Assert.assertEquals(service.getClass(), SQLiteDatabaseServiceImpl.class);
	}

	/**
	 * Test whether the getService can return an MSsql database.
	 * 
	 * @throws ReflectiveOperationException
	 * 
	 */
	@Test
	public void testGetMssqlService() throws ReflectiveOperationException {
		DatabaseServiceFactory factory = new DatabaseServiceFactory();

		DatabaseService service = factory.getService("mssql");

		Assert.assertEquals(service.getClass(), MssqlDatabaseServiceImpl.class);
	}

	/**
	 * Test whether the getService can return an Mysql database.
	 * 
	 * @throws ReflectiveOperationException
	 * 
	 */
	@Test
	public void testGetMysqlService() throws ReflectiveOperationException {
		DatabaseServiceFactory factory = new DatabaseServiceFactory();

		DatabaseService service = factory.getService("mysql");

		Assert.assertEquals(service.getClass(), MysqlDatabaseServiceImpl.class);
	}

	/**
	 * Test whether the getService can return an Oracle database.
	 * 
	 * @throws ReflectiveOperationException
	 */
	@Test
	public void testGetOracleService() throws ReflectiveOperationException {
		DatabaseServiceFactory factory = new DatabaseServiceFactory();

		DatabaseService service = factory.getService("oracle");

		Assert.assertEquals(service.getClass(), OracleDatabaseServiceImpl.class);
	}

}
