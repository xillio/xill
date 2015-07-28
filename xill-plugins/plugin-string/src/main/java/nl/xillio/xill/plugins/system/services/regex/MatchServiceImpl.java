package nl.xillio.xill.plugins.system.services.regex;

import java.util.List;

/**
 * This is the main implementation of the {@link MatchService}
 */
public class MatchServiceImpl implements MatchService {

	@Override
	public boolean contains(List<Object> list, Object needle) {
		return list.contains(needle);
	}

	@Override
	public boolean contains(String parent, String child) {
		return parent.contains(child);
	}

}
