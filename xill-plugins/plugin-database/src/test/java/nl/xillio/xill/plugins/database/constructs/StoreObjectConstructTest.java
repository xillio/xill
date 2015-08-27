package nl.xillio.xill.plugins.database.constructs;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.mysql.jdbc.Connection;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

import java.awt.List;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.components.RobotID;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.database.BaseDatabaseConstruct;
import nl.xillio.xill.plugins.database.services.DatabaseService;
import nl.xillio.xill.plugins.database.services.DatabaseServiceFactory;
import nl.xillio.xill.plugins.database.util.ConnectionMetadata;
import nl.xillio.xill.testutils.ConstructTest;

public class StoreObjectConstructTest extends ConstructTest{

	@Test (expectedExceptions = RobotRuntimeException.class)
	public void testProcessDatabaseNull() throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		//mock
		MetaExpression table = mockExpression(ATOMIC, true, 0, "table");
		MetaExpression object = fromValue(new LinkedHashMap<>());
		MetaExpression keys = fromValue(new ArrayList<MetaExpression>());
		MetaExpression overwrite = mockExpression(ATOMIC,true,0,"empty");
		MetaExpression database = mockExpression(ATOMIC,true,0,"databaseName");
		MetaExpression[] args = {table,object,keys,overwrite,database};
		Connection connection = spy(Connection.class);
		DatabaseServiceFactory factory = mock(DatabaseServiceFactory.class);
		RobotID id = mock(RobotID.class);
		
		when(database.equals(NULL)).thenReturn(true);
		
	  ConnectionMetadata conMetadata = mock(ConnectionMetadata.class);
    when((conMetadata).getDatabaseName()).thenReturn("databaseName");
    
	  BaseDatabaseConstruct.setLastConnections(id,conMetadata);
	  
	  DatabaseService dbService = mock(DatabaseService.class);
    when((factory).getService(any())).thenReturn(dbService);
     
	  doThrow(RobotRuntimeException.class).when(dbService).storeObject(null, "table", (LinkedHashMap<String, Object>) extractValue(object),(java.util.List<String>) keys.getValue(), true);
	  
		//run
		MetaExpression result = StoreObjectConstruct.process(args, factory, id);
		
		
		//verify
    verify(conMetadata,times(1)).getConnection();
		verify(dbService,times(1)).storeObject(connection, "table", (LinkedHashMap<String, Object>) extractValue(object),(java.util.List<String>) keys.getValue(), true);
		
		
		
		//assert
	   Assert.assertEquals(result, NULL);
	}
}
