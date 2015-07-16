package nl.xillio.xill.plugins.selenium.constructs;

import java.io.File;

import org.apache.commons.io.FileUtils;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.api.errors.RobotRuntimeException;

public class StringToPageConstruct extends Construct {

	@Override
	public String getName() {
		return "stringToPage";
	}

	@Override
	public ConstructProcessor prepareProcess(final ConstructContext context) {
		return new ConstructProcessor(
			StringToPageConstruct::process,
			new Argument("content"));
	}

	public static MetaExpression process(final MetaExpression contentVar) {
		String content = contentVar.getStringValue();

		try {
			File htmlFile = File.createTempFile("ct_sel", ".html");
			htmlFile.deleteOnExit();
			FileUtils.writeStringToFile(htmlFile, content);
			String uri = "file:///" + htmlFile.getAbsolutePath();
			return LoadPageConstruct.process(fromValue(uri), NULL);
		} catch (Exception e) {
			throw new RobotRuntimeException(e.getClass().getSimpleName(), e);
		}
	}

}
