package nl.xillio.xill.plugins.string;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.xml.bind.DatatypeConverter;

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
 * Base-64 decode the provided string, and store it in the provided file.
 *
 * @author Sander
 *
 */
public class Base64DecodeConstruct implements Construct {

	@Override
	public String getName() {

		return "base64Decode";
	}

	@Override
	public ConstructProcessor prepareProcess(final ConstructContext context) {
		return new ConstructProcessor(Base64DecodeConstruct::process, new Argument("content"),
				new Argument("filename"));
	}

	private static MetaExpression process(final MetaExpression contentVar, final MetaExpression filenameVar) {

		if (contentVar.getType() != ExpressionDataType.ATOMIC) {
			throw new RobotRuntimeException("Expected atomic value.");
		}

		if (contentVar == ExpressionBuilder.NULL || filenameVar == ExpressionBuilder.NULL) {
			throw new RobotRuntimeException("Input cannot be null.");
		}

		String content = contentVar.getStringValue();
		String filename = filenameVar.getStringValue();

		byte[] data = DatatypeConverter.parseBase64Binary(content);

		try (OutputStream out = new FileOutputStream(filename)) {
			out.write(data);
			out.close();
		} catch (FileNotFoundException e) {
			throw new RobotRuntimeException(
					"The file could not be found or the filename is invalid: '" + filename + "'");
		} catch (IOException e) {
			throw new RobotRuntimeException("IO Exception");
		}
		return ExpressionBuilder.NULL;

	}
}
