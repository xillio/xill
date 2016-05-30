package nl.xillio.xill.plugins.codec.encode.constructs;

import com.google.inject.Inject;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.api.errors.OperationFailedException;
import nl.xillio.xill.plugins.codec.encode.services.EncoderService;

import java.io.UnsupportedEncodingException;

/**
 * Do URL encoding of the provided string.
 *
 * @author Zbynek Hochmann
 */
public class ToPercentConstruct extends Construct {

    private final EncoderService encoderService;

    @Inject
    public ToPercentConstruct(EncoderService encoderService) {
        this.encoderService = encoderService;
    }

    @Override
    public ConstructProcessor prepareProcess(final ConstructContext context) {
        return new ConstructProcessor(
                this::process,
                new Argument("string", ATOMIC),
                new Argument("xWwwForm", FALSE, ATOMIC));
    }

    MetaExpression process(final MetaExpression string, final MetaExpression xWwwFormVar) {
        try {
            return string.isNull() ? NULL : fromValue(encoderService.urlEncode(string.getStringValue(), !xWwwFormVar.isNull() && xWwwFormVar.getBooleanValue()));
        } catch (UnsupportedEncodingException e) {
            throw new OperationFailedException("URL encode the string", e.getMessage(), e);
        }
    }
}
