package nl.xillio.xill.util.settings;

import java.util.List;
import java.util.Map;

/**
 * Interface covers all needed methods to interact with the data holder for the Settings purposes.
 * That data holder can be database, file, etc. It depends just on the implementation. 
 * 
 * @author Zbynek Hochmann
 */
public interface ContentHandler {
	/**
	 * Initialize the target content provider
	 * E.g. opens existing file or creates the new file and creates the basic structure or connects to DB, creates the schema if not done yet, etc. 
	 * 
	 * @throws Exception when some problem occurs
	 */
	void init() throws Exception;
	
	/**
	 * Returns all item values for item specified by category and key value
	 * This is used for getting one concrete item having key specified
	 * 
	 * @param category The name of the category (e.g. "Layout")
	 * @param keyValue The value of the item's key (e.g. "LeftPanelWidth")
	 * @return The list of item values where key is the name of value
	 * @throws Exception when some problem occurs
	 */
	Map<String, Object> get(final String category, final String keyValue) throws Exception;

	/**
	 * Returns list of items. The item structure is the same as described in get()
	 * This is usually applied to category having items without keys but it can be used for even items having key (but the information what is key for particular item won't be there)
	 * 
	 * @param category The name of the category (e.g. "Layout")
	 * @return The list of items
	 * @throws Exception when some problem occurs
	 */
	List<Map<String, Object>> getAll(final String category) throws Exception;
	
	/**
	 * Sets one item - it means it will add the item if does not exist or update the values if already exists
	 * The values that already exists and not in itemContent will be left unchanged - 
	 *  - the only way how to delete them is to delete the item first and then call this set() (e.g. add new one)
	 *  
	 * @param category The name of the category (e.g. "Layout")
	 * @param itemContent The content of one item - e.g. list of item values
	 * @param keyName Specifies what item value is the key for the item (this can be null in case of adding keyless item - the item has no key then - it's used for simple list usage)
	 * @param keyValue Specifies what value has key - it's used just for looking for existing item (it can be null if keyName is null)
	 * @return true if the new item was created, false if the item already exists
	 * @throws Exception when some problem occurs
	 */
	boolean set(final String category, final Map<String, Object> itemContent, final String keyName, final String keyValue) throws Exception;
	
	/**
	 * Deletes existing item
	 * It will try to find the existing item according to category and item's key
	 * 
	 * @param category The name of the category (e.g. "Layout")
	 * @param keyName Specifies what item value is the key for the item
	 * @param keyValue Specifies the value of that key
	 * @return true if item was found and deleted, false if not found (i.e. not deleted)
	 * @throws Exception when some problem occurs
	 */
	boolean delete(final String category, final String keyName, final String keyValue) throws Exception;
	
	/**
	 * Checks the existence of the item 
	 * 
	 * @param category The name of the category (e.g. "Layout")
	 * @param keyName Specifies what item value is the key for the item
	 * @param keyValue Specifies the value of that key
	 * @return true if item was found (i.e. exists), false if item was not found (i.e. does not exist)
	 * @throws Exception when some problem occurs
	 */
	boolean exist(final String category, final String keyName, final String keyValue) throws Exception;
}
