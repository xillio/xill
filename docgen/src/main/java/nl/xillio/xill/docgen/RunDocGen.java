package nl.xillio.xill.docgen;

import nl.xillio.xill.docgen.exceptions.ParsingException;
import nl.xillio.xill.docgen.impl.XillDocGen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * //TODO javadoc
 *
 * @author Thomas Biesaart
 * @since 12-8-2015
 */
public class RunDocGen {
	public static void main(String... args) throws ParsingException {
		DocGen docgen = new XillDocGen(null);

		DocumentationParser parser = docgen.getParser();

		DocumentationEntity entity = parser.parse(RunDocGen.class.getResource("/test.xml"), "testMe");

		DocumentationGenerator generator = docgen.getGenerator("MyPackage");

		generator.generate(entity);

	}

	static class Function implements DocumentationEntity {

		@Override
		public String getIdentity() {
			return "loadSomething";
		}

		@Override
		public String getType() {
			return "construct.html";
		}

		@Override
		public Map<String, Object> getProperties() {
			Map<String, Object> model = new HashMap<>();
			model.put("test", "awesome");
			model.put("name", getIdentity());
			model.put("type", getType());
			List<Map<String,Object>> parameters = new ArrayList<>();
			model.put("parameters", parameters);
			parameters.add(param("ATOMIC", "target", null));
			parameters.add(param("ANY", "value", "null"));
			return model;
		}

		private Map<String, Object> param(String atomic, String test, Object defaultValue) {
			Map<String, Object> result = new HashMap<>();
			result.put("type", atomic);
			result.put("name", test);
			result.put("default", defaultValue);
			result.put("description", "This is a default description because I am too lazy to write two.");
			return result;
		}
	}
}
