/*
 * Created on Nov 11, 2003
 * Version $Id$
 */
package com.pnehrer.rss.core.internal;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
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
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.core.runtime.Status;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.pnehrer.rss.core.IChannel;
import com.pnehrer.rss.core.IImage;
import com.pnehrer.rss.core.IItem;
import com.pnehrer.rss.core.ITextInput;
import com.pnehrer.rss.core.RSSCore;
import com.pnehrer.rss.core.SourceTranslatorDelegate;

/**
 * @author <a href="mailto:pnehrer@freeshell.org">Peter Nehrer</a>
 */
public class Channel 
    extends PlatformObject 
    implements IChannel,
        IResourceChangeListener {
    
    private static final String CHANNEL = "channel";
    private static final String TRANSLATOR_ID = "translatorId";
    public static final String URL = "url";
    public static final String UPDATE_INTERVAL = "updateInterval";
    public static final String TITLE = "title";
    public static final String DESCRIPTION = "description";
    public static final String LINK = "link";
    public static final String DATE = "date";
    public static final String LAST_UPDATED = "lastUpdated";
    public static final String IMAGE = "image";
    public static final String ITEM = "item";
    public static final String TEXT_INPUT = "textInput";

    private final PropertyChangeSupport propertyChangeSupport = 
        new PropertyChangeSupport(this);
        
    private UpdateTask updateTask;
    private final Object updateTaskLock = new Object();

    private final IFile file;
    private SourceTranslatorDelegate translator;
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

    private Channel(IFile file) {
        this.file = file;
        startListening();
    }
    
    private Channel(IFile file, SourceTranslatorDelegate translator) {
        this.file = file;
        this.translator = translator;
        startListening();
    }
    
    private void startListening() {
        ResourcesPlugin.getWorkspace().addResourceChangeListener(this);
    }

    /* (non-Javadoc)
     * @see com.pnehrer.rss.core.IChannel#getFile()
     */
    public IFile getFile() {
        return file;
    }
    
    /* (non-Javadoc)
     * @see com.pnehrer.rss.core.IChannel#getURL()
     */
    public URL getURL() {
        return url;
    }
    
    public void setURL(URL url) {
        Object oldValue = this.url;
        this.url = url;
        firePropertyChange(URL, oldValue, url);
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
    public void setUpdateInterval(Integer updateInterval) {
        Object oldValue = this.updateInterval;
        this.updateInterval = updateInterval;
        firePropertyChange(UPDATE_INTERVAL, oldValue, updateInterval);
    }
    
    /* (non-Javadoc)
     * @see com.pnehrer.rss.core.IChannel#getLastUpdated()
     */
    public Date getLastUpdated() {
        return lastUpdated;
    }
    
    private void setLastUpdated(Date lastUpdated) {
        Object oldValue = this.lastUpdated;
        this.lastUpdated = lastUpdated;
        firePropertyChange(LAST_UPDATED, oldValue, lastUpdated);
    }
    
    /* (non-Javadoc)
     * @see com.pnehrer.rss.core.IChannel#getTitle()
     */
    public String getTitle() {
        return title;
    }
    
    private void setTitle(String title) {
        Object oldValue = this.title;
        this.title = title;
        firePropertyChange(TITLE, oldValue, title);
    }

    /* (non-Javadoc)
     * @see com.pnehrer.rss.core.IChannel#getLink()
     */
    public String getLink() {
        return link;
    }
    
    private void setLink(String link) {
        Object oldValue = this.link;
        this.link = link;
        firePropertyChange(LINK, oldValue, link);
    }

    /* (non-Javadoc)
     * @see com.pnehrer.rss.core.IChannel#getDescription()
     */
    public String getDescription() {
        return description;
    }
    
    private void setDescription(String description) {
        Object oldValue = this.description;
        this.description = description;
        firePropertyChange(DESCRIPTION, oldValue, description);
    }

    /* (non-Javadoc)
     * @see com.pnehrer.rss.core.IChannel#getDate()
     */
    public Date getDate() {
        return date;
    }
    
    private void setDate(Date date) {
        Object oldValue = this.date;
        this.date = date;
        firePropertyChange(DATE, oldValue, date);
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
    
    private void firePropertyChange(
        String propertyName, 
        Object oldValue, 
        Object newValue) {
            
        propertyChangeSupport.firePropertyChange(
            propertyName, 
            oldValue, 
            newValue);
    }
    
    public void update() throws CoreException {
        if(translator != null && url != null) {
            DocumentBuilderFactory factory = 
                DocumentBuilderFactory.newInstance();
            Document document;
            try {
                DocumentBuilder builder = factory.newDocumentBuilder();
                document = builder.parse(url.openStream());
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
            
            update(document);
        }
    }
    
    private synchronized void update(Document sourceDocument) 
        throws CoreException {
            
        Document document = 
            translator.getTranslator().translate(sourceDocument);
        
        Element channel = document.getDocumentElement();
        if(CHANNEL.equals(channel.getTagName())) {
            update(channel);
            setLastUpdated(new Date());
            save();
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
    
    private synchronized void load() throws CoreException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
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
        if(CHANNEL.equals(channel.getTagName())) {
            do {
                String str = channel.getAttribute(TRANSLATOR_ID);
                if(str == null)
                    break;
                    
                if(translator == null || !str.equals(translator.getId())) 
                    translator = RSSCore.getPlugin().getTranslator(str); 

                if(translator == null)
                    throw new CoreException(
                        new Status(
                            IStatus.ERROR,
                            RSSCore.PLUGIN_ID,
                            0,
                            "could not find source translator with id: " + str,
                            null));

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
    
    private void update(Element channel) 
        throws CoreException {
            
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
                if(IMAGE.equals(node.getNodeName())) {
                    Object oldImage = image;
                    if(image == null)
                        image = new Image(this);
                    
                    image.update((Element)node);
                    firePropertyChange(IMAGE, oldImage, image);
                    hasImage = true;
                }
                else if(ITEM.equals(node.getNodeName())) {
                    String itemLink = 
                        ((Element)node).getAttribute(Item.LINK);
                    Item item = (Item)items.get(itemLink);
                    if(item == null)
                        item = new Item(this);
                    
                    liveItemLinks.add(itemLink);
                    item.update((Element)node);
                }
                else if(TEXT_INPUT.equals(node.getNodeName())) {
                    Object oldTextInput = textInput;
                    if(textInput == null)
                        textInput = new TextInput(this);
                        
                    textInput.update((Element)node);
                    firePropertyChange(TEXT_INPUT, oldTextInput, textInput);
                    hasTextInput = true;
                }
            }
        }

        items.keySet().retainAll(liveItemLinks);
        firePropertyChange(ITEM, oldItems, items);
    }
    
    private synchronized void save() throws CoreException {
        DocumentBuilderFactory df = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;
        try {
            builder = df.newDocumentBuilder();
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

        Document document = builder.newDocument();
        
        Element channel = document.createElement(CHANNEL);
        document.appendChild(channel);
        channel.setAttribute(TRANSLATOR_ID, translator.getId());
        channel.setAttribute(URL, url.toExternalForm());
        if(updateInterval != null)
            channel.setAttribute(UPDATE_INTERVAL, updateInterval.toString());
            
        channel.setAttribute(
            LAST_UPDATED, 
            DateFormat.getInstance().format(lastUpdated));
            
        channel.setAttribute(TITLE, title);
        channel.setAttribute(LINK, link);
        channel.setAttribute(DESCRIPTION, description);
        if(date != null)
            channel.setAttribute(DATE, DateFormat.getInstance().format(date));

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
        
        TransformerFactory tf = TransformerFactory.newInstance();
        ignoreResourceChange = true;
        try {
            Transformer transformer = tf.newTransformer();
            transformer.transform(
                new DOMSource(document), 
                new StreamResult());
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
                // TODO Schedule new update task!
            }
        }
    }
    
    /* (non-Javadoc)
     * @see com.pnehrer.rss.core.IChannel#addPropertyChangeListener(java.beans.PropertyChangeListener)
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    /* (non-Javadoc)
     * @see com.pnehrer.rss.core.IChannel#removePropertyChangeListener(java.beans.PropertyChangeListener)
     */
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResourceChangeListener#resourceChanged(org.eclipse.core.resources.IResourceChangeEvent)
     */
    public void resourceChanged(IResourceChangeEvent event) {
        if(ignoreResourceChange)
            return;
            
        if(event.getType() == IResourceChangeEvent.POST_CHANGE) {
            try {
                if(event.getDelta() == null) {
                    if(file.equals(event.getResource()))
                        load();
                }
                else {
                    event.getDelta().accept(new IResourceDeltaVisitor() {
                        public boolean visit(IResourceDelta delta) 
                            throws CoreException {

                            if(file.equals(delta.getResource())) {
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
                                return true;
                        }
                    });
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
        SourceTranslatorDelegate translator,
        Document document) 
        throws CoreException {
            
        Channel channel = new Channel(file, translator);
        channel.update(document);
        return channel;
    }
    
    public static Channel load(IFile file) throws CoreException {
        Channel channel = new Channel(file);
        channel.load();
        return channel;
    }
    
    private class UpdateTask extends TimerTask {

        /* (non-Javadoc)
         * @see java.util.TimerTask#run()
         */
        public void run() {
            try {
                update();
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
