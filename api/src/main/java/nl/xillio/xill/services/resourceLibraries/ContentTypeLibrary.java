package nl.xillio.xill.services.resourceLibraries;


import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.nio.file.Paths;

@Singleton
public class ContentTypeLibrary extends ResourceBasedLibrary {
    @Inject
    public ContentTypeLibrary() {
        super(ContentTypeLibrary.class.getResource("/.mimetypes"));

        loadResource(Paths.get(System.getProperty("user.home"), ".mimetypes"));
    }
}
