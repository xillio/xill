package nl.xillio.xill.plugins.database.constructs;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import com.google.inject.Inject;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.database.BaseDatabaseConstruct;
import nl.xillio.xill.plugins.database.services.BaseDatabaseService;
import nl.xillio.xill.plugins.database.services.DatabaseService;
import nl.xillio.xill.plugins.database.services.DatabaseServiceFactory;
import nl.xillio.xill.plugins.database.services.EscapeService;
import nl.xillio.xill.plugins.database.util.ConnectionMetadata;

/**
 * 
 * 
 * @author Sander Visser
 *
 */
public class StoreObjectConstruct extends BaseDatabaseConstruct{

	@Override
	public ConstructProcessor prepareProcess(final ConstructContext context) {
		Argument[] args =
			{
			    new Argument("table", ATOMIC),
			    new Argument("object", OBJECT),
			    new Argument("keys", LIST),
			    new Argument("overwrite",TRUE,ATOMIC),
			    new Argument("database",NULL, ATOMIC),
			};
		return new ConstructProcessor((a) -> process(a, factory), args);
	}
	
	static MetaExpression process(final MetaExpression[] args,final DatabaseServiceFactory factory){
		String tblName = args[0].getStringValue();
		LinkedHashMap<String,Object> newObject = (LinkedHashMap<String, Object>) args[1].getValue();
		ConnectionMetadata metaData;
		if(args[4].equals(NULL)){
			metaData = BaseDatabaseService.getLastConnection();
		}else{
			metaData = args[4].getMeta(ConnectionMetadata.class);
			BaseDatabaseService.setLastConnection(metaData);
		}
		Connection connection = metaData.getConnection();
		
		List<MetaExpression> keysMeta = (ArrayList<MetaExpression>)args[2].getValue();
		List<String> keys = new ArrayList<String>();
		for(MetaExpression e : keysMeta){
			keys.add(e.getStringValue());
		}
		boolean overwrite = args[3].getBooleanValue();
	
		try {
			factory.getService(metaData.getDatabaseName()).storeObject(connection,tblName,newObject,keys,overwrite,metaData.getDatabaseName());
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException e) {
			throw new RobotRuntimeException("OH SHIII THAT WENT PRETTY WRONG!");
		}
	
		
		return NULL;
		
	}

}
