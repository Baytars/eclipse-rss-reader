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

    private static RSSCore instance;
    
    private final ChannelManager channelManager = new ChannelManager();
    private Templates preprocessor;

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

    public void download(URL url, IFile file, IProgressMonitor monitor) 
        throws CoreException {
        
        if(monitor != null)
            monitor.beginTask("create", 2);
                
        try {        
            InputStream input = url.openStream();
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            
            Transformer transformer = preprocessor.newTransformer();
            transformer.setParameter(CHANNEL_URL, url.toExternalForm());
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
            ChannelBuilder builder = new ChannelBuilder();
            parse(file.getContents(), builder);
            return builder.getResult();
        }
        else return channel;
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResourceChangeListener#resourceChanged(org.eclipse.core.resources.IResourceChangeEvent)
     */
    public void resourceChanged(IResourceChangeEvent event) {
        IResource resource = event.getResource();
        if(event.getType() == IResourceChangeEvent.POST_CHANGE) {
            try {
                event.getDelta().accept(new ResourceDeltaVisitor());
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
                    Channel channel = channelManager.get(file);
                    parse(file.getContents(), new ChannelBuilder(channel));
                    channel.firePropertyChange();
                }
                
                return false;
            }            
            else return true;
        }
    }
}
