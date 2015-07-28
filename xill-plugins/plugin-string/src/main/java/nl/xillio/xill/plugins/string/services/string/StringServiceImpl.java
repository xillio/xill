package nl.xillio.xill.plugins.string.services.string;

import org.apache.commons.lang3.StringUtils;

/**
 * This is the main implementation of the {@link StringService}
 */
public class StringServiceImpl implements StringService {

	@Override
	public boolean contains(final String parent, final String child) {
		return parent.contains(child);
	}

	@Override
	public boolean endsWith(final String first, final String second) {
		return first.endsWith(second);
	}

	@Override
	public int indexOf(final String haystack, final String needle, final int index) {
		return haystack.indexOf(needle, index);
	}

	@Override
	public String join(final String[] input, final String delimiter) {
		return StringUtils.join(input, delimiter);
	}
}
