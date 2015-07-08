package nl.xillio.xill.plugins.string;

import java.io.File;
import java.io.IOException;

import javax.xml.bind.DatatypeConverter;

import org.apache.commons.io.FileUtils;

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
 * Returns the base-64 encoded string of the provided file.
 *
 * @author Sander
 *
 */
public class Base64EncodeConstruct implements Construct {

	@Override
	public String getName() {

		return "base64Encode";
	}

	@Override
	public ConstructProcessor prepareProcess(final ConstructContext context) {
		return new ConstructProcessor(Base64EncodeConstruct::process, new Argument("file"));
	}

	private static MetaExpression process(final MetaExpression file) {

		if (file.getType() != ExpressionDataType.ATOMIC) {
			throw new RobotRuntimeException("Expected atomic value.");
		}

		if (file == ExpressionBuilder.NULL) {
			throw new RobotRuntimeException("Input cannot be null.");
		}

		byte[] data = null;

		try {
			String filename = file.getStringValue();
			data = FileUtils.readFileToByteArray(new File(filename));

		} catch (IOException e) {
			throw new RobotRuntimeException("IO Exception");
		}

		if (data != null && data.length > 0) {
			String content = DatatypeConverter.printBase64Binary(data);
			if (content != null && !content.equals("")) {
				return ExpressionBuilder.fromValue(content);
			}
		}

		return ExpressionBuilder.NULL;

	}
}
