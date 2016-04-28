package nl.xillio.xill.plugins.stream.constructs;

import nl.xillio.xill.api.components.ExpressionBuilder;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.components.MetaExpressionIterator;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.api.errors.RobotRuntimeException;

import java.util.Scanner;
import java.util.regex.Pattern;

import static nl.xillio.xill.plugins.stream.utils.StreamUtils.getInputStream;

class IterateConstruct extends Construct {
    private static final Pattern LINE_SEPARATOR_PATTERN = Pattern.compile("\r\n|[\n\r\u2028\u2029\u0085]");

    @Override
    public ConstructProcessor prepareProcess(ConstructContext context) {
        return new ConstructProcessor(
                this::process,
                new Argument("stream", ATOMIC),
                new Argument("delimiter", fromValue(""), ATOMIC)
        );
    }

    @SuppressWarnings("squid:UnusedPrivateMethod") // Sonar does not detect method references
    private MetaExpression process(final MetaExpression stream, final MetaExpression delimiter) {
        if (delimiter.isNull()) {
            throw new RobotRuntimeException("The delimiter cannot be null");
        }

        // Need to register the stream, otherwise it will be disposed of when the provider of the stream (a ConstructCall, for example)
        // is closed. Consider the case it is declared inline the Stream.iterate
        stream.registerReference();

        Scanner scanner = new Scanner(getInputStream(stream, "stream"));

        if (!delimiter.getStringValue().isEmpty()) {
            scanner.useDelimiter(delimiter.getStringValue());
        } else {
            scanner.useDelimiter(LINE_SEPARATOR_PATTERN);
        }

        // Now the stream is registered but will not be closed when the MetaExpressionIterator is closed (only the Scanner is)
        // so we need to release both when the MetaExpressionIterator is closed
        MetaExpressionIterator<String> iterator = new MetaExpressionIterator<String>(scanner, ExpressionBuilder::fromValue) {
            @Override
            public void close() throws Exception {
                super.close(); // closes the Scanner
                stream.releaseReference(); // release the reference shielded by the Scanner
            }
        };

        MetaExpression result = fromValue(buildStringValue(stream, delimiter));
        result.storeMeta(iterator);

        return result;
    }

    private String buildStringValue(MetaExpression stream, MetaExpression delimiter) {
        String description = stream.getBinaryValue().getDescription();
        String delimiterValue = delimiter.getStringValue();

        if (description == null) {
            return "[Stream Iterator]";
        }

        if (delimiterValue.isEmpty()) {
            return "[Stream Iterator: " + description + "]";
        }

        return "[Stream Iterator: " + description + ":" + delimiterValue + "]";
    }
}
