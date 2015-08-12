package nl.xillio.xill.plugins.database.services;

/**
 * 
 * Database service for escaping strings.
 * 
 * @author Sander Visser
 *
 */
public class EscapeServiceImpl implements EscapeService{

	@Override
	public String escape(String input) {
		input = input.replaceAll("\\\\", "\\\\\\\\");
		input = input.replaceAll("\\n", "\\\\n");
		input = input.replaceAll("\\r", "\\\\r");
		input = input.replaceAll("\\t", "\\\\t");
		input = input.replaceAll("\\b", "\\\\b");
		input = input.replaceAll("\\f", "\\\\f");
		input = input.replaceAll("\\00", "\\\\0");
		input = input.replaceAll("'", "\\\\'");
		input = input.replaceAll("\\\"", "\\\\\"");
		return input;
	}

}
