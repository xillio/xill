package nl.xillio.xill.plugins.file.constructs;

import java.io.BufferedReader;

import com.google.inject.Inject;

import nl.xillio.xill.api.components.MetaExpression;
import nl.xillio.xill.api.construct.Argument;
import nl.xillio.xill.api.construct.Construct;
import nl.xillio.xill.api.construct.ConstructContext;
import nl.xillio.xill.api.construct.ConstructProcessor;
import nl.xillio.xill.plugins.file.services.fileUtils.FileUtilities;

/**
 *
 */
public class GetTextConstruct extends Construct {

	@Inject
	private FileUtilities fileUtils;

	@Override
	public ConstructProcessor prepareProcess(final ConstructContext context) {
		return new ConstructProcessor((uri) -> process(context, fileUtils, uri), new Argument("uri", ATOMIC));
	}
	static MetaExpression process(final ConstructContext context, final FileUtilities fileUtils, final MetaExpression uri) {

		return fromValue(fileUtils.getText(uri.getStringValue()));
	}
}
