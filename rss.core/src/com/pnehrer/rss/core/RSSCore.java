/*
 * Created on Nov 5, 2003
 * Version $Id$
 */
package com.pnehrer.rss.core;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.DateFormat;
import java.util.Date;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPluginDescriptor;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Preferences;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.xml.sax.SAXException;

import com.pnehrer.rss.core.internal.Channel;
import com.pnehrer.rss.core.internal.ChannelManager;
import com.pnehrer.rss.core.internal.ChannelBuilder;

/**
 * @author <a href="mailto:pnehrer@freeshell.org">Peter Nehrer</a>
 */
public class RSSCore extends Plugin implements IResourceChangeListener {

    public static final String PLUGIN_ID = "com.pnehrer.rss.core";
    private static final String PREPROCESSOR = "internalize.xsl";
    private static final String CHANNEL_URL = "channelURL";
    
    public static final String PREF_UPDATE_INTERVAL = "updateInterval";
    public static final QualifiedName PROP_LAST_UPDATED = 
        new QualifiedName(PLUGIN_ID, "lastUpdated");

    private static RSSCore instance;
    
    private final ChannelManager channelManager = new ChannelManager();
    private Templates preprocessor;
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
     * @see org.eclipse.core.runtime.Plugin#startup()
     */
    public void startup() throws CoreException {
        super.startup();

        TransformerFactory factory = TransformerFactory.newInstance();
        try {
            preprocessor = factory.newTemplates(
                new StreamSource(
                    RSSCore.getPlugin().openStream(
                        new Path(PREPROCESSOR))));
        }
        catch(TransformerConfigurationException ex) {
            throw new CoreException(
                new Status(
                    IStatus.ERROR,
                    PLUGIN_ID,
                    0,
                    "could not initialize preprocessor",
                    ex));
        }
        catch(IOException ex) {
            throw new CoreException(
                new Status(
                    IStatus.ERROR,
                    PLUGIN_ID,
                    0,
                    "could not initialize preprocessor",
                    ex));
        }
        
        ResourcesPlugin.getWorkspace().addResourceChangeListener(this);
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.core.runtime.Plugin#shutdown()
     */
    public void shutdown() throws CoreException {
        ResourcesPlugin.getWorkspace().removeResourceChangeListener(this);
        super.shutdown();
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

    public void download(
        URL url, 
        IFile file, 
        IProgressMonitor monitor) 
        throws CoreException {
        
        if(monitor != null)
            monitor.beginTask("download", 2);
                
        try {        
            InputStream input = url.openStream();
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            
            Transformer transformer = preprocessor.newTransformer();
            transformer.setParameter(CHANNEL_URL, url.toExternalForm());
            Channel channel = channelManager.get(file);
            Integer updateInterval = channel == null ?
                new Integer(getPluginPreferences().getInt(PREF_UPDATE_INTERVAL)) :
                channel.getUpdateInterval();
            transformer.setParameter(PREF_UPDATE_INTERVAL, updateInterval);
            transformer.transform(
                new StreamSource(input),
                new StreamResult(output));

            if(monitor != null) 
                monitor.worked(1);

            input = new ByteArrayInputStream(output.toByteArray());        
            SubProgressMonitor subMonitor = monitor == null ?
                null :
                new SubProgressMonitor(monitor, 1);
                
            if(file.exists()) file.setContents(input,true, true, subMonitor);
            else file.create(input, true, subMonitor);

            file.setPersistentProperty(
                PROP_LAST_UPDATED, 
                DateFormat.getInstance().format(new Date()));
        }
        catch(TransformerException ex) {
            throw new CoreException(
                new Status(
                    IStatus.ERROR,
                    PLUGIN_ID,
                    0,
                    "could not parse external channel source",
                    ex));
        }
        catch(IOException ex) {
            throw new CoreException(
                new Status(
                    IStatus.ERROR,
                    PLUGIN_ID,
                    0,
                    "could not parse external channel source",
                    ex));
        }
        finally {
            if(monitor != null)
                monitor.done();
        }
    }
    
    private void parse(InputStream input, ChannelBuilder builder) 
        throws CoreException {
            
        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setNamespaceAware(true);
        try {
            SAXParser parser = factory.newSAXParser();
            parser.parse(input, builder);
        }
        catch(ParserConfigurationException ex) {
            throw new CoreException(
                new Status(
                    IStatus.ERROR,
                    PLUGIN_ID,
                    0,
                    "could not parse internal channel source",
                    ex));
        }
        catch(SAXException ex) {
            throw new CoreException(
                new Status(
                    IStatus.ERROR,
                    PLUGIN_ID,
                    0,
                    "could not parse internal channel source",
                    ex));
        } 
        catch(IOException ex) {
            throw new CoreException(
                new Status(
                    IStatus.ERROR,
                    PLUGIN_ID,
                    0,
                    "could not parse internal channel source",
                    ex));
        }
    }
    
    public IChannel create(IFile file) throws CoreException {
        Channel channel = channelManager.get(file);
        if(channel == null) {
            ChannelBuilder builder = new ChannelBuilder(file);
            parse(file.getContents(), builder);
            channel = builder.getResult();
            channelManager.add(channel);
        }
        
        return channel;
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResourceChangeListener#resourceChanged(org.eclipse.core.resources.IResourceChangeEvent)
     */
    public void resourceChanged(IResourceChangeEvent event) {
        IResource resource = event.getResource();
        if(event.getType() == IResourceChangeEvent.POST_CHANGE) {
            try {
                if(event.getDelta() == null) {
                    if(resource instanceof IFile)
                        channelSourceChanged((IFile)resource);
                }
                else {
                    event.getDelta().accept(new ResourceDeltaVisitor());
                }
            }
            catch(CoreException ex) {
                getLog().log(
                    new Status(
                        IStatus.ERROR,
                        PLUGIN_ID,
                        0,
                        "could not process resource changes",
                        ex));
            }
        }
        else {
            channelManager.remove((IProject)event.getResource());
        }
    }
    
    private void channelSourceChanged(IFile file) throws CoreException {
        Channel channel = channelManager.get(file);
        if(channel != null) {
            parse(file.getContents(), new ChannelBuilder(channel));
            channel.firePropertyChange();
        }
    }
    
    private class ResourceDeltaVisitor implements IResourceDeltaVisitor {

        /* (non-Javadoc)
         * @see org.eclipse.core.resources.IResourceDeltaVisitor#visit(org.eclipse.core.resources.IResourceDelta)
         */
        public boolean visit(IResourceDelta delta) throws CoreException {
            IResource resource = delta.getResource();
            if(resource instanceof IFile) {
                IFile file = (IFile)resource;
                if(delta.getKind() == IResourceDelta.REMOVED) {
                    channelManager.remove(file);
                }
                else if(delta.getKind() == IResourceDelta.CHANGED) {
                    channelSourceChanged(file);
                }
                
                return false;
            }            
            else return true;
        }
    }
}
