package nl.xillio.xill.util.settings;

import java.util.List;
import java.util.Map;

public interface ContentHandler {
	void init() throws Exception;
	Map<String, Object> get(final String category, final String keyValue) throws Exception;
	List<Map<String, Object>> getAll(final String category) throws Exception;
	boolean set(final String category, final Map<String, Object> itemContent, final String keyName, final String keyValue) throws Exception;
	boolean delete(final String category, final String keyName, final String keyValue) throws Exception;
	boolean exist(final String category, final String keyName, final String keyValue) throws Exception;
}
