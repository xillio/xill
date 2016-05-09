package nl.xillio.xill.plugins.codec.decode.constructs;

import com.google.inject.Inject;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.api.errors.InvalidUserInputException;
import nl.xillio.xill.api.errors.OperationFailedException;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import nl.xillio.xill.plugins.codec.decode.services.DecoderService;
import org.apache.commons.codec.DecoderException;

import java.nio.charset.UnsupportedCharsetException;

/**
 * Implementation of Decode.FromHex Construct. See {@link DecoderService#fromHex(String, String)}.
 *
 * @author Paul van der Zandt
 * @since 3.0
 */
public class FromHexConstruct extends Construct {

    private final DecoderService decoderService;

    @Inject
    public FromHexConstruct(DecoderService decoderService) {
        this.decoderService = decoderService;
    }

    @Override
    public ConstructProcessor prepareProcess(ConstructContext context) {
        return new ConstructProcessor(this::process,
                new Argument("hexString", ATOMIC),
                new Argument("charset", fromValue("UTF-8"), ATOMIC));
    }

    @SuppressWarnings("squid:UnusedPrivateMethod")
    private MetaExpression process(MetaExpression hexString, MetaExpression charsetName) {
        try {
            return hexString.isNull() ? NULL : fromValue(decoderService.fromHex(hexString.getStringValue(), charsetName.getStringValue()));
        } catch (DecoderException e) {
            throw new OperationFailedException("convert hex to normal", e.getMessage(), e);
        } catch (UnsupportedCharsetException e) {
            throw new InvalidUserInputException("Unknown character set.", charsetName.getStringValue(), "A valid character set.", e);
        }
    }
}
