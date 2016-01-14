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
 * @author Pieter Dirk Soels
 */
public class FromPercentConstruct extends Construct {

    private final DecoderService decoderService;

    @Inject
    public FromPercentConstruct(DecoderService decoderService) {
        this.decoderService = decoderService;
    }

    @Override
    public ConstructProcessor prepareProcess(ConstructContext context) {
        return new ConstructProcessor(
                this::process,
                new Argument("string", ATOMIC),
                new Argument("xWwwForm", FALSE, ATOMIC));
    }

    MetaExpression process(final MetaExpression string, final MetaExpression xWwwFormVar) {
        try {
            return fromValue(decoderService.urlDecode(string.getStringValue()));
        } catch (UnsupportedEncodingException e) {
            throw new RobotRuntimeException("Cannot URL decode the string", e);
        }
    }
}
