package nl.xillio.xill.docgen.impl;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import nl.xillio.xill.docgen.DocumentationEntity;
import nl.xillio.xill.docgen.DocumentationGenerator;
import nl.xillio.xill.docgen.exceptions.ParsingException;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

/**
 * This is the FreeMarker implementation of the {@link DocumentationGenerator}
 *
 * @author Thomas Biesaart
 * @author Ivor van der Hoog
 * @since 12-8-2015
 */
public class FreeMarkerDocumentationGenerator implements DocumentationGenerator {
    private final String packageName;
    private final Configuration fmConfig;
    private boolean isClosed;
    private final File packageFolder;

    public FreeMarkerDocumentationGenerator(String collectionIdentity, Configuration fmConfig, File documentationFolder) {
        packageName = collectionIdentity;
        this.fmConfig = fmConfig;
        this.packageFolder = new File(documentationFolder, collectionIdentity);
    }

    @Override
    public void generate(DocumentationEntity entity) throws ParsingException, IllegalStateException {
        if(isClosed) {
            throw new IllegalStateException("This generator has already been closed");
        }

        //Get template
        Template template = getTemplate(entity.getType());
        Map<String, Object> model = getModel(entity);

        //Prepare target
        File target = new File(packageFolder, entity.getIdentity() + ".html");

        try {
            doGenerate(template, target, model);
        } catch (IOException e) {
            throw new ParsingException("Failed to write to " + target.getAbsolutePath(), e);
        } catch (TemplateException e) {
            throw new ParsingException("Error in template " + template.getName(), e);
        }
    }

    void doGenerate(Template template, File target, Map<String, Object> model) throws IOException, TemplateException {
        //Make sure the target file exists
        FileUtils.touch(target);

        //Generate
        FileWriter writer = new FileWriter(target);
        template.process(model, writer);
    }

    private Map<String, Object> getModel(DocumentationEntity entity) {
        Map<String, Object> model = entity.getProperties();
        model.put("packageName", packageName);
        return model;
    }

    private Template getTemplate(String name) throws ParsingException {
        try {
            return fmConfig.getTemplate(name + ".html");
        } catch (IOException e) {
            throw new ParsingException("Failed to get template", e);
        }
    }

    @Override
    public void close() throws Exception {
        isClosed = true;
    }
}
