/*
 * Created on Dec 16, 2003
 * Version $Id$
 */
package com.pnehrer.rss.ui;

import org.eclipse.help.browser.IBrowserFactory;


public class BrowserFactoryDescriptor {

    private final String id;
    private final String name;
    private final IBrowserFactory factory;
    
    public BrowserFactoryDescriptor(
        String id, 
        String name, 
        IBrowserFactory factory) {

        this.id = id;
        this.name = name;
        this.factory = factory;
    }

    public IBrowserFactory getFactory() {
        return factory;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }
    
    public boolean equals(Object other) {
        if(other instanceof BrowserFactoryDescriptor)
            return id.equals(((BrowserFactoryDescriptor)other).id);
        else
            return false;
    }
    
    public int hashCode() {
        return id.hashCode();
    }
    
    public String toString() {
        return id;
    }
}