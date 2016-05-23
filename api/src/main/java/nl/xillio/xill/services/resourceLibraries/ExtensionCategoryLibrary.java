package nl.xillio.xill.services.resourceLibraries;


import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.nio.file.Paths;

@Singleton
public class ExtensionCategoryLibrary extends ResourceBasedLibrary {
    @Inject
    ExtensionCategoryLibrary() {
        super(ExtensionCategoryLibrary.class.getResource("/.extension_categories"));

        loadResource(Paths.get(System.getProperty("user.home"), ".extension_categories"));
    }
}
