package nl.xillio.xill.plugins.codec.decode.constructs;

import java.io.*;

import me.biesaart.utils.FileUtilsService;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.codec.decode.services.DecoderService;

import com.google.inject.Inject;
import nl.xillio.xill.plugins.codec.encode.services.EncoderService;
import org.apache.commons.codec.binary.Base64InputStream;
import org.apache.commons.io.IOUtils;

/**
 *
 * <p>
 * Base-64 decode the provided string, and store it in the provided file.
 * </p>
 *
 * @author Sander
 *
 */
public class FileFromBase64Construct extends Construct {
	@Inject
	private final DecoderService decoderService;
	private final FileUtilsService fileUtilsService;

	@Inject
	public FileFromBase64Construct(DecoderService decoderService, FileUtilsService fileUtilsService) {
		this.decoderService = decoderService;
		this.fileUtilsService = fileUtilsService;
	}

	@Override
	public ConstructProcessor prepareProcess(final ConstructContext context) {
		return new ConstructProcessor(
				(file, path) -> process(file, path, decoderService, context),
				new Argument("file", ATOMIC),
				new Argument("path", ATOMIC));
	}

	MetaExpression process(final MetaExpression pathInput, final MetaExpression pathOutput, final DecoderService decoderService, final ConstructContext context) {
		assertNotNull(pathInput, "input file");
		assertNotNull(pathOutput, "output file");

		File fileInput = getFile(context, pathInput.getStringValue());
		File fileOutput = getFile(context, pathOutput.getStringValue());

        try {
            fileUtilsService.forceMkdir(fileOutput.getParentFile());
        } catch (IOException e) {
            throw new RobotRuntimeException("Error writing to file: " + e.getMessage(), e);
        }

		try (InputStream in = new Base64InputStream(new FileInputStream(fileInput)); OutputStream out = new FileOutputStream(fileOutput)){
			IOUtils.copy(in, out);
		} catch (FileNotFoundException e) {
			throw new RobotRuntimeException("The file could not be found or the filename is invalid: " + e.getMessage(), e);
		} catch (IOException e) {
			throw new RobotRuntimeException("Error writing to file: " + e.getMessage(), e);
		}

		return NULL;

	}
}
