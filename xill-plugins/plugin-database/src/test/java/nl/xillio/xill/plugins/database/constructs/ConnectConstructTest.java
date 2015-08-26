package nl.xillio.xill.plugins.database.constructs;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.mysql.jdbc.Connection;

import nl.xillio.xill.api.components.AtomicExpression;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.components.ObjectExpression;
import nl.xillio.xill.api.components.RobotID;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.database.BaseDatabaseConstruct;
import nl.xillio.xill.plugins.database.services.BaseDatabaseService;
import nl.xillio.xill.plugins.database.services.DatabaseService;
import nl.xillio.xill.plugins.database.services.DatabaseServiceFactory;
import nl.xillio.xill.plugins.database.util.ConnectionMetadata;
import nl.xillio.xill.plugins.database.util.Database;
import nl.xillio.xill.plugins.database.util.Tuple;
import nl.xillio.xill.testutils.ConstructTest;

import static org.mockito.Mockito.*;

import java.sql.SQLException;
import java.util.LinkedHashMap;

public class ConnectConstructTest extends ConstructTest{

  /**
   * test the method when all given input is valid. does not throw any exceptions
   */
  @Test
  public void testProcessNormal() throws SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException {
  	//mock
  	//mock all the input
   MetaExpression database = mockExpression(ATOMIC, true, 0, "databaseName");
   MetaExpression type = mockExpression(ATOMIC, true, 0, "databaseType");
   MetaExpression user = mockExpression(ATOMIC, true, 0, "user");
   MetaExpression pass = mockExpression(ATOMIC, true, 0, "pass");
   MetaExpression options = mockExpression(OBJECT);
   when(options.getValue()).thenReturn(new LinkedHashMap<>());
   
   MetaExpression[] args = {database,type,user,pass,options};
   DatabaseServiceFactory factory = mock(DatabaseServiceFactory.class);
   RobotID robotID = mock(RobotID.class);
   
   //mock the connection and service
   Connection connection = mock(Connection.class);
   DatabaseService dbService = mock(DatabaseService.class);
   
   
   when((dbService).createConnection(eq("databaseName"),eq("user"),eq("pass"),any())).thenReturn(connection);
   when((factory).getService("databaseType")).thenReturn(dbService);
	 ConnectionMetadata metadata = mock(ConnectionMetadata.class);
	 
	 BaseDatabaseConstruct.setLastConnections(robotID,metadata);
	 
   BaseDatabaseConstruct bdbc = mock(BaseDatabaseConstruct.class);
   
   //run
   MetaExpression result = ConnectConstruct.process(args, factory, robotID);
   
   
   //verify
   verify(dbService,times(1)).createConnection(eq("databaseName"), eq("user"), eq("pass"));
  // verify(bdbc,times(1)).setLastConnections(eq(robotID), any());
   verify(factory,times(1)).getService("databaseType");
   
   //assert
   Assert.assertEquals(result, new AtomicExpression("databaseName"));
   
  }
  
  /**
   * This test checks whether the method throws an exception when service.createconnection goes wrong.
   * @throws RobotRuntimeException
   */
  @Test (expectedExceptions = RobotRuntimeException.class, expectedExceptionsMessageRegExp = "...")
  public void testProcessSQLException() throws Throwable {
  	MetaExpression database = mockExpression(ATOMIC, true, 0, "databaseName");
    MetaExpression type = mockExpression(ATOMIC, true, 0, "databaseType");
    MetaExpression user = mockExpression(ATOMIC, true, 0, "user");
    MetaExpression pass = mockExpression(ATOMIC, true, 0, "pass");
    MetaExpression options = mockExpression(OBJECT);
    when(options.getValue()).thenReturn(new LinkedHashMap<>());
    
    MetaExpression[] args = {database,type,user,pass,options};
    DatabaseServiceFactory factory = mock(DatabaseServiceFactory.class);
    RobotID robotID = mock(RobotID.class);
    
    Connection connection = mock(Connection.class);
    DatabaseService dbService = mock(DatabaseService.class);
    when((factory).getService("databaseType")).thenReturn(dbService);
    
    
    when((dbService).createConnection(eq("databaseName"),eq("user"),eq("pass"))).thenThrow(new SQLException("..."));

    //run
    MetaExpression result = ConnectConstruct.process(args, factory, robotID);
    
    verify(dbService,times(1)).createConnection(eq("databaseName"),eq("user"),eq("pass"));
  }
  
  /**
   * This test checks whether the method throws an exception when factory.getService goes wrong.
   * @throws RobotRuntimeException
   */
  @Test (expectedExceptions = RobotRuntimeException.class)
  public void testProcessGetServiceException() throws Throwable {
  	MetaExpression database = mockExpression(ATOMIC, true, 0, "databaseName");
    MetaExpression type = mockExpression(ATOMIC, true, 0, "databaseType");
    MetaExpression user = mockExpression(ATOMIC, true, 0, "user");
    MetaExpression pass = mockExpression(ATOMIC, true, 0, "pass");
    MetaExpression options = mockExpression(OBJECT);
    when(options.getValue()).thenReturn(new LinkedHashMap<>());
    
    MetaExpression[] args = {database,type,user,pass,options};
    DatabaseServiceFactory factory = mock(DatabaseServiceFactory.class);
    RobotID robotID = mock(RobotID.class);
    
    Connection connection = mock(Connection.class);
    DatabaseService dbService = mock(DatabaseService.class);
    when((factory).getService("databaseType")).thenThrow(new InstantiationException());
    


    //run
    MetaExpression result = ConnectConstruct.process(args, factory, robotID);
    
    verify(factory,times(1)).getService("databaseType");
  }
}
