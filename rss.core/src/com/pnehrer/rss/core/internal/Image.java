/*
 * Created on Nov 11, 2003
 * Version $Id$
 */
package com.pnehrer.rss.core.internal;

import java.beans.PropertyChangeSupport;

import org.eclipse.core.runtime.PlatformObject;
import org.w3c.dom.Element;

import com.pnehrer.rss.core.IChannel;
import com.pnehrer.rss.core.IImage;

/**
 * @author <a href="mailto:pnehrer@freeshell.org">Peter Nehrer</a>
 */
public class Image extends PlatformObject implements IImage {
    
    private static final String TITLE = "title";
    private static final String URL = "url";
    private static final String LINK = "link";
    
    private final PropertyChangeSupport propertyChangeSupport = 
        new PropertyChangeSupport(this);
    
    private final Channel channel;
    private String title;
    private String url;
    private String link;

    Image(Channel channel) {
        this.channel = channel;
    }

    /* (non-Javadoc)
     * @see com.pnehrer.rss.core.IImage#getChannel()
     */
    public IChannel getChannel() {
        return channel;
    }

    /* (non-Javadoc)
     * @see com.pnehrer.rss.core.IImage#getTitle()
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
     * @see com.pnehrer.rss.core.IImage#getURL()
     */
    public String getURL() {
        return url;
    }
    
    private void setURL(String url) {
        Object oldValue = this.url;
        this.url = url;
        firePropertyChange(URL, oldValue, url);
    }

    /* (non-Javadoc)
     * @see com.pnehrer.rss.core.IImage#getLink()
     */
    public String getLink() {
        return link;
    }
    
    private void setLink(String link) {
        Object oldValue = this.link;
        this.link = link;
        firePropertyChange(LINK, oldValue, link);
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
    
    void update(Element image) {
        setTitle(image.getAttribute(TITLE));
        setURL(image.getAttribute(URL));
        setLink(image.getAttribute(LINK));
    }
    
    void save(Element image) {
        image.setAttribute(TITLE, title);
        image.setAttribute(URL, url);
        image.setAttribute(LINK, link);
    }
    
    public boolean equals(Object other) {
        if(other instanceof Image)
            return url.equals(((Image)other).url);
        else
            return false;
    }
    
    public int hashCode() {
        return url.hashCode();
    }
    
    public String toString() {
        return url;
    }
}
