package nl.xillio.xill.plugins.codec.constructs;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.api.errors.RobotRuntimeException;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.NullOutputStream;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.DigestOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * This class will read an input stream and create a digest token from it. It can optionally forward all the data
 * to an output stream.
 *
 * @author Thomas Biesaart
 */
public abstract class AbstractDigestConstruct extends Construct {

    @Override
    public ConstructProcessor prepareProcess(ConstructContext context) {
        return new ConstructProcessor(
                this::tryProcess,
                new Argument("input", ATOMIC),
                new Argument("output", NULL, ATOMIC)
        );
    }

    @SuppressWarnings("squid:UnusedPrivateMethod") // Sonar still doesn't support method references
    private MetaExpression tryProcess(MetaExpression input, MetaExpression output) {
        try {
            return process(input, output);
        } catch (IOException e) {
            throw new RobotRuntimeException("Could not get digest: " + e.getMessage(), e);
        }
    }

    private MetaExpression process(MetaExpression input, MetaExpression output) throws IOException {

        if (input.isNull()) {
            throw new RobotRuntimeException("The provided input cannot be null");
        }

        if (input.getBinaryValue().hasInputStream()) {
            return processStream(input, output);
        }

        return processString(input, output);
    }

    /**
     * Process this construct with string input.
     *
     * @param input  the input expression
     * @param output the output expression
     * @return the result
     * @throws IOException if an IO error occurs
     */
    private MetaExpression processString(MetaExpression input, MetaExpression output) throws IOException {
        OutputStream outputStream = getOutputStream(output);
        MessageDigest messageDigest = digest();
        try (InputStream inputStream = IOUtils.toInputStream(input.getStringValue())) {
            DigestOutputStream digestOutputStream = new DigestOutputStream(outputStream, messageDigest);
            IOUtils.copyLarge(inputStream, digestOutputStream);
        }
        String result = process(messageDigest);
        return fromValue(result);
    }

    /**
     * Process this construct with stream input.
     *
     * @param input  the input expression
     * @param output the output expression
     * @return the result
     * @throws IOException if an IO error occurs
     */
    private MetaExpression processStream(MetaExpression input, MetaExpression output) throws IOException {
        InputStream inputStream = input.getBinaryValue().getInputStream();
        OutputStream outputStream = getOutputStream(output);
        MessageDigest messageDigest = digest();
        DigestOutputStream digestOutputStream = new DigestOutputStream(outputStream, messageDigest);
        IOUtils.copyLarge(inputStream, digestOutputStream);
        String result = process(messageDigest);
        return fromValue(result);
    }

    private OutputStream getOutputStream(MetaExpression output) throws IOException {
        if (output.isNull()) {
            return new NullOutputStream();
        }

        if (output.getBinaryValue().hasOutputStream()) {
            return output.getBinaryValue().getOutputStream();
        }

        throw new RobotRuntimeException("Please provide a stream for the output parameter");
    }

    private MessageDigest digest() {
        try {
            return getDigest();
        } catch (NoSuchAlgorithmException e) {
            throw new RobotRuntimeException("The algorithm could not be found, please contact the support team.", e);
        }
    }

    /**
     * Process the digest to a readable result.
     *
     * @param messageDigest the digest
     * @return the result
     */
    protected String process(MessageDigest messageDigest) {
        return new String(Hex.encodeHex(messageDigest.digest()));
    }

    /**
     * Create a new digest that can be used for processing.
     *
     * @return the digest
     * @throws NoSuchAlgorithmException is thrown if no algorithm is found
     */
    protected abstract MessageDigest getDigest() throws NoSuchAlgorithmException;
}
