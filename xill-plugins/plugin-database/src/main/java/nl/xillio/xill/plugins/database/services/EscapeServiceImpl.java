package nl.xillio.xill.plugins.database.services;

import nl.xillio.xill.plugins.database.util.Tuple;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;

/**
 * 
 * Database service for escaping strings.
 * 
 * @author Sander Visser, Daan Knoope
 *
 */
public class EscapeServiceImpl implements EscapeService{

	static final List<Tuple<String, String>> replacements = Arrays.asList(
					new Tuple<>("\\", "\\\\"),
					new Tuple<>("\n", "\\n"),
					new Tuple<>("\r", "\\r"),
					new Tuple<>("\t", "\\t"),
					new Tuple<>("\b", "\\b"),
					new Tuple<>("\f", "\\f"),
					new Tuple<>("\0", "\\0")
					);

	@Override
	public String escape(String input) {
		for(Tuple<String, String> replacement : replacements)
			input = input.replaceAll(Matcher.quoteReplacement(replacement.getKey()), Matcher.quoteReplacement(replacement.getValue()));
		return input;
	}

}
