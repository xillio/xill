package nl.xillio.xill.plugins.codec.encode.constructs;

import com.google.inject.Inject;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.codec.encode.services.EncoderService;

import java.nio.charset.UnsupportedCharsetException;

/**
 * Implementation of Encode.ToHex Construct. See {@link EncoderService#toHex(String, boolean, String)}
 *
 * @author Paul van der Zandt
 * @since 3.0
 */
public class ToHexConstruct extends Construct {

    private final EncoderService encoderService;

    @Inject
    public ToHexConstruct(EncoderService encoderService) {
        this.encoderService = encoderService;
    }

    @Override
    public ConstructProcessor prepareProcess(ConstructContext context) {
        return new ConstructProcessor(this::process,
                new Argument("inputString", ATOMIC),
                new Argument("toLowerCase", FALSE, ATOMIC),
                new Argument("charset", fromValue("UTF-8"), ATOMIC));
    }

    private MetaExpression process(MetaExpression inputString, MetaExpression toLowerCase, MetaExpression charsetName) {
        try {
            return inputString.isNull() ? NULL : fromValue(encoderService.toHex(inputString.getStringValue(), toLowerCase.getBooleanValue(), charsetName.getStringValue()));
        } catch (UnsupportedCharsetException e) {
            throw new RobotRuntimeException("Unknown character set: " + charsetName.getStringValue());
        }
    }
}
