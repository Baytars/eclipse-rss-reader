package com.pnehrer.tools.morphine.ant.types;

import java.util.*;

import org.apache.tools.ant.types.*;

public class DataTypeWithProperties extends DataType {

    protected List props;

    public DataTypeWithProperties() {
        props = new ArrayList();
    }

    public Object createProperty() {
        NameValuePair param = new NameValuePair();
        props.add(param);
        return param;
    }

    public String getProperty(String name) {
        if(name == null) return null;

        for(Iterator i = props.iterator(); i.hasNext();) {
            NameValuePair item = (NameValuePair)i.next();
            if(name.equals(item.getName())) return item.getValue();
        }

        return null;
    }
}
