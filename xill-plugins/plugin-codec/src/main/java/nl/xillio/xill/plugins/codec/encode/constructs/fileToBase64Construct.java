package nl.xillio.xill.plugins.codec.encode.constructs;

import java.io.*;

import com.google.inject.Inject;
import me.biesaart.utils.FileUtilsService;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.plugins.codec.encode.services.EncoderService;

/**
 * <p>
 * Creates a base-64 encoded file from the provided file.
 * </p>
 *
 * @author Pieter Dirk Soels
 *
 */
public class FileToBase64Construct extends Construct {

	private final EncoderService encoderService;
    private final FileUtilsService fileUtilsService;

    @Inject
    public FileToBase64Construct(EncoderService encoderService, FileUtilsService fileUtilsService) {
        this.encoderService = encoderService;
        this.fileUtilsService = fileUtilsService;
    }

    @Override
	public ConstructProcessor prepareProcess(final ConstructContext context) {
        return new ConstructProcessor(
                (input, output) -> process(input, output, context),
                new Argument("input", ATOMIC),
                new Argument("output", ATOMIC));
    }

	MetaExpression process(final MetaExpression pathInput, final MetaExpression pathOutput, final ConstructContext context) {
		assertNotNull(pathInput, "input file");
        assertNotNull(pathOutput, "output file");

        File fileInput = getFile(context, pathInput.getStringValue());
        File fileOutput = getFile(context, pathOutput.getStringValue());

        encoderService.encodeFileBase64(fileInput, fileOutput, fileUtilsService);

		return NULL;

	}

}
