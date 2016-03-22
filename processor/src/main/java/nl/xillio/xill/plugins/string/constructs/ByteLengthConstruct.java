package nl.xillio.xill.plugins.string.constructs;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.plugins.stream.utils.StreamUtils;

import java.nio.charset.Charset;

/**
 * Get the byte length of a string.
 */
public class ByteLengthConstruct extends Construct {
    @Override
    public ConstructProcessor prepareProcess(final ConstructContext context) {
        return new ConstructProcessor(
                ByteLengthConstruct::process,
                new Argument("string", ATOMIC),
                new Argument("encoding", NULL, ATOMIC));
    }

    static MetaExpression process(final MetaExpression string, final MetaExpression encoding) {
        assertNotNull(string, "string");

        // Get the string and charset.
        String str = string.getStringValue();
        Charset charset = StreamUtils.getCharset(encoding);

        return fromValue(str.getBytes(charset).length);
    }
}
