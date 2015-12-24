package nl.xillio.xill.plugins.codec.decode.constructs;

import java.io.*;

import me.biesaart.utils.FileUtilsService;
import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.plugins.codec.decode.services.DecoderService;

import com.google.inject.Inject;

/**
 *
 * <p>
 * Base-64 decode the file, and store it in the other given file.
 * </p>
 *
 * @author Pieter Dirk Soels
 *
 */
public class FileFromBase64Construct extends Construct {
	@Inject
	private final DecoderService decoderService;
	private final FileUtilsService fileUtilsService;

	@Inject
	public FileFromBase64Construct(DecoderService decoderService, FileUtilsService fileUtilsService) {
		this.decoderService = decoderService;
		this.fileUtilsService = fileUtilsService;
	}

	@Override
	public ConstructProcessor prepareProcess(final ConstructContext context) {
		return new ConstructProcessor(
				(input, output) -> process(input, output, decoderService, context),
				new Argument("input", ATOMIC),
				new Argument("output", ATOMIC));
	}

	MetaExpression process(final MetaExpression pathInput, final MetaExpression pathOutput, final DecoderService decoderService, final ConstructContext context) {
		assertNotNull(pathInput, "input file");
		assertNotNull(pathOutput, "output file");

		File fileInput = getFile(context, pathInput.getStringValue());
		File fileOutput = getFile(context, pathOutput.getStringValue());

        decoderService.decodeFileBase64(fileInput, fileOutput, fileUtilsService);

		return NULL;

	}
}
