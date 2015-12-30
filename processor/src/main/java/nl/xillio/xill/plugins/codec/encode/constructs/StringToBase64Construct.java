package nl.xillio.xill.plugins.codec.encode.constructs;

import com.google.inject.Inject;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.*;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.codec.encode.services.EncoderService;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * Implementation of encoding a string to base64.
 *
 * @author Pieter Soels
 */
public class StringToBase64Construct extends Construct {

    private final EncoderService encoderService;

    @Inject
    public StringToBase64Construct(EncoderService encoderService) {
        this.encoderService = encoderService;
    }

    @Override
    public ConstructProcessor prepareProcess(ConstructContext context) {
        return new ConstructProcessor(this::process,
                new Argument("inputString", ATOMIC));
    }

    MetaExpression process(MetaExpression inputString) {
        if (inputString.isNull()) {
            throw new RobotRuntimeException("You cannot encode a null value");
        }

        try {
            return fromValue(encoderService.encodeStringBase64(inputString.getStringValue()));
        } catch (UnsupportedEncodingException e) {
            throw new RobotRuntimeException("Cannot encode the string in UTF-8", e);
        } catch (IOException e) {
            throw new RobotRuntimeException("Something went wrong when encoding: " + e.getMessage(), e);
        }
    }
}
