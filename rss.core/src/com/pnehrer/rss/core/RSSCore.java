/*
 * Created on Nov 5, 2003
 * Version $Id$
 */
package com.pnehrer.rss.core;

import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IAdapterManager;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Preferences;
import org.osgi.framework.BundleContext;
import org.w3c.dom.Document;

import com.pnehrer.rss.internal.core.ChannelAdapterFactory;
import com.pnehrer.rss.internal.core.ChannelManager;
import com.pnehrer.rss.internal.core.ResourceAdapterFactory;
import com.pnehrer.rss.internal.core.TranslatorManager;

/**
 * @author <a href="mailto:pnehrer@freeshell.org">Peter Nehrer</a>
 */
public class RSSCore extends Plugin {

    public static final String PLUGIN_ID = "com.pnehrer.rss.core";
    public static final String PREF_UPDATE_PERIODICALLY = "updatePeriodically";
    public static final String PREF_UPDATE_INTERVAL = "updateInterval";
    public static final String PREF_LOG_UPDATES = "logUpdates";
    public static final String MARKER_UPDATE = PLUGIN_ID + ".update";
    public static final String ATTR_LINK = "link";
    public static final int SEARCH_TITLE = 1;
    public static final int SEARCH_DESCRIPTION = 2;

    private static RSSCore instance;
    private ChannelManager channelManager;
    private TranslatorManager translatorManager;
    private ResourceAdapterFactory resourceAdapterFactory;
    private ChannelAdapterFactory channelAdapterFactory;

    public RSSCore() {
        instance = this;
    }

    public static RSSCore getPlugin() {
        return instance;
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.core.runtime.Plugin#initializeDefaultPluginPreferences()
     */
    protected void initializeDefaultPluginPreferences() {
        Preferences prefs = getPluginPreferences();
        prefs.setDefault(PREF_UPDATE_PERIODICALLY, true);
        prefs.setDefault(PREF_UPDATE_INTERVAL, 30);
        prefs.setDefault(PREF_LOG_UPDATES, true);
    }

    /* (non-Javadoc)
     * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
     */
    public void start(BundleContext context) throws Exception {
        super.start(context);
        translatorManager = new TranslatorManager();
        channelManager = new ChannelManager();
        IAdapterManager mgr = Platform.getAdapterManager();
        channelAdapterFactory = new ChannelAdapterFactory();
        mgr.registerAdapters(channelAdapterFactory, IFile.class);
        resourceAdapterFactory = new ResourceAdapterFactory();
        mgr.registerAdapters(resourceAdapterFactory, IRSSElement.class);
    }
    
    /* (non-Javadoc)
     * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
     */
    public void stop(BundleContext context) throws Exception {
        channelManager.cancelPendingTasks();
        IAdapterManager mgr = Platform.getAdapterManager();
        mgr.unregisterAdapters(resourceAdapterFactory);
        mgr.unregisterAdapters(channelAdapterFactory);
        super.stop(context);
    }
    
    public IRegisteredTranslator[] getTranslators(Document document)
        throws CoreException {

        return translatorManager.getTranslators(document);
    }
    
    public IChannel getChannel(IFile file) throws CoreException {
        return channelManager.getChannel(file);
    }
    
    public IChannel createChannel(
        IFile file, 
        IRegisteredTranslator translator, 
        Document document,
        URL url,
        Integer updateInterval,
        IProgressMonitor monitor) 
        throws CoreException {

        return channelManager.createChannel(
            file, 
            translator, 
            document,
            url,
            updateInterval,
            monitor);
    }
    
    public IItem[] search(
        String _term, 
        final boolean caseSensitive,
        final int fieldMask,
        Object[] workingSet,
        IProgressMonitor monitor) 
        throws CoreException {

        if(monitor != null)
            monitor.beginTask("Searching for " + _term, workingSet.length);
                    
        try {
            final String term = caseSensitive ? _term : _term.toLowerCase();
            final Set result = new HashSet();
            for(int i = 0; i < workingSet.length; ++i) {
                IRSSElement rssElement = (IRSSElement)
                    ((IAdaptable)workingSet[i]).getAdapter(IRSSElement.class);
                if(rssElement != null) {
                    collectMatches(
                        result,
                        term, 
                        caseSensitive, 
                        fieldMask, 
                        rssElement.getChannel());
    
                    continue;
                }
                
                IResource resource = (IResource)
                    ((IAdaptable)workingSet[i]).getAdapter(IResource.class);
                if(resource != null)
                    resource.accept(new IResourceVisitor() {
                        public boolean visit(IResource resource) throws CoreException {
                            if(resource.getType() == IResource.FILE) {
                                IRSSElement rssElement = (IRSSElement)
                                    ((IAdaptable)resource).getAdapter(IRSSElement.class);
    
                                if(rssElement != null)
                                    collectMatches(
                                        result,
                                        term, 
                                        caseSensitive, 
                                        fieldMask, 
                                        rssElement.getChannel());
    
                                return false;
                            }
                            else
                                return true;
                        }
                    });
                    
                if(monitor != null)
                    monitor.worked(1);
            }
        
            return (IItem[])result.toArray(new IItem[result.size()]);
        }
        finally {
            if(monitor != null)
                monitor.done();
        }
    }
    
    private void collectMatches(
        Set result, 
        String term, 
        boolean caseSensitive,
        int fieldMask, 
        IChannel channel) {
    
        IItem[] items = channel.getItems();
        for(int i = 0; i < items.length; ++i) {
            if((fieldMask & SEARCH_TITLE) != 0) {
                String title = items[i].getTitle();
                if(title != null) { 
                    if(!caseSensitive)
                        title = title.toLowerCase();

                    if(title.indexOf(term) >= 0) {
                        result.add(items[i]);
                        continue;
                    }
                }
            }

            if((fieldMask & SEARCH_DESCRIPTION) != 0) {
                String description = items[i].getTitle();
                if(description != null) { 
                    if(caseSensitive)
                        description = description.toLowerCase();

                    if(description.indexOf(term) >= 0)
                        result.add(items[i]);
                }
            }
        }
    }
}
