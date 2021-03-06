package nl.xillio.xill.plugins.codec.decode.constructs;

import com.google.inject.Inject;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.plugins.codec.decode.services.DecoderService;

/**
 * Do URL decoding of the provided string.
 *
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
                new Argument("inputString", ATOMIC));
    }

    MetaExpression process(final MetaExpression string) {
        return fromValue(decoderService.urlDecode(string.getStringValue()));
    }
}
