/*
 * Created on Nov 19, 2003
 * Version $Id$
 */
package com.pnehrer.rss.internal.core;

import org.eclipse.core.runtime.CoreException;
import org.w3c.dom.Document;

import com.pnehrer.rss.core.IRegisteredTranslator;
import com.pnehrer.rss.core.ITranslator;

/**
 * @author <a href="mailto:pnehrer@freeshell.org">Peter Nehrer</a>
 */
public class RegisteredTranslator implements IRegisteredTranslator {

    private final String id;
    private final String description;
    private final ITranslator translator;
    
    RegisteredTranslator(
        String id, 
        String description, 
        ITranslator translator) { 
    
        this.id = id;
        this.description = description;
        this.translator = translator;
    }

    /* (non-Javadoc)
     * @see com.pnehrer.rss.core.ITranslator#canTranslate(org.w3c.dom.Document)
     */
    public boolean canTranslate(Document document) {
        return translator.canTranslate(document);
    }

    /* (non-Javadoc)
     * @see com.pnehrer.rss.core.ITranslator#translate(org.w3c.dom.Document)
     */
    public Document translate(Document document) throws CoreException {
        return translator.translate(document);
    }

    public String getId() {
        return id;
    }
    
    public String getDescription() {
        return description;
    }
    
    public ITranslator getTranslator() {
        return translator;
    }
    
    public boolean equals(Object other) {
        if(other instanceof RegisteredTranslator)
            return id.equals(((RegisteredTranslator)other).id);
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
