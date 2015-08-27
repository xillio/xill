package nl.xillio.xill.plugins.database.constructs;

import org.mockito.Mockito;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.mysql.jdbc.Connection;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.components.RobotID;
import nl.xillio.xill.plugins.database.BaseDatabaseConstruct;
import nl.xillio.xill.plugins.database.services.DatabaseService;
import nl.xillio.xill.plugins.database.services.DatabaseServiceFactory;
import nl.xillio.xill.plugins.database.util.ConnectionMetadata;
import nl.xillio.xill.testutils.ConstructTest;
import oracle.net.aso.c;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

import java.sql.SQLException;
import java.util.LinkedHashMap;

public class GetObjectConstructTest extends ConstructTest{

  @Test
  public void testProcessDatabaseNull() throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
    MetaExpression table = mockExpression(ATOMIC, true, 0, "table");
    MetaExpression object = fromValue(new LinkedHashMap<>());
    MetaExpression database = mockExpression(ATOMIC, true, 0, "database");
    DatabaseServiceFactory factory = mock(DatabaseServiceFactory.class);
    RobotID id = mock(RobotID.class);
    
    ConnectionMetadata conMetadata = mock(ConnectionMetadata.class);
    
    //so we get the lastConnections
    when(database.equals(NULL)).thenReturn(true);
    LinkedHashMap connectionMap = mock(LinkedHashMap.class);
    BaseDatabaseConstruct.setLastConnections(id,conMetadata);
    
    
    DatabaseService dbService = mock(DatabaseService.class);
    when((factory).getService(any())).thenReturn(dbService);
     
    when((conMetadata).getDatabaseName()).thenReturn("databaseName");
    
    LinkedHashMap resultMap = mock(LinkedHashMap.class);
    when((dbService).getObject(any(), eq("table"),any())).thenReturn(resultMap);
    BaseDatabaseConstruct bdbc = mock(BaseDatabaseConstruct.class);

    MetaExpression result = GetObjectConstruct.process(table, object, database, factory, id);
    
    //verify
    verify(conMetadata,times(1)).getConnection();
    verify(dbService,times(1)).getObject(any(), any(), any());
    
    Assert.assertEquals(result, NULL);
  }
}
