package nl.xillio.xill.plugins.codec.encode.constructs;

import com.google.inject.Inject;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.plugins.codec.encode.services.EncoderService;

/**
 * Encodes all special XML characters (&lt;,&gt;,&amp;,&quot;,&#39;) to their respective xml entities.
 *
 * @author Sander Visser
 */
public class EscapeXMLConstruct extends Construct {

    private final EncoderService encoderService;

    @Inject
    public EscapeXMLConstruct(EncoderService encoderService) {
        this.encoderService = encoderService;
    }

    @Override
    public ConstructProcessor prepareProcess(final ConstructContext context) {
        return new ConstructProcessor(
                this::process,
                new Argument("String", ATOMIC));
    }

    MetaExpression process(final MetaExpression stringVar) {
        assertNotNull(stringVar, "string");

        String text = stringVar.getStringValue();

        return fromValue(encoderService.escapeXML(text));

    }

}
