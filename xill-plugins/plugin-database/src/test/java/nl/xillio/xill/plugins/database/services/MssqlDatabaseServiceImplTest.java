package nl.xillio.xill.plugins.database.services;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * <p>
 * Test the {@link MssqlDatabaseServiceImpl}.
 * </p>
 * <p>
 * We only test the createSelectQuery method
 * </p>
 */
public class MssqlDatabaseServiceImplTest {

	/**
	 * Test whether the create select query returns a currect query
	 */
	@Test
	public void testCreateSelectQuery() {
		MssqlDatabaseServiceImpl service = new MssqlDatabaseServiceImpl();
		String output = service.createSelectQuery("TABLE", "rownum > 2");

		Assert.assertEquals(output, "SELECT TOP 1 * FROM TABLE WHERE rownum > 2");

	}

}
