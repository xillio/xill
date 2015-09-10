package nl.xillio.xill.plugins.document.services;

import com.google.inject.ImplementedBy;
import nl.xillio.xill.plugins.document.exceptions.PersistenceException;

/**
 * This interface is responsible for the persistence of the UDM.
 * @author Thomas Biesaart
 * @since 3.0.0
 */
public interface PersistenceService extends AutoCloseable {

	/**
	 * Start the persistence
	 */
	void start() throws PersistenceException;
}
