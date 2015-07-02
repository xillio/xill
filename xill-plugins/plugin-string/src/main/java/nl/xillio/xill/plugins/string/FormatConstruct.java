package nl.xillio.xill.plugins.string;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

import nl.xillio.xill.api.components.ExpressionBuilder;
import nl.xillio.xill.api.components.ExpressionDataType;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.api.errors.RobotRuntimeException;

/**
 *
 * Formats the string with the provided values. See for documentation: http://docs.oracle.com/javase/1.5.0/docs/api/java/util/Formatter.html#syntax
 *
 * @author Sander
 *
 */
public class FormatConstruct implements Construct {
private final RegexConstruct regexConstruct;
	
	public FormatConstruct(RegexConstruct regexConstruct) {
		this.regexConstruct = regexConstruct;
	}
	@Override
	public String getName() {

		return "format";
	}

	@Override
	public ConstructProcessor prepareProcess(final ConstructContext context) {
		return new ConstructProcessor((textVar,valueVar)->process(regexConstruct,textVar,valueVar), new Argument("text"), new Argument("value"));
	}

	private static MetaExpression process(final RegexConstruct regexConstruct, final MetaExpression textVar, final MetaExpression... valueVar) {

		/*
		if (textVar.getType() != ExpressionDataType.ATOMIC){
			throw new RobotRuntimeException("Expected atomic value.");
		}

		// If the value is null return the original text.
		//if (value == ExpressionBuilder.NULL) {
		//	return text;
		//}

		if (textVar == ExpressionBuilder.NULL) {
			throw new RobotRuntimeException("Input string cannot be null.");
		}
		List<MetaExpression> formatList = new ArrayList<>();
		
		Matcher matcher = regexConstruct.getMatcher(regex, textVar.getStringValue(), RegexConstruct.REGEX_TIMEOUT);
		int i = 0;
		while (matcher.find()) {
			formatList.add(i, ExpressionBuilder.fromValue(matcher.group()));
		}
		
		<> list = new ArrayList<>();
		
		for(int j = 0; j < formatList.size();j++){
			
			
		}
		*/
		return ExpressionBuilder.fromValue("Not yet working");
		
	}
	
	
}
