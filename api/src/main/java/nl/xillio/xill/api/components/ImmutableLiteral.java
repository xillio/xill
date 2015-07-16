package nl.xillio.xill.api.components;

import java.util.ArrayList;
import java.util.Collection;

/**
 * This class represents a literal in the xill language
 */
public class ImmutableLiteral extends MetaExpression {

    private Expression value;

    /**
     * @param value the value to set
     */
    public ImmutableLiteral(Expression value) {
	this.value = value;
    }
    
    @Override
    public Number getNumberValue() {
	return value.getNumberValue();
    }

    @Override
    public String getStringValue() {
	return value.getStringValue();
    }

    @Override
    public boolean getBooleanValue() {
	return value.getBooleanValue();
    }

    @Override
    public boolean isNull() {
	return value.isNull();
    }

    @Override
    public Collection<Processable> getChildren() {
	return new ArrayList<>();
    }
    
    @Override
    public void close() throws Exception {
	//Stub to prevent closing of literals
	
	//Clear out the meta pool
	closeMetaPool();
	
	//Make sure the reference count doesn't drop below 0
	resetReferences();
    }
}
