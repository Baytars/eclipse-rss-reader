/*
 * Created on Nov 11, 2003
 * Version $Id$
 */
package com.pnehrer.rss.core.internal;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Date;

import org.eclipse.core.runtime.PlatformObject;

import com.pnehrer.rss.core.IChannel;
import com.pnehrer.rss.core.IImage;
import com.pnehrer.rss.core.IItem;
import com.pnehrer.rss.core.ITextInput;

/**
 * @author <a href="mailto:pnehrer@freeshell.org">Peter Nehrer</a>
 */
public class Channel extends PlatformObject implements IChannel {

    private final PropertyChangeSupport propertyChangeSupport = 
        new PropertyChangeSupport(this);

    private final String url;
    private String title;
    private String link;
    private String description;
    private Date date;
    private Image image;
    private Item[] items;
    private TextInput textInput;

    Channel(String url) {
        this.url = url;
    }

    /* (non-Javadoc)
     * @see com.pnehrer.rss.core.IChannel#getURL()
     */
    public String getURL() {
        return url;
    }

    /* (non-Javadoc)
     * @see com.pnehrer.rss.core.IChannel#getTitle()
     */
    public String getTitle() {
        return title;
    }
    
    void setTitle(String title) {
        this.title = title;
    }

    /* (non-Javadoc)
     * @see com.pnehrer.rss.core.IChannel#getLink()
     */
    public String getLink() {
        return link;
    }
    
    void setLink(String link) {
        this.link = link;
    }

    /* (non-Javadoc)
     * @see com.pnehrer.rss.core.IChannel#getDescription()
     */
    public String getDescription() {
        return description;
    }
    
    void setDescription(String description) {
        this.description = description;
    }

    /* (non-Javadoc)
     * @see com.pnehrer.rss.core.IChannel#getDate()
     */
    public Date getDate() {
        return date;
    }
    
    void setDate(Date date) {
        this.date = date;
    }

    /* (non-Javadoc)
     * @see com.pnehrer.rss.core.IChannel#getImage()
     */
    public IImage getImage() {
        return image;
    }
    
    void setImage(Image image) {
        this.image = image;
    }

    /* (non-Javadoc)
     * @see com.pnehrer.rss.core.IChannel#getItems()
     */
    public IItem[] getItems() {
        return items;
    }
    
    void setItems(Item[] items) {
        this.items = items;
    }

    /* (non-Javadoc)
     * @see com.pnehrer.rss.core.IChannel#getTextInput()
     */
    public ITextInput getTextInput() {
        return textInput;
    }
    
    void setTextInput(TextInput textInput) {
        this.textInput = textInput;
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
    
    public void firePropertyChange() {
        propertyChangeSupport.firePropertyChange(null, null, null);
    }

    public boolean equals(Object other) {
        if(other instanceof Channel) {
            return url.equals(((Channel)other).url);
        }
        else return false;
    }
    
    public int hashCode() {
        return url.hashCode();
    }
}
