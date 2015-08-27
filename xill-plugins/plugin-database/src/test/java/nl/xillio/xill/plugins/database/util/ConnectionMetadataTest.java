package nl.xillio.xill.plugins.database.util;

import org.testng.annotations.Test;
import java.sql.Connection;

import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

/**
 * @author Daan Knoope
 */
public class ConnectionMetadataTest {

	@Test
	public void testConnectionMetaData() throws Exception{
		Connection connection = mock(Connection.class);
		Connection connection2 = mock(Connection.class);
		String name = "databasename";
		ConnectionMetadata metadata = new ConnectionMetadata(name, connection);

		assertEquals(metadata.getConnection(), connection);
		assertEquals(metadata.getDatabaseName(), name);

		metadata.setConnection(connection2);
		assertEquals(metadata.getConnection(), connection2);
	}
}
