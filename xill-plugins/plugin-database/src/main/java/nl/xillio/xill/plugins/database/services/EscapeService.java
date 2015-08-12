package nl.xillio.xill.plugins.database.services;

import nl.xillio.xill.services.XillService;

public interface EscapeService  extends XillService {

	/**
	 * @param input string to escape
	 * @return the SQL-escaped string
	 */
	public String escape(String input);
}
