package nl.xillio.xill.plugins.document.services;

import java.util.HashMap;
import java.util.Map;

import nl.xillio.udm.builders.DecoratorBuilder;
import nl.xillio.udm.builders.DocumentRevisionBuilder;

public class ConversionServiceImpl implements ConversionService {

	// @Override
	// public Map<String, Map<String, Object>> udmToMap(List<Decorator> decorators) {
	// Map<String, Map<String, Object>> result = new HashMap<>();
	//
	// for (Decorator decorator : decorators){
	// Map<String, Object> fields = new HashMap<>();
	// for (Field field : decorator.getFields()){
	// fields.put(field.getName(), field.getValue());
	// }
	// result.put(decorator.getName(), fields);
	// }
	//
	// return result;
	// }
	//
	// @Override
	// public List<Decorator> mapToUdm(Map<String, Map<String, Object>> object) {
	// List<Decorator> decorators = new ArrayList<>();
	// for (Entry<String, Map<String, Object>> decoratorEntry : object.entrySet()) {
	// Decorator decorator = new Decorator();
	// decorator.setName(decoratorEntry.getKey());
	// List<Field> fields = decorator.getFields();
	// for (Entry<String, Object> fieldEntry : decoratorEntry.getValue().entrySet()) {
	// Field field = new Field();
	// field.setName(fieldEntry.getKey());
	// field.setValue(fieldEntry.getValue());
	// fields.add(field);
	// }
	// decorators.add(decorator);
	// }
	//
	// return decorators;
	// }

	@Override
	public void mapToUdm(Map<String, Map<String, Object>> object, DocumentRevisionBuilder builder) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Map<String, Map<String, Object>> udmToMap(DocumentRevisionBuilder builder) {
		Map<String, Map<String, Object>> result = new HashMap<>();
		
		// Get all decorators from the document.
		for (String decName : builder.decorators()) {
			DecoratorBuilder decorator = builder.decorator(decName);
			
			// Get all fields from the decorator.
			Map<String, Object> fields = new HashMap<>();
			for (String fieldName : decorator.fields()) {
				fields.put(fieldName, decorator.field(fieldName));
			}
			
			result.put(decName, fields);
		}
		
		return result;
	}
}
