/*
 * Created on Nov 11, 2003
 * Version $Id$
 */
package com.pnehrer.rss.core.internal;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.TimerTask;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
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
    implements IChannel,
        IResourceChangeListener {
    
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

    private final IFile file;
    private IRegisteredTranslator translator;
    private URL url;
    private Integer updateInterval;
    private String title;
    private String link;
    private String description;
    private Date date;
    private Date lastUpdated;
    private Image image;
    private final Map items = Collections.synchronizedMap(new HashMap());
    private TextInput textInput;
    
    private boolean ignoreResourceChange;
    private boolean suppressChangeEvents;

    private Channel(IFile file) {
        this.file = file;
        startListening();
    }
    
    private Channel(IFile file, IRegisteredTranslator translator) {
        this.file = file;
        this.translator = translator;
        startListening();
    }
    
    private void startListening() {
        ResourcesPlugin.getWorkspace().addResourceChangeListener(this);
    }
    
    private void stopListening() {
        ResourcesPlugin.getWorkspace().removeResourceChangeListener(this);
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
        firePropertyChange();
    }
    
    /* (non-Javadoc)
     * @see com.pnehrer.rss.core.IChannel#getURL()
     */
    public URL getURL() {
        return url;
    }
    
    public synchronized void setURL(URL url) {
        this.url = url;
        firePropertyChange();
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
        firePropertyChange();
    }
    
    /* (non-Javadoc)
     * @see com.pnehrer.rss.core.IChannel#getLastUpdated()
     */
    public Date getLastUpdated() {
        return lastUpdated;
    }
    
    private void setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
        firePropertyChange();
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
        return (IItem[])items.values().toArray(new IItem[items.size()]);
    }
    
    /* (non-Javadoc)
     * @see com.pnehrer.rss.core.IChannel#getTextInput()
     */
    public ITextInput getTextInput() {
        return textInput;
    }
    
    private void firePropertyChange() {
        if(suppressChangeEvents)
            return;

        ChannelManager.getInstance().firePropertyChange(this);            
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
                throw new CoreException(
                    new Status(
                        IStatus.ERROR,
                        RSSCore.PLUGIN_ID,
                        0,
                        "could not parse channel source",
                        ex));
            }
            catch(SAXException ex) {
                throw new CoreException(
                    new Status(
                        IStatus.ERROR,
                        RSSCore.PLUGIN_ID,
                        0,
                        "could not parse channel source",
                        ex));
            }
            catch(IOException ex) {
                throw new CoreException(
                    new Status(
                        IStatus.ERROR,
                        RSSCore.PLUGIN_ID,
                        0,
                        "could not parse channel source",
                        ex));
            }
            finally {
                if(monitor != null)
                    monitor.done();
            }
        }
    }
    
    private synchronized void update(
        Document sourceDocument, 
        IProgressMonitor monitor) 
        throws CoreException {
            
        if(monitor != null)
            monitor.beginTask("update", 3);

        try {
            Document document = translator.translate(sourceDocument);
            
            if(monitor != null)
                monitor.worked(1);
            
            Element channel = document.getDocumentElement();
            if(CHANNEL.equals(channel.getLocalName())) {
                update(channel);
                
                if(monitor != null)
                    monitor.worked(1);
                
                setLastUpdated(new Date());
                save(monitor == null ?
                    null :
                    new SubProgressMonitor(monitor, 1));
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
    
    private synchronized void load() throws CoreException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        Document document;
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            document = builder.parse(file.getContents());
        }
        catch(ParserConfigurationException ex) {
            throw new CoreException(
                new Status(
                    IStatus.ERROR,
                    RSSCore.PLUGIN_ID,
                    0,
                    "could not parse channel file",
                    ex));
        }
        catch(SAXException ex) {
            throw new CoreException(
                new Status(
                    IStatus.ERROR,
                    RSSCore.PLUGIN_ID,
                    0,
                    "could not parse channel file",
                    ex));
        }
        catch(IOException ex) {
            throw new CoreException(
                new Status(
                    IStatus.ERROR,
                    RSSCore.PLUGIN_ID,
                    0,
                    "could not parse channel file",
                    ex));
        }
        
        Element channel = document.getDocumentElement();
        if(CHANNEL.equals(channel.getLocalName())) {
            do {
                String str = channel.getAttribute(TRANSLATOR_ID);
                if(str == null)
                    break;
                    
                suppressChangeEvents = true;
                try {
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
                                "could not find source translator with id: " + str,
                                null));

                    setTranslator(matchingTranslator);

                    str = channel.getAttribute(URL);
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
                                "invalid channel url: " + str,
                                ex));
                    }
        
                    str = channel.getAttribute(UPDATE_INTERVAL);
                    setUpdateInterval(str == null ? null : new Integer(str));

                    update(channel);

                    return;
                }
                finally {
                    suppressChangeEvents = false;
                    firePropertyChange();
                }
            }
            while(false);
        }

        throw new CoreException(
            new Status(
                IStatus.ERROR,
                RSSCore.PLUGIN_ID,
                0,
                "invalid channel file",
                null));
    }
    
    private void update(Element channel) throws CoreException {
        setTitle(channel.getAttribute(TITLE));
        setLink(channel.getAttribute(LINK));
        setDescription(channel.getAttribute(DESCRIPTION));
        String dateStr = channel.getAttribute(DATE); 
        setDate(dateStr == null ? null : parseDate(dateStr));

        boolean hasImage = false;
        boolean hasTextInput = false;
        Collection liveItemLinks = new HashSet();
        Map oldItems = new HashMap(items);

        NodeList children = channel.getChildNodes();
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
                    String itemLink = 
                        ((Element)node).getAttribute(Item.LINK);
                    Item item = (Item)items.get(itemLink);
                    if(item == null) {
                        item = new Item(this);
                        items.put(itemLink, item);
                    }
                    
                    liveItemLinks.add(itemLink);
                    item.update((Element)node);
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

        items.keySet().retainAll(liveItemLinks);
    }
    
    public synchronized void save(IProgressMonitor monitor) 
        throws CoreException {

        DocumentBuilderFactory df = DocumentBuilderFactory.newInstance();
        df.setNamespaceAware(true);
        
        if(monitor != null)
            monitor.beginTask("save", 3);
        
        try {
            DocumentBuilder builder = df.newDocumentBuilder();
            Document document = builder.newDocument();
            
            Element channel = document.createElement(CHANNEL);
            document.appendChild(channel);
            channel.setAttribute(TRANSLATOR_ID, translator.getId());
            channel.setAttribute(URL, url.toExternalForm());
            if(updateInterval != null)
                channel.setAttribute(
                    UPDATE_INTERVAL, 
                    updateInterval.toString());
                
            channel.setAttribute(
                LAST_UPDATED, 
                DateFormat.getInstance().format(lastUpdated));
                
            channel.setAttribute(TITLE, title);
            channel.setAttribute(LINK, link);
            channel.setAttribute(DESCRIPTION, description);
            if(date != null)
                channel.setAttribute(
                    DATE, 
                    DateFormat.getInstance().format(date));
    
            if(image != null) {
                Element imageElement = document.createElement(IMAGE);
                channel.appendChild(imageElement);
                image.save(imageElement);
            }
                
            for(Iterator i = items.values().iterator(); i.hasNext();) {
                Item item = (Item)i.next();
                Element itemElement = document.createElement(ITEM);
                channel.appendChild(itemElement);
                item.save(itemElement);
            }
            
            if(textInput != null) {
                Element textInputElement = document.createElement(TEXT_INPUT);
                channel.appendChild(textInputElement);
                textInput.save(textInputElement);
            }
            
            if(monitor != null)
                monitor.worked(1);
            
            ByteArrayOutputStream out = new ByteArrayOutputStream();            
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.transform(
                new DOMSource(document), 
                new StreamResult(out));

            if(monitor != null)
                monitor.worked(1);
        
            ignoreResourceChange = true;
            file.setContents(
                new ByteArrayInputStream(out.toByteArray()), 
                true, 
                true, 
                monitor == null ?
                    null :
                    new SubProgressMonitor(monitor, 1));
        }
        catch(ParserConfigurationException ex) {
            throw new CoreException(
                new Status(
                    IStatus.ERROR,
                    RSSCore.PLUGIN_ID,
                    0,
                    "could not save channel",
                    ex));
        }
        catch(TransformerException ex) {
            throw new CoreException(
                new Status(
                    IStatus.ERROR,
                    RSSCore.PLUGIN_ID,
                    0,
                    "could not save channel",
                    ex));
        }
        finally {
            ignoreResourceChange = false;
        }
    }
    
    private void updateUpdateSchedule() {
        synchronized(updateTaskLock) {
            if(updateTask != null)
                updateTask.cancel();

            if(updateInterval != null) {
                updateTask = new UpdateTask();
                ChannelManager.getInstance().scheduleTask(
                    updateTask, 
                    updateInterval.intValue());
            }
        }
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResourceChangeListener#resourceChanged(org.eclipse.core.resources.IResourceChangeEvent)
     */
    public void resourceChanged(IResourceChangeEvent event) {
        if(ignoreResourceChange)
            return;
            
        IResource resource = event.getResource();
        switch(event.getType()) {
            case IResourceChangeEvent.PRE_CLOSE:
            case IResourceChangeEvent.PRE_DELETE:
                if(file.getProject().equals(resource.getProject()))
                    stopListening();

                break;
                
            case IResourceChangeEvent.POST_CHANGE:
            try {
                if(event.getDelta() == null) {
                    if(file.equals(resource))
                        load();
                }
                else {
                    event.getDelta().accept(new ResourceDeltaVisitor());
                }
            }
            catch(CoreException ex) {
                // TODO Log me!
            }
        }
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
     */
    public Object getAdapter(Class adapter) {
        Object result = super.getAdapter(adapter);
        if(result == null && adapter.isAssignableFrom(IFile.class)) return file;
        else return result;
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
    
    public static Channel create(
        IFile file, 
        IRegisteredTranslator translator,
        Document document,
        URL url,
        Integer updateInterval,
        IProgressMonitor monitor)
        throws CoreException {
            
        Channel channel = new Channel(file, translator);
        channel.setURL(url);
        channel.setUpdateInterval(updateInterval);
        channel.update(document, monitor);
        return channel;
    }
    
    public static Channel load(IFile file) throws CoreException {
        Channel channel = new Channel(file);
        channel.load();
        return channel;
    }
    
    private class ResourceDeltaVisitor implements IResourceDeltaVisitor {

        /* (non-Javadoc)
         * @see org.eclipse.core.resources.IResourceDeltaVisitor#visit(org.eclipse.core.resources.IResourceDelta)
         */
        public boolean visit(IResourceDelta delta) throws CoreException {
            IResource resource = delta.getResource();
            if(file.equals(resource)) {
                switch(delta.getKind()) {
                    case IResourceDelta.CHANGED:
                        if((delta.getFlags() & IResourceDelta.CONTENT) != 0)
                            load();
                                            
                        break;
                                    
                    case IResourceDelta.ADDED:
                        if((delta.getFlags() & IResourceDelta.MOVED_FROM) == 0)
                            load();

                        break;
                }
                                    
                return false;
            }
            else
                return resource.getType() != IResource.FILE;
        }
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
                RSSCore.getPlugin().getLog().log(
                    new Status(
                        IStatus.ERROR,
                        RSSCore.PLUGIN_ID,
                        0,
                        "could not update channel",
                        ex));
            }
        }
    }
}
