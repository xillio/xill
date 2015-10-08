package nl.xillio.xill.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

/**
 * Settings needed by the Ace editor Xill mode
 * 
 * @author Geert Konijnendijk
 *
 */
public class HighlightSettings {

	private List<String> keywords = new ArrayList<>();
	private List<String> builtins = new ArrayList<>();

	/**
	 * Add a single Xill keyword to the settings
	 * 
	 * @param keyword
	 *        A Xill keyword
	 */
	public void addKeyword(String keyword) {
		keywords.add(keyword);
	}

	/**
	 * Add a single Xill builtin to the settings
	 * 
	 * @param builtin
	 *        A Xill builtin
	 */
	public void addBuiltin(String builtin) {
		builtins.add(builtin);
	}

	/**
	 * Add multiple keywords to the settings
	 * 
	 * @param keywords
	 *        Xill keywords
	 */
	public void addKeywords(String... keywords) {
		addKeywords(Arrays.asList(keywords));
	}

	/**
	 * Add multiple builtins to the settings
	 * 
	 * @param builtins
	 *        Xill builtins
	 */
	public void addBuiltins(String... builtins) {
		addBuiltins(Arrays.asList(builtins));
	}

	/**
	 * Add multiple keywords to the settings
	 * 
	 * @param keywords
	 *        Xill keywords
	 */
	public void addKeywords(Collection<String> keywords) {
		this.keywords.addAll(keywords);
	}

	/**
	 * Add multiple builtins to the settings
	 * 
	 * @param builtins
	 *        Xill builtins
	 */
	public void addBuiltins(Collection<String> builtins) {
		this.builtins.addAll(builtins);
	}

	/**
	 * 
	 * @return All keywords separated with "|"
	 */
	public String getKeywords() {
		return StringUtils.join(keywords, "|");
	}

	/**
	 * 
	 * @return All builtins separated with "|"
	 */
	public String getBuiltins() {
		return StringUtils.join(builtins, "|");
	}
}
