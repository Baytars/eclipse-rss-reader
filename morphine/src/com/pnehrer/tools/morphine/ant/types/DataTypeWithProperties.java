package com.pnehrer.tools.morphine.ant.types;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.tools.ant.types.DataType;

public class DataTypeWithProperties extends DataType {

	protected class NameValuePair {
	
	    private String name;
	    private String value;
	
	    public String getName() {
	        return name;
	    }
	
	    public void setName(String name) {
	    	if(name != null) throw new IllegalStateException("name has already been set");
	    	
	        this.name = name;
	        updatePropertyName(this);
	    }
	
	    public String getValue() {
	        return value;
	    }
	
	    public void setValue(String value) {
	        this.value = value;
	    }
	
	    public boolean equals(Object other) {
	        if(other == this) return true;
	        else if(other instanceof NameValuePair) {
	            NameValuePair obj = (NameValuePair)other;
	            return (name == null ? obj.getName() == null : name.equals(obj.getName()))
	                && (value == null ? obj.getValue() == null : value.equals(obj.getValue()));
	        }
	        else return false;
	    }
	
	    public int hashCode() {
	        int code = 17;
	        code = 37 * code + (name == null ? 0 : name.hashCode());
	        code = 37 * code + (value == null ? 0 : value.hashCode());
	        return code;
	    }
	}

    private List newProps;
    protected Map properties;    

    public DataTypeWithProperties() {
        newProps = new ArrayList();
        properties = new HashMap();
    }

    public Object createProperty() {
        NameValuePair param = new NameValuePair();
        newProps.add(param);
        return param;
    }

    public String getProperty(String name) {
        if(name == null) return null;
        else return (String)properties.get(name);
    }
    
    private void updatePropertyName(NameValuePair obj) {
    	properties.put(obj.getName(), obj);
    	newProps.remove(obj);
    }
}
