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
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException
	 */
	@Test
	public void testGetSQLiteService() throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		DatabaseServiceFactory factory = new DatabaseServiceFactory();

		DatabaseService service = factory.getService("sqlite");

		Assert.assertEquals(service.getClass(), SQLiteDatabaseServiceImpl.class);
	}

	/**
	 * Test whether the getService can return an MSsql database.
	 * 
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException
	 */
	@Test
	public void testGetMssqlService() throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		DatabaseServiceFactory factory = new DatabaseServiceFactory();

		DatabaseService service = factory.getService("mssql");

		Assert.assertEquals(service.getClass(), MssqlDatabaseServiceImpl.class);
	}

	/**
	 * Test whether the getService can return an Mysql database.
	 * 
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException
	 */
	@Test
	public void testGetMysqlService() throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		DatabaseServiceFactory factory = new DatabaseServiceFactory();

		DatabaseService service = factory.getService("mysql");

		Assert.assertEquals(service.getClass(), MysqlDatabaseServiceImpl.class);
	}

	/**
	 * Test whether the getService can return an Oracle database.
	 * 
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException
	 */
	@Test
	public void testGetOracleService() throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		DatabaseServiceFactory factory = new DatabaseServiceFactory();

		DatabaseService service = factory.getService("oracle");

		Assert.assertEquals(service.getClass(), OracleDatabaseServiceImpl.class);
	}

}
