/*
 * Created on Nov 19, 2003
 * Version $Id$
 */
package com.pnehrer.rss.core;

/**
 * @author <a href="mailto:pnehrer@freeshell.org">Peter Nehrer</a>
 */
public final class SourceTranslatorDelegate {

    private final String id;
    private final String description;
    private final ISourceTranslator translator;
    
    SourceTranslatorDelegate(
        String id, 
        String description, 
        ISourceTranslator translator) { 
    
        this.id = id;
        this.description = description;
        this.translator = translator;
    }

    public String getId() {
        return id;
    }
    
    public String getDescription() {
        return description;
    }
    
    public ISourceTranslator getTranslator() {
        return translator;
    }
    
    public boolean equals(Object other) {
        if(other instanceof SourceTranslatorDelegate)
            return id.equals(((SourceTranslatorDelegate)other).id);
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
