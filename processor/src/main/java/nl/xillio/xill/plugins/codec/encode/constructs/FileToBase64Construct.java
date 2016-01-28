package nl.xillio.xill.plugins.codec.encode.constructs;

import com.google.inject.Inject;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.codec.encode.services.EncoderService;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * <p>
 * Creates a base-64 encoded file from the provided file.
 * </p>
 *
 * @author Pieter Dirk Soels
 */
public class FileToBase64Construct extends Construct {

    private final EncoderService encoderService;

    @Inject
    public FileToBase64Construct(EncoderService encoderService) {
        this.encoderService = encoderService;
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

        if (pathInput.valueEquals(pathOutput)) {
            throw new RobotRuntimeException("Input path should not be the same as output path.");
        }

        File fileInput = getFile(context, pathInput.getStringValue());
        File fileOutput = getFile(context, pathOutput.getStringValue());

        try {
            encoderService.encodeFileBase64(fileInput, fileOutput);
        } catch (FileNotFoundException e) {
            throw new RobotRuntimeException("The file could not be found or the filename is invalid: " + e.getMessage(), e);
        } catch (IOException e) {
            throw new RobotRuntimeException("Error writing to file: " + e.getMessage(), e);
        }

        return NULL;

    }

}
