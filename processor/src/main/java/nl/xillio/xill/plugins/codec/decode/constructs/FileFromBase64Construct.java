package nl.xillio.xill.plugins.codec.decode.constructs;

import com.google.inject.Inject;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.codec.decode.services.DecoderService;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * <p>
 * Base-64 decode the file, and store it in the other given file.
 * </p>
 *
 * @author Pieter Dirk Soels
 */
public class FileFromBase64Construct extends Construct {
    @Inject
    private final DecoderService decoderService;

    @Inject
    public FileFromBase64Construct(DecoderService decoderService) {
        this.decoderService = decoderService;
    }

    @Override
    public ConstructProcessor prepareProcess(final ConstructContext context) {
        return new ConstructProcessor(
                (input, output) -> process(input, output, decoderService, context),
                new Argument("input", ATOMIC),
                new Argument("output", ATOMIC));
    }

    MetaExpression process(final MetaExpression pathInput, final MetaExpression pathOutput, final DecoderService decoderService, final ConstructContext context) {
        assertNotNull(pathInput, "input file");
        assertNotNull(pathOutput, "output file");

        if (pathInput.valueEquals(pathOutput)) {
            throw new RobotRuntimeException("Input path should not be the same as output path.");
        }

        File fileInput = getFile(context, pathInput.getStringValue());
        File fileOutput = getFile(context, pathOutput.getStringValue());

        try {
            decoderService.decodeFileBase64(fileInput, fileOutput);
        } catch (FileNotFoundException e) {
            throw new RobotRuntimeException("The file could not be found or the filename is invalid: " + e.getMessage(), e);
        } catch (IOException e) {
            throw new RobotRuntimeException("Error writing to file: " + e.getMessage(), e);
        }

        return NULL;

    }
}
