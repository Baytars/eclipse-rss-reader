package com.pnehrer.tools.morphine.ant.types;

public class NameValuePair {

    private String name;
    private String value;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
