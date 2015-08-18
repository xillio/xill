package nl.xillio.xill.docgen.impl;

import nl.xillio.xill.docgen.DocumentationEntity;
import nl.xillio.xill.docgen.PropertiesProvider;

import java.util.*;

/**
 * This class represents the documentation of a package
 *
 * @author Thomas Biesaart
 * @author Ivor van der Hoog
 * @since 12-8-2015
 */
public class PackageDocumentationEntity implements DocumentationEntity {

	private String name;
	private final List<ConstructDocumentationEntity> children = new ArrayList<>();


	@Override
	public String getIdentity() {
		return name;
	}

	@Override
	public String getType() {
		return "package";
	}

	@Override
	public List<String> getTags() {
		return Collections.emptyList();
	}

	@Override
	public Map<String, Object> getProperties() {
		children.sort(DocumentationEntity.SORT_BY_IDENTITY);
		Map<String, Object> properties = new HashMap<>();
		properties.put("packageName", name);
		properties.put("constructs", PropertiesProvider.extractContent(children));
		return properties;
	}

	public void add(DocumentationEntity entity) {
		children.add((ConstructDocumentationEntity) entity);
	}

	public void setName(String name) {
		this.name = name;
	}
}
