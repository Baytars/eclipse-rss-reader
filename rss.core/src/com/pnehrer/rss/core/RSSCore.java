/*
 * Created on Nov 5, 2003
 * Version $Id$
 */
package com.pnehrer.rss.core;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IPluginDescriptor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Preferences;
import org.eclipse.core.runtime.Status;
import org.w3c.dom.Document;

import com.pnehrer.rss.core.internal.Channel;
import com.pnehrer.rss.core.internal.ChannelManager;

/**
 * @author <a href="mailto:pnehrer@freeshell.org">Peter Nehrer</a>
 */
public class RSSCore extends Plugin {

    public static final String PLUGIN_ID = "com.pnehrer.rss.core";
    private static final String TRANSLATOR_EXTENSION = "translator";
    private static final String TRANSLATOR_ELEMENT = "translator";
    private static final String CLASS_ATTR = "class";
    private static final String ID_ATTR = "id";
    private static final String DESCRIPTION_ATTR = "description";
    
    public static final String PREF_UPDATE_INTERVAL = "updateInterval";

    private static RSSCore instance;
    
    private final ChannelManager channelManager = new ChannelManager();
    private final Map translators = new HashMap();
    private volatile boolean translatorsLoaded;
    private boolean prefInit;

    /**
     * @param descriptor
     */
    public RSSCore(IPluginDescriptor descriptor) {
        super(descriptor);
        instance = this;
    }

    public static RSSCore getPlugin() {
        return instance;
    }
    
    private synchronized void loadTranslators() throws CoreException {
        if(!translatorsLoaded) { 
            IPluginDescriptor pd = getDescriptor();
            IExtensionPoint ep = pd.getExtensionPoint(TRANSLATOR_EXTENSION);
            if(ep != null) {
                IExtension[] extensions = ep.getExtensions();
                for(int i = 0, n = extensions.length; i < n; ++i) {
                    IConfigurationElement[] elements =
                        extensions[i].getConfigurationElements();
                    for(int j = 0, m = elements.length; j < m; ++j) {
                        if(!TRANSLATOR_ELEMENT.equals(elements[i].getName()))
                            continue;
                            
                        Object translator = 
                            elements[i].createExecutableExtension(CLASS_ATTR);
                        if(translator instanceof ISourceTranslator) {
                            String id = elements[i].getAttribute(ID_ATTR);
                            String description = 
                                elements[i].getAttribute(DESCRIPTION_ATTR);
                            SourceTranslatorDelegate delegate =
                                new SourceTranslatorDelegate(
                                    id,
                                    description == null ? id : description,
                                    (ISourceTranslator)translator);
                                    
                            translators.put(id, delegate);
                        }
                    }
                }
            }
            
            translatorsLoaded = true;
        }
    }
    
    public SourceTranslatorDelegate[] getTranslators(Document document)
        throws CoreException {

        if(!translatorsLoaded)
            loadTranslators();

        Collection result = new HashSet();
        for(Iterator i = translators.values().iterator(); i.hasNext();) {
            SourceTranslatorDelegate delegate = 
                (SourceTranslatorDelegate)i.next();
            if(delegate.getTranslator().canTranslate(document))
                result.add(delegate);
        }
        
        return (SourceTranslatorDelegate[])result.toArray(
            new SourceTranslatorDelegate[result.size()]);
    }
    
    public SourceTranslatorDelegate getTranslator(String id) 
        throws CoreException {
            
        if(!translatorsLoaded)
            loadTranslators();

        return (SourceTranslatorDelegate)translators.get(id);
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.core.runtime.Plugin#initializeDefaultPluginPreferences()
     */
    protected void initializeDefaultPluginPreferences() {
        if(!prefInit) {
            prefInit = true;
            Preferences prefs = getPluginPreferences();
            prefs.setDefault(PREF_UPDATE_INTERVAL, 30);
        }
    }
    
    public IChannel create(IFile file) throws CoreException {
        Channel channel = channelManager.get(file);
        if(channel == null) {
            channel = Channel.load(file);
            channelManager.add(channel);
        }
        
        return channel;
    }
    
    public IChannel newChannel(IFile file, Document document) 
        throws CoreException {
            
        if(channelManager.get(file) != null) {
            throw new CoreException(
                new Status(
                    IStatus.ERROR,
                    PLUGIN_ID,
                    0,
                    "channel already exists",
                    null));
        }
        else {
            Channel channel = Channel.create(file, document);
            channelManager.add(channel);
            return channel;
        }
    }
}
