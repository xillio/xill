package nl.xillio.xill.plugins.codec.decode.constructs;

import com.google.inject.Inject;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.codec.decode.services.DecoderService;

import java.io.UnsupportedEncodingException;

/**
 * Implementation of decoding a base64-encoded string to its decoded value.
 *
 * @author Pieter Soels
 */
public class StringFromBase64Construct extends Construct {

    private final DecoderService decoderService;

    @Inject
    public StringFromBase64Construct(DecoderService decoderService) {
        this.decoderService = decoderService;
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
            return fromValue(decoderService.stringFromBase64(inputString.getStringValue()));
        } catch (UnsupportedEncodingException e) {
            throw new RobotRuntimeException("Cannot decode the byte array in UTF-8", e);
        }
    }
}
