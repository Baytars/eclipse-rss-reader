/*
 * Created on Nov 11, 2003
 * Version $Id$
 */
package com.pnehrer.rss.internal.core;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRunnable;
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
    implements IChannel, IResourceChangeListener {

    private static final String VERSION_HEADER = "Eclipse RSS Reader v1.2.0";

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
    
    private long selfModificationStamp;
    
    private Channel(IFile file) {
        this.file = file;
    }
    
    private Channel(IFile file, IRegisteredTranslator translator) {
        this.file = file;
        this.translator = translator;
    }
    
    private void activate() {
        ResourcesPlugin.getWorkspace().addResourceChangeListener(this);
    }
    
    private void passivate() {
        synchronized(this) {
            if(updateTask != null) {
                updateTask.cancel();
                updateTask = null;
            }
        }
            
        ResourcesPlugin.getWorkspace().removeResourceChangeListener(this);
        ChannelManager.getInstance().removeChannel(this);
    }

    private File getCache() {
        return file
            .getProject()
			.getWorkingLocation(RSSCore.PLUGIN_ID)
            .append(file.getProjectRelativePath())
            .toFile();
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
    
    public void setTranslator(IRegisteredTranslator translator) {
        this.translator = translator;
    }
    
    /* (non-Javadoc)
     * @see com.pnehrer.rss.core.IChannel#getURL()
     */
    public URL getURL() {
        return url;
    }
    
    public void setURL(URL url) {
        this.url = url;
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
        updateSchedule();
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

    /* (non-Javadoc)
     * @see com.pnehrer.rss.core.IChannel#getLink()
     */
    public String getLink() {
        return link;
    }

    /* (non-Javadoc)
     * @see com.pnehrer.rss.core.IChannel#getDescription()
     */
    public String getDescription() {
        return description;
    }

    /* (non-Javadoc)
     * @see com.pnehrer.rss.core.IChannel#getDate()
     */
    public Date getDate() {
        return date;
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
    
    public boolean hasUpdates() {
        try {
            IMarker[] markers =
                file.findMarkers(
                    RSSCore.MARKER_UPDATE, 
                    true, 
                    IResource.DEPTH_ZERO);

            return markers.length > 0;
        }
        catch(CoreException ex) {
            return false;
        }
    }
    
    public void resetUpdateFlags() {
        try {
            file.deleteMarkers(RSSCore.MARKER_UPDATE, true, IResource.DEPTH_ZERO);
        }
        catch(CoreException e) {
            // ignore
        }
    }
    
    public void update(IProgressMonitor monitor) throws CoreException {
        if(url != null) {
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
                "Could not parse channel source. File: " + file,
                t));
    }
    
    private synchronized void cache(Document document) throws CoreException {
        File cache = getCache();
        File parent = cache.getParentFile();
        if(!parent.exists())
            parent.mkdirs();
            
        // This sucks, but it works. How else can one serialize in a standard way?
        try {
	        TransformerFactory factory = TransformerFactory.newInstance();
	        Transformer serializer = factory.newTransformer();
	        serializer.transform(
	        		new DOMSource(document),
	        		new StreamResult(cache));
	        RSSCore rssCore = RSSCore.getPlugin();
	        if (rssCore.getPluginPreferences().getBoolean(RSSCore.PREF_LOG_UPDATES))
		        rssCore.getLog().log(
		        	new Status(
		        		IStatus.INFO,
						RSSCore.PLUGIN_ID, 
						0,
						"Cached " + url + " for " + file + " at " + cache + ".",
						null));
	        
	        updateSchedule();
		} 
        catch(TransformerException ex) {
            throw new CoreException(
                    new Status(
                        IStatus.ERROR,
                        RSSCore.PLUGIN_ID,
                        0,
                        "Could not cache channel contents. File: " + file,
                        ex));   
		}
    }
    
    private void update(Document sourceDocument, IProgressMonitor monitor) 
        throws CoreException {
            
        if(monitor != null)
            monitor.beginTask("update", 3);

        try {
            Document document = translator.translate(sourceDocument);
            cache(document);
            
            if(monitor != null)
                monitor.worked(1);
            
            final Element channel = document.getDocumentElement();
            if(CHANNEL.equals(channel.getLocalName())) {
                IWorkspaceRunnable action = new IWorkspaceRunnable() {
                    public void run(IProgressMonitor monitor) throws CoreException {
                        update(channel, true);
                    }
                };

                ResourcesPlugin.getWorkspace().run(
                    action, 
                    monitor == null ?
                        null :
                        new SubProgressMonitor(monitor, 1));
            }
            else {
                throw new CoreException(
                    new Status(
                        IStatus.ERROR,
                        RSSCore.PLUGIN_ID,
                        0,
                        "invalid channel format. File: " + file,
                        null));
            }
        }
        finally {
            if(monitor != null)
                monitor.done();
        }
    }
    
    private synchronized void loadProperties(boolean updateSchedule) 
        throws CoreException {
            
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
                        "Could not load channel properties from file " + file,
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
                throwCouldNotFindTranslatorException(str);

            translator = matchingTranslator;

            str = props.getProperty(URL);
            if(str == null)
                break;

            try {
                url = new URL(str);
            }
            catch(MalformedURLException ex) {
                throwInvalidChannelURL(str, ex);
            }

            str = props.getProperty(UPDATE_INTERVAL);
            updateInterval = str == null ? null : new Integer(str);
            if(updateSchedule)
                updateSchedule();
                
            return;
        }
        while(false);

        throwInvalidChannelFile();
    }

    private void throwCouldNotFindTranslatorException(String id) throws CoreException {    
        throw new CoreException(
            new Status(
                IStatus.ERROR,
                RSSCore.PLUGIN_ID,
                0,
                "Could not find source translator with id " + id + ", file: " + file,
                null));
    }
    
    private void throwInvalidChannelURL(String url, Throwable t) throws CoreException {
        throw new CoreException(
            new Status(
                IStatus.ERROR,
                RSSCore.PLUGIN_ID,
                0,
                "Invalid channel url " + url + ", file: " + file,
                t));
    }

    private void throwInvalidChannelFile() throws CoreException {
        throw new CoreException(
            new Status(
                IStatus.ERROR,
                RSSCore.PLUGIN_ID,
                0,
                "Invalid channel file: " + file,
                null));
    }

    private boolean loadContents() throws CoreException {
        File cache = getCache();
        if(cache.isFile()) {
            Document document = null;
            Exception exception = null;
            try {
                FileInputStream in = new FileInputStream(cache);
                DocumentBuilderFactory factory = 
                    DocumentBuilderFactory.newInstance();
                factory.setNamespaceAware(true);
                try {
                    DocumentBuilder builder = factory.newDocumentBuilder();
                    document = builder.parse(in);
                }
                catch(ParserConfigurationException ex) {
                    exception = ex;
                }
                catch(SAXException ex) {
                    exception = ex;
                }
                finally {
                    in.close();
                }
            }
            catch(IOException ex) {
            	exception = ex;
            }
            
            if (exception != null)
                RSSCore.getPlugin().getLog().log(
                    new Status(
                        IStatus.WARNING,
                        RSSCore.PLUGIN_ID,
                        0,
                        "Could not parse cached channel. File: " + file,
                        exception));
            else {
	            Element channel;
	            if(CHANNEL.equals((channel = document.getDocumentElement()).getLocalName())) {
	                update(channel, false);
	                return true;
	            }
            }
        }
        
        return false;
    }
    
    private void load_1_1() throws CoreException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        Document document = null;
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            document = builder.parse(file.getContents());
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
        
        final Element channel = document.getDocumentElement();
        if(CHANNEL.equals(channel.getLocalName())) {
            do {
                String str = channel.getAttribute(TRANSLATOR_ID);
                if(str == null)
                    break;
                    
                IRegisteredTranslator matchingTranslator = translator;
                if(translator == null || !str.equals(translator.getId())) 
                    matchingTranslator = 
                        TranslatorManager.getInstance().getTranslator(str); 

                if(matchingTranslator == null)
                    throwCouldNotFindTranslatorException(str);

                translator = matchingTranslator;

                str = channel.getAttribute(URL);
                if(str == null)
                    break;

                try {
                    url = new URL(str);
                }
                catch(MalformedURLException ex) {
                    throwInvalidChannelURL(str, ex);
                }
                
                IWorkspaceRunnable action = new IWorkspaceRunnable() {
                    public void run(IProgressMonitor monitor) throws CoreException {
                        update(channel, true);
                    }
                };

                ResourcesPlugin.getWorkspace().run(action, null);

                str = channel.getAttribute(UPDATE_INTERVAL);
                updateInterval = str == null ? null : new Integer(str);
                updateSchedule();
                return;
            }
            while(false);
        }

        throwInvalidChannelFile();
    }
    
    private synchronized void update(
    	Element channel, 
		boolean processChanges) 
        throws CoreException {
            
        title = channel.getAttribute(TITLE);
        link = channel.getAttribute(LINK);
        description = channel.getAttribute(DESCRIPTION);
        String dateStr = channel.getAttribute(DATE); 
        date = 
            dateStr == null || dateStr.trim().length() == 0 ? 
                null : 
                parseDate(dateStr);

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
                        image = new Image(Channel.this);
            
                    image.update((Element)node);
                    hasImage = true;
                }
                else if(ITEM.equals(node.getLocalName())) {
                    String itemLink = ((Element)node).getAttribute(LINK);
                    Item item = (Item)itemMap.get(itemLink);
                    if(item == null) {
                        item = new Item(Channel.this, itemLink);
                        items.add(itemIndex, item);
                        if(processChanges) {
                            IMarker marker = file.createMarker(RSSCore.MARKER_UPDATE);
                            marker.setAttribute(RSSCore.ATTR_LINK, itemLink);
                        }
                    }
            
                    item.update((Element)node);
                    liveItems.add(item);
                    ++itemIndex;
                }
                else if(TEXT_INPUT.equals(node.getLocalName())) {
                    Object oldTextInput = textInput;
                    if(textInput == null)
                        textInput = new TextInput(Channel.this);
                
                    textInput.update((Element)node);
                    hasTextInput = true;
                }
            }
        }

        if(processChanges) {        
            IMarker[] markers = 
                file.findMarkers(RSSCore.MARKER_UPDATE, true, IResource.DEPTH_ZERO);
            Map markerMap = new HashMap(markers.length);
            for(int i = 0; i < markers.length; ++i)
                markerMap.put(markers[i].getAttribute(RSSCore.ATTR_LINK), markers[i]);

            for(Iterator i = items.iterator(); i.hasNext();) {
                Item item = (Item)i.next();
                if(!liveItems.contains(item)) {
                    IMarker marker = (IMarker)markerMap.get(item.getLink());
                    if(marker != null)
                        marker.delete();
                
                    i.remove();
                }
            }
        }
        else
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
            props.store(out, VERSION_HEADER);
        }
        catch(IOException ex) {
            throw new CoreException(
                new Status(
                    IStatus.ERROR,
                    RSSCore.PLUGIN_ID,
                    0,
                    "Could not save channel properties to file " + file,
                    ex));
        }

        IWorkspace workspace = ResourcesPlugin.getWorkspace();
        ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
        workspace.removeResourceChangeListener(this);
        try {
	        if(file.exists())
	            file.setContents(in, false, true, monitor);
	        else
	            file.create(in, false, monitor);
	        
	        selfModificationStamp = file.getModificationStamp();
        }
        finally {
        	workspace.addResourceChangeListener(this);
        }
    }
    
    private synchronized void updateSchedule() {
        if(updateTask != null) {
            updateTask.cancel();
            updateTask = null;
        }

        if(updateInterval != null) {
            updateTask = new UpdateTask();
            ChannelManager.getInstance().scheduleTask(
                updateTask,
                getLastUpdated(), 
                updateInterval.intValue());
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
                                	&& selfModificationStamp != file.getModificationStamp())

                                	loadProperties(true);
                                            
                                break;
                        
                            case IResourceDelta.REMOVED:
                                if((delta.getFlags() & IResourceDelta.MOVED_TO) == 0) {
                                	passivate();
                                	File cache = getCache();
                                	if (cache.exists())
                                		cache.delete();
                                }
                                else if (!"rss".equals(delta.getMovedToPath().getFileExtension())) {
                                    file = 
                                        ResourcesPlugin
                                            .getWorkspace()
                                            .getRoot()
                                            .getFile(delta.getMovedToPath());
                                	passivate();
                                	File cache = getCache();
                                	if (cache.exists())
                                		cache.delete();
                                }
                                else {
                                    File oldCache = getCache();
                                    file = 
                                        ResourcesPlugin
                                            .getWorkspace()
                                            .getRoot()
                                            .getFile(delta.getMovedToPath());
                                    file.setSessionProperty(
                                    	ChannelManager.CHANNEL_KEY, 
										this);
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

    static Date parseDate(String str) {
        try {
            return DateFormat.getInstance().parse(str);
        }
        catch(ParseException ex) {
        	SimpleDateFormat format = 
        		new SimpleDateFormat("EEE, d MMM yy hh:mm:ss zzz");
        	try {
				return format.parse(str);
			} 
        	catch(ParseException ex2) {
        		int len = str.length();
        		if (str.endsWith("Z"))
        			str = str.substring(0, len - 1) + "GMT";
        		else if (len >= 6)
        			switch (str.charAt(len - 6)) {
						case '+':
						case '-':
							str = str.substring(0, len - 6) + "GMT" + str.substring(len - 6);
	        		}
        		
        		format = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ssz");
        		try {
					return format.parse(str);
				} 
        		catch(ParseException ex3) {
        			return null;
				}
			}
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
        try {
            channel.url = url;
            channel.updateInterval = updateInterval;
            channel.save(
                monitor == null ?
                    null :
                    new SubProgressMonitor(monitor, 1));
            channel.update(
                document,
                monitor == null ?
                    null :
                    new SubProgressMonitor(monitor, 1));
        }
        finally {
            if(monitor != null)
                monitor.done();
        }

        channel.activate();
        return channel;
    }
    
    static Channel load(IFile file) throws CoreException {
        Channel channel = new Channel(file);
        BufferedReader reader = 
            new BufferedReader(new InputStreamReader(file.getContents()));
        String header = null;
        try {
            header = reader.readLine();
            reader.close();
        }
        catch(IOException ex) {
            throw new CoreException(
                new Status(
                    IStatus.ERROR,
                    RSSCore.PLUGIN_ID,
                    0,
                    "Could not determine channel version for file " + file,
                    ex));
        }
        
        String expectedHeader = "#" + VERSION_HEADER;
        if(expectedHeader.equals(header)) {
            channel.loadProperties(false);
            if(channel.loadContents())
                channel.updateSchedule();
            else
                channel.update(null);
        }
        else {
            channel.load_1_1();
            channel.save(null);
        }

        channel.activate();
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
