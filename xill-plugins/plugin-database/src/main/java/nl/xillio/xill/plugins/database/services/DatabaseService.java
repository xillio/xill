package nl.xillio.xill.plugins.database.services;

import java.sql.Connection;

import nl.xillio.xill.plugins.database.util.Tuple;
import nl.xillio.xill.services.XillService;

public interface DatabaseService extends XillService {

	Connection createConnection(String database, String user, String pass, Tuple<String, String>... options);

}
