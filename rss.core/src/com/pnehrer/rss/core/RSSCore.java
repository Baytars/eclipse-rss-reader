/*
 * Created on Nov 5, 2003
 * Version $Id$
 */
package com.pnehrer.rss.core;

import java.net.URL;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdapterManager;
import org.eclipse.core.runtime.IPluginDescriptor;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Preferences;
import org.w3c.dom.Document;

import com.pnehrer.rss.core.internal.ChannelAdapterFactory;
import com.pnehrer.rss.core.internal.ChannelManager;
import com.pnehrer.rss.core.internal.ResourceAdapterFactory;
import com.pnehrer.rss.core.internal.TranslatorManager;

/**
 * @author <a href="mailto:pnehrer@freeshell.org">Peter Nehrer</a>
 */
public class RSSCore extends Plugin {

    public static final String PLUGIN_ID = "com.pnehrer.rss.core";
    public static final String PREF_UPDATE_PERIODICALLY = "updatePeriodically";
    public static final String PREF_UPDATE_INTERVAL = "updateInterval";

    private static RSSCore instance;
    private ChannelManager channelManager;
    private TranslatorManager translatorManager;
    private ResourceAdapterFactory resourceAdapterFactory;
    private ChannelAdapterFactory channelAdapterFactory;
    
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
    
    /* (non-Javadoc)
     * @see org.eclipse.core.runtime.Plugin#initializeDefaultPluginPreferences()
     */
    protected void initializeDefaultPluginPreferences() {
        if(!prefInit) {
            prefInit = true;
            Preferences prefs = getPluginPreferences();
            prefs.setDefault(PREF_UPDATE_PERIODICALLY, true);
            prefs.setDefault(PREF_UPDATE_INTERVAL, 30);
        }
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.runtime.Plugin#startup()
     */
    public void startup() throws CoreException {
        super.startup();
        translatorManager = new TranslatorManager();
        channelManager = new ChannelManager();
        IAdapterManager mgr = Platform.getAdapterManager();
        channelAdapterFactory = new ChannelAdapterFactory();
        mgr.registerAdapters(channelAdapterFactory, IFile.class);
        resourceAdapterFactory = new ResourceAdapterFactory();
        mgr.registerAdapters(resourceAdapterFactory, IRSSElement.class);
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.core.runtime.Plugin#shutdown()
     */
    public void shutdown() throws CoreException {
        channelManager.cancelPendingTasks();
        IAdapterManager mgr = Platform.getAdapterManager();
        mgr.unregisterAdapters(resourceAdapterFactory);
        mgr.unregisterAdapters(channelAdapterFactory);
        super.shutdown();
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

    public void addChannelChangeListener(IChannelChangeListener listener) {
        channelManager.addChannelChangeListener(listener);
    }

    public void removeChannelChangeListener(IChannelChangeListener listener) {
        channelManager.removeChannelChangeListener(listener);
    }
}
