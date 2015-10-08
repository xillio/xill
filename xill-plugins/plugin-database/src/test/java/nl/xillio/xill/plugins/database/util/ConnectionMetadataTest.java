package nl.xillio.xill.plugins.database.util;

import org.testng.annotations.Test;
import java.sql.Connection;

import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

/**
 * Test the getters and setters of the {@link ConnectionMetadata}
 */
public class ConnectionMetadataTest {

	/**
	 * Test the getters and setters.
	 * @throws Exception 
	 */
	@Test
	public void testConnectionMetaData() throws Exception{
		Connection connection = mock(Connection.class);
		Connection connection2 = mock(Connection.class);
		String name = "databasename";
		
		// set and get values.
		ConnectionMetadata metadata = new ConnectionMetadata(name, connection);

		assertEquals(metadata.getConnection(), connection);
		assertEquals(metadata.getDatabaseName(), name);
		
		// we set and get again.
		metadata.setConnection(connection2);
		assertEquals(metadata.getConnection(), connection2);
	}
}
