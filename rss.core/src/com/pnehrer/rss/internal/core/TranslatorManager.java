/*
 * Created on Nov 23, 2003
 * Version $Id$
 */
package com.pnehrer.rss.internal.core;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.Platform;
import org.w3c.dom.Document;

import com.pnehrer.rss.core.ITranslator;
import com.pnehrer.rss.core.RSSCore;

/**
 * @author <a href="mailto:pnehrer@freeshell.org">Peter Nehrer</a>
 */
public class TranslatorManager {
    
    private static final String TRANSLATOR_EXTENSION = "translator";
    private static final String TRANSLATOR_ELEMENT = "translator";
    private static final String CLASS_ATTR = "class";
    private static final String ID_ATTR = "id";
    private static final String DESCRIPTION_ATTR = "description";

    private static TranslatorManager instance;
    private final Map translators = new HashMap();
    private volatile boolean initialized;
    
    public TranslatorManager() {
        instance = this;
    }

    static TranslatorManager getInstance() {
        return instance;
    }

    private synchronized void loadTranslators() throws CoreException {
        if(!initialized) {
            IExtensionPoint ep = 
            	Platform.getExtensionRegistry().getExtensionPoint(
            			RSSCore.PLUGIN_ID, 
						TRANSLATOR_EXTENSION);
            if(ep != null) {
                IExtension[] extensions = ep.getExtensions();
                for(int i = 0, n = extensions.length; i < n; ++i) {
                    String prefix = extensions[i].getNamespace() + ".";
                    IConfigurationElement[] elements =
                        extensions[i].getConfigurationElements();
                    for(int j = 0, m = elements.length; j < m; ++j) {
                        if(!TRANSLATOR_ELEMENT.equals(elements[j].getName()))
                            continue;
                            
                        Object translator = 
                            elements[j].createExecutableExtension(CLASS_ATTR);
                        if(translator instanceof ITranslator) {
                            String id = prefix
                                + elements[j].getAttribute(ID_ATTR);
                            String description = 
                                elements[j].getAttribute(DESCRIPTION_ATTR);
                            RegisteredTranslator delegate =
                                new RegisteredTranslator(
                                    id,
                                    description == null ? id : description,
                                    (ITranslator)translator);
                                    
                            translators.put(id, delegate);
                        }
                    }
                }
            }
            
            initialized = true;
        }
    }
    
    public RegisteredTranslator getTranslator(String id) 
        throws CoreException {

        if(!initialized)
            loadTranslators();
            
        return (RegisteredTranslator)translators.get(id);
    }
    
    public RegisteredTranslator[] getTranslators(Document document)
        throws CoreException {

        if(!initialized)
            loadTranslators();

        Collection result = new HashSet();
        for(Iterator i = translators.values().iterator(); i.hasNext();) {
            RegisteredTranslator delegate = 
                (RegisteredTranslator)i.next();
            if(delegate.getTranslator().canTranslate(document))
                result.add(delegate);
        }
        
        return (RegisteredTranslator[])result.toArray(
            new RegisteredTranslator[result.size()]);
    }
}
