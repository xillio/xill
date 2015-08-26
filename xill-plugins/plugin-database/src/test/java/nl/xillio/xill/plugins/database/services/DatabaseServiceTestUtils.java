package nl.xillio.xill.plugins.database.services;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.notNull;
import static org.mockito.Mockito.doReturn;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Utility methods for service testing
 * 
 * @author Geert Konijnendijk
 *
 */
public class DatabaseServiceTestUtils {

	/**
	 * Stubb methods from the {@link BaseDatabaseService} using Mockito
	 * 
	 * @param spyService
	 *        Spy of the service
	 * @param con
	 *        Connection to let {@link BaseDatabaseService#connect(String)} and {@link BaseDatabaseService#connect(String, java.util.Properties)} return
	 * @param connectionURL
	 *        URL to let {@link BaseDatabaseService#createConnectionURL(String, String, String, nl.xillio.xill.plugins.database.util.Tuple...)} return
	 * @throws SQLException
	 */
	public static void baseDatabaseServiceStubs(BaseDatabaseService spyService, Connection con, String connectionURL) throws SQLException {
		doReturn(con).when(spyService).connect(any());
		doReturn(con).when(spyService).connect(any(), any());
		doReturn(connectionURL).when(spyService).createConnectionURL(notNull(String.class), any(), any());
		doReturn(connectionURL).when(spyService).createConnectionURL(notNull(String.class), any(), any(), any());
	}

}
