/*
 * Created on Nov 11, 2003
 * Version $Id$
 */
package com.pnehrer.rss.internal.core;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TimerTask;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.xml.serialize.Method;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.Serializer;
import org.apache.xml.serialize.SerializerFactory;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.pnehrer.rss.core.ChannelChangeEvent;
import com.pnehrer.rss.core.IChannel;
import com.pnehrer.rss.core.IImage;
import com.pnehrer.rss.core.IItem;
import com.pnehrer.rss.core.IRegisteredTranslator;
import com.pnehrer.rss.core.ITextInput;
import com.pnehrer.rss.core.RSSCore;

/**
 * @author <a href="mailto:pnehrer@freeshell.org">Peter Nehrer</a>
 */
public class Channel 
    extends PlatformObject 
    implements IChannel, IResourceChangeListener {

    private static final String CHANNEL = "channel";
    private static final String TRANSLATOR_ID = "translatorId";
    private static final String URL = "url";
    private static final String UPDATE_INTERVAL = "updateInterval";
    private static final String TITLE = "title";
    private static final String DESCRIPTION = "description";
    private static final String LINK = "link";
    private static final String DATE = "date";
    private static final String LAST_UPDATED = "lastUpdated";
    private static final String IMAGE = "image";
    private static final String ITEM = "item";
    private static final String TEXT_INPUT = "textInput";

    private UpdateTask updateTask;
    private final Object updateTaskLock = new Object();

    private IFile file;
    private IRegisteredTranslator translator;
    private URL url;
    private Integer updateInterval;
    private String title;
    private String link;
    private String description;
    private Date date;
    private Image image;
    private final List items = new ArrayList();
    private TextInput textInput;
    
    private boolean suppressChangeEvents;
    private long selfModificationStamp = IFile.NULL_STAMP;

    private Channel(IFile file) {
        this.file = file;
        activate();
    }
    
    private Channel(IFile file, IRegisteredTranslator translator) {
        this.file = file;
        this.translator = translator;
        activate();
    }
    
    private void activate() {
        ResourcesPlugin.getWorkspace().addResourceChangeListener(this);
    }
    
    private void passivate() {
        if(updateTask != null)
            updateTask.cancel();
            
        ResourcesPlugin.getWorkspace().removeResourceChangeListener(this);
        ChannelManager.getInstance().removeChannel(this);
        firePropertyChange(ChannelChangeEvent.REMOVED);
    }

    /* (non-Javadoc)
     * @see com.pnehrer.rss.core.IChannel#getFile()
     */
    public IFile getFile() {
        return file;
    }
    
    public IRegisteredTranslator getTranslator() {
        return translator;
    }
    
    public synchronized void setTranslator(IRegisteredTranslator translator) {
        this.translator = translator;
        firePropertyChange(ChannelChangeEvent.CHANGED);
    }
    
    /* (non-Javadoc)
     * @see com.pnehrer.rss.core.IChannel#getURL()
     */
    public URL getURL() {
        return url;
    }
    
    public synchronized void setURL(URL url) {
        this.url = url;
        firePropertyChange(ChannelChangeEvent.CHANGED);
    }
    
    /* (non-Javadoc)
     * @see com.pnehrer.rss.core.IChannel#getUpdateInterval()
     */
    public Integer getUpdateInterval() {
        return updateInterval;
    }
    
    /* (non-Javadoc)
     * @see com.pnehrer.rss.core.IChannel#setUpdateInterval(java.lang.Integer)
     */
    public synchronized void setUpdateInterval(Integer updateInterval) {
        this.updateInterval = updateInterval;
        updateUpdateSchedule();
        firePropertyChange(ChannelChangeEvent.CHANGED);
    }
    
    /* (non-Javadoc)
     * @see com.pnehrer.rss.core.IChannel#getLastUpdated()
     */
    public Date getLastUpdated() {
        File cache = getCache();
        return cache.isFile() ? new Date(cache.lastModified()) : null;
    }
    
    /* (non-Javadoc)
     * @see com.pnehrer.rss.core.IRSSElement#getChannel()
     */
    public IChannel getChannel() {
        return this;
    }
    
    /* (non-Javadoc)
     * @see com.pnehrer.rss.core.IChannel#getTitle()
     */
    public String getTitle() {
        return title;
    }
    
    private void setTitle(String title) {
        this.title = title;
    }

    /* (non-Javadoc)
     * @see com.pnehrer.rss.core.IChannel#getLink()
     */
    public String getLink() {
        return link;
    }
    
    private void setLink(String link) {
        this.link = link;
    }

    /* (non-Javadoc)
     * @see com.pnehrer.rss.core.IChannel#getDescription()
     */
    public String getDescription() {
        return description;
    }
    
    private void setDescription(String description) {
        this.description = description;
    }

    /* (non-Javadoc)
     * @see com.pnehrer.rss.core.IChannel#getDate()
     */
    public Date getDate() {
        return date;
    }
    
    private void setDate(Date date) {
        this.date = date;
    }

    /* (non-Javadoc)
     * @see com.pnehrer.rss.core.IChannel#getImage()
     */
    public IImage getImage() {
        return image;
    }

    /* (non-Javadoc)
     * @see com.pnehrer.rss.core.IChannel#getItems()
     */
    public IItem[] getItems() {
        return (IItem[])items.toArray(new IItem[items.size()]);
    }
    
    /* (non-Javadoc)
     * @see com.pnehrer.rss.core.IChannel#getTextInput()
     */
    public ITextInput getTextInput() {
        return textInput;
    }
    
    private void firePropertyChange(int flags) {
        if(suppressChangeEvents)
            return;

        ChannelManager.getInstance().firePropertyChange(this, flags);            
    }
    
    public void update(IProgressMonitor monitor) throws CoreException {
        if(translator != null && url != null) {
            DocumentBuilderFactory factory = 
                DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            
            if(monitor != null)
                monitor.beginTask("update", 2);
            
            try {
                DocumentBuilder builder = factory.newDocumentBuilder();
                Document document = builder.parse(url.openStream());
                
                if(monitor != null)
                    monitor.worked(1);
                
                update(
                    document, 
                    monitor == null ?
                        null :
                        new SubProgressMonitor(monitor, 1));
            }
            catch(ParserConfigurationException ex) {
                throwCouldNotParseChannelSourceException(ex);
            }
            catch(SAXException ex) {
                throwCouldNotParseChannelSourceException(ex);
            }
            catch(IOException ex) {
                throwCouldNotParseChannelSourceException(ex);
            }
            finally {
                if(monitor != null)
                    monitor.done();
            }
        }
    }
    
    private void throwCouldNotParseChannelSourceException(Throwable t) 
        throws CoreException {
            
        throw new CoreException(
            new Status(
                IStatus.ERROR,
                RSSCore.PLUGIN_ID,
                0,
                "could not parse channel source",
                t));
    }
    
    private synchronized void update(
        Document sourceDocument, 
        IProgressMonitor monitor) 
        throws CoreException {
            
        if(monitor != null)
            monitor.beginTask("update", 3);

        try {
            Document document = translator.translate(sourceDocument);

            File cache = getCache();
            File parent = cache.getParentFile();
            if(!parent.exists())
                parent.mkdirs();
            
            SerializerFactory factory = 
                SerializerFactory.getSerializerFactory(Method.XML);
            try {
                Serializer serializer = 
                    factory.makeSerializer(
                        new FileWriter(getCache()),
                        new OutputFormat(document));
                serializer.asDOMSerializer().serialize(document);
                updateUpdateSchedule();
            }
            catch(IOException ex) {
                throw new CoreException(
                    new Status(
                        IStatus.ERROR,
                        RSSCore.PLUGIN_ID,
                        0,
                        "could not cache channel contents",
                        ex));   
            }
            
            if(monitor != null)
                monitor.worked(1);
            
            Element channel = document.getDocumentElement();
            if(CHANNEL.equals(channel.getLocalName())) {
                update(channel);
                
                if(monitor != null)
                    monitor.worked(1);
            }
            else {
                throw new CoreException(
                    new Status(
                        IStatus.ERROR,
                        RSSCore.PLUGIN_ID,
                        0,
                        "invalid channel format",
                        null));
            }
        }
        finally {
            if(monitor != null)
                monitor.done();
        }
    }
    
    private synchronized void loadProperties() throws CoreException {                
        Properties props = new Properties();
        if(file.exists()) {
            try {
                props.load(file.getContents());
            }
            catch(IOException ex) {
                throw new CoreException(
                    new Status(
                        IStatus.ERROR,
                        RSSCore.PLUGIN_ID,
                        0,
                        "could not load channel properties from file " + file,
                        null));
            }
        }

        do {
            String str = props.getProperty(TRANSLATOR_ID);
            if(str == null)
                break;

            IRegisteredTranslator matchingTranslator = translator;
            if(translator == null || !str.equals(translator.getId())) 
                matchingTranslator = 
                    TranslatorManager.getInstance().getTranslator(str); 

            if(matchingTranslator == null)
                throw new CoreException(
                    new Status(
                        IStatus.ERROR,
                        RSSCore.PLUGIN_ID,
                        0,
                        "could not find source translator with id " + str,
                        null));

            suppressChangeEvents = true;
            try {
                setTranslator(matchingTranslator);
    
                str = props.getProperty(URL);
                if(str == null)
                    break;
    
                try {
                    setURL(new URL(str));
                }
                catch(MalformedURLException ex) {
                    throw new CoreException(
                        new Status(
                            IStatus.ERROR,
                            RSSCore.PLUGIN_ID,
                            0,
                            "invalid channel url " + str,
                            ex));
                }
    
                str = props.getProperty(UPDATE_INTERVAL);
                setUpdateInterval(str == null ? null : new Integer(str));
                return;
            }
            finally {
                suppressChangeEvents = false;
                firePropertyChange(ChannelChangeEvent.CHANGED);
            }
        }
        while(false);

        throw new CoreException(
            new Status(
                IStatus.ERROR,
                RSSCore.PLUGIN_ID,
                0,
                "invalid channel file",
                null));
    }

    private void throwCouldNotParseChannelFileException(Throwable t)
        throws CoreException {
        
        throw new CoreException(
            new Status(
                IStatus.ERROR,
                RSSCore.PLUGIN_ID,
                0,
                "could not parse channel file",
                t));
    }
    
    private File getCache() {
        return file
            .getProject()
            .getPluginWorkingLocation(RSSCore.getPlugin().getDescriptor())
            .append(file.getProjectRelativePath())
            .toFile();
    }

    private synchronized void loadContents() throws CoreException {
        File cache = getCache(); 
        if(cache.isFile()) {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            Document document = null; 
            try {
                DocumentBuilder builder = factory.newDocumentBuilder();
                document = builder.parse(new FileInputStream(cache));
            }
            catch(ParserConfigurationException ex) {
                throwCouldNotParseChannelFileException(ex);
            }
            catch(SAXException ex) {
                throwCouldNotParseChannelFileException(ex);
            }
            catch(IOException ex) {
                throwCouldNotParseChannelFileException(ex);
            }
        
            Element channel = document.getDocumentElement();
            if(CHANNEL.equals(channel.getLocalName()))
                update(channel);
        }
    }
    
    private void update(Element channel) throws CoreException {
        setTitle(channel.getAttribute(TITLE));
        setLink(channel.getAttribute(LINK));
        setDescription(channel.getAttribute(DESCRIPTION));
        String dateStr = channel.getAttribute(DATE); 
        setDate(
            dateStr == null || dateStr.trim().length() == 0 ? 
                null : 
                parseDate(dateStr));

        boolean hasImage = false;
        boolean hasTextInput = false;
        Collection liveItems = new HashSet();
        Map itemMap = new HashMap(items.size());
        for(Iterator iter = items.iterator(); iter.hasNext();) {
            Item item = (Item)iter.next();
            itemMap.put(item.getLink(), item);
        }

        NodeList children = channel.getChildNodes();
        int itemIndex = 0;
        for(int i = 0, n = children.getLength(); i < n; ++i) {
            Node node = children.item(i);
            if(node.getNodeType() == Node.ELEMENT_NODE) {
                if(IMAGE.equals(node.getLocalName())) {
                    Object oldImage = image;
                    if(image == null)
                        image = new Image(this);
                    
                    image.update((Element)node);
                    hasImage = true;
                }
                else if(ITEM.equals(node.getLocalName())) {
                    String itemLink = ((Element)node).getAttribute(Item.LINK);
                    Item item = (Item)itemMap.get(itemLink);
                    if(item == null) {
                        item = new Item(this);
                        items.add(itemIndex, item);
                    }
                    
                    item.update((Element)node);
                    liveItems.add(item);
                    ++itemIndex;
                }
                else if(TEXT_INPUT.equals(node.getLocalName())) {
                    Object oldTextInput = textInput;
                    if(textInput == null)
                        textInput = new TextInput(this);
                        
                    textInput.update((Element)node);
                    hasTextInput = true;
                }
            }
        }

        items.retainAll(liveItems);
    }
    
    public synchronized void save(IProgressMonitor monitor) 
        throws CoreException {

        Properties props = new Properties();
        props.setProperty(TRANSLATOR_ID, translator.getId());
        props.setProperty(URL, url.toExternalForm());
        if(updateInterval != null)
            props.setProperty(UPDATE_INTERVAL, updateInterval.toString());
            
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            props.store(out, null);
        }
        catch(IOException ex) {
            throw new CoreException(
                new Status(
                    IStatus.ERROR,
                    RSSCore.PLUGIN_ID,
                    0,
                    "could not save channel properties",
                    ex));
        }
        
        ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
        if(file.exists())
            file.setContents(in, false, true, monitor);
        else
            file.create(in, false, monitor);

        selfModificationStamp = file.getModificationStamp();
    }
    
    private void updateUpdateSchedule() {
        synchronized(updateTaskLock) {
            if(updateTask != null)
                updateTask.cancel();

            if(updateInterval != null) {
                updateTask = new UpdateTask();
                ChannelManager.getInstance().scheduleTask(
                    updateTask,
                    getLastUpdated(), 
                    updateInterval.intValue());
            }
        }
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResourceChangeListener#resourceChanged(org.eclipse.core.resources.IResourceChangeEvent)
     */
    public void resourceChanged(IResourceChangeEvent event) {
        switch(event.getType()) {
            case IResourceChangeEvent.PRE_CLOSE:
            case IResourceChangeEvent.PRE_DELETE:
                if(file.getProject().equals(event.getResource()))
                    passivate();

                break;
                
            case IResourceChangeEvent.POST_CHANGE:
                IResourceDelta delta = 
                    event.getDelta().findMember(file.getFullPath());
                if(delta != null) {
                    try {
                        switch(delta.getKind()) {
                            case IResourceDelta.CHANGED:
                                if((delta.getFlags() & IResourceDelta.CONTENT) != 0
                                    && file.getModificationStamp() > selfModificationStamp)

                                    loadProperties();
                                            
                                break;
                        
                            case IResourceDelta.REMOVED:
                                if((delta.getFlags() & IResourceDelta.MOVED_TO) == 0)
                                    passivate();
                                else {
                                    File oldCache = getCache();
                                    file = 
                                        ResourcesPlugin
                                            .getWorkspace()
                                            .getRoot()
                                            .getFile(delta.getMovedToPath());
                                    if(oldCache.isFile()) {
                                        File cache = getCache();
                                        File parent = cache.getParentFile();
                                        if(!parent.exists())
                                            parent.mkdirs();
                                            
                                        if(cache.exists())
                                            cache.delete();
                                            
                                        oldCache.renameTo(cache);
                                    }
                                }
                        
                                break;
                        }
                    }
                    catch(CoreException ex) {
                        RSSCore.getPlugin().getLog().log(ex.getStatus());
                    }
                }
        }
    }

    public boolean equals(Object other) {
        if(other instanceof Channel) {
            return file.equals(((Channel)other).file);
        }
        else return false;
    }
    
    public int hashCode() {
        return file.hashCode();
    }
    
    public String toString() {
        return file.toString();
    }

    private Date parseDate(String str) {
        try {
            return DateFormat.getInstance().parse(str);
        }
        catch(ParseException ex) {
            return null;
        }
    }
    
    static Channel create(
        IFile file, 
        IRegisteredTranslator translator,
        Document document,
        URL url,
        Integer updateInterval,
        IProgressMonitor monitor)
        throws CoreException {
            
        if(monitor != null)
            monitor.beginTask("create", 2);

        Channel channel = new Channel(file, translator);
        channel.suppressChangeEvents = true;
        try {
            channel.setURL(url);
            channel.update(
                document, 
                monitor == null ?
                    null :
                    new SubProgressMonitor(monitor, 1));
            channel.setUpdateInterval(updateInterval);
            channel.save(
                monitor == null ?
                    null :
                    new SubProgressMonitor(monitor, 1));
        }
        finally {
            channel.suppressChangeEvents = false;
            if(monitor != null)
                monitor.done();
        }
        
        ChannelManager.getInstance().firePropertyChange(
            channel, 
            ChannelChangeEvent.ADDED);

        return channel;
    }
    
    static Channel load(IFile file) throws CoreException {
        Channel channel = new Channel(file);
        channel.loadProperties();
        channel.loadContents();
        return channel;
    }
    
    private class UpdateTask extends TimerTask {

        /* (non-Javadoc)
         * @see java.util.TimerTask#run()
         */
        public void run() {
            try {
                update((IProgressMonitor)null);
            }
            catch(CoreException ex) {
                RSSCore.getPlugin().getLog().log(ex.getStatus());
            }
        }
    }
}
