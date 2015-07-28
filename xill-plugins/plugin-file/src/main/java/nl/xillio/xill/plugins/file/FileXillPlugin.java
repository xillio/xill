package nl.xillio.xill.plugins.file;

import com.google.inject.Binder;

import nl.xillio.plugins.XillPlugin;
import nl.xillio.xill.plugins.file.services.fileUtils.FileUtilities;
import nl.xillio.xill.plugins.file.services.fileUtils.FileUtilitiesImpl;

/**
 * This package includes all example constructs
 */
public class FileXillPlugin extends XillPlugin {
	
	@Override
	public void configure(Binder binder) {
		binder.bind(FileUtilities.class).to(FileUtilitiesImpl.class);
	}
}
