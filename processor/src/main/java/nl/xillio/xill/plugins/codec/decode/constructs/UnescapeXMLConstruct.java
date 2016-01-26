package nl.xillio.xill.plugins.codec.decode.constructs;

import com.google.inject.Inject;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.plugins.codec.decode.services.DecoderService;

/**
 * <p>
 * Decodes all ampersand-encoded characters in the provided text.
 * </p>
 *
 * @author Sander Visser
 */
public class UnescapeXMLConstruct extends Construct {

    private DecoderService decoderService;

    @Inject
    public UnescapeXMLConstruct(DecoderService decoderService) {
        this.decoderService = decoderService;
    }

    @Override
    public ConstructProcessor prepareProcess(final ConstructContext context) {
        return new ConstructProcessor(
                this::process,
                new Argument("string", ATOMIC),
                new Argument("passes", fromValue(1), ATOMIC));
    }

    MetaExpression process(final MetaExpression stringVar, final MetaExpression passesVar) {
        assertNotNull(stringVar, "string");
        assertNotNull(passesVar, "passes");

        String text = stringVar.getStringValue();

        int passes = passesVar.getNumberValue().intValue();

        return fromValue(decoderService.unescapeXML(text, passes));
    }
}
