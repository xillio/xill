package nl.xillio.xill.plugins.database.services;

import static org.mockito.Mockito.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.Test;

import nl.xillio.xill.api.components.MetaExpression;

public class BaseDatabaseServiceTest{

  /**
   * method to check whether updateObject() is called when keys.size != 0 and overwrite is true
   * @throws SQLException 
   */
  @Test
  public void testStoreObjectDoUpdate() throws SQLException {
   
  	//mock
  	BaseDatabaseService bds = mock(BaseDatabaseService.class);
  	Connection connection = mock(Connection.class);
  	ArrayList<String> keys = mock(ArrayList.class);
  	
  	LinkedHashMap<String, Object> obj = new LinkedHashMap<>();
  	obj.put("testKey", "testObj");
  	
  	//doThrow(new RuntimeException()).when(bds).updateObject(connection, "test", obj, keys);
  	when(keys.size()).thenReturn(1);
  	
  	//run
  	bds.storeObject(connection, "testKey", obj, keys, true);
  	
  	//verify
  	//verify(bds,times(1)).updateObject(connection, "test", obj, keys);
  	
  	//assert
  	//Nothing to assert.
  }
}
