/*
 * Created on Nov 11, 2003
 * Version $Id$
 */
package com.pnehrer.rss.internal.core;

import java.util.Date;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.PlatformObject;
import org.w3c.dom.Element;

import com.pnehrer.rss.core.IChannel;
import com.pnehrer.rss.core.IItem;
import com.pnehrer.rss.core.RSSCore;

/**
 * @author <a href="mailto:pnehrer@freeshell.org">Peter Nehrer</a>
 */
public class Item extends PlatformObject implements IItem {

    private static final String TITLE = "title";
    private static final String DESCRIPTION = "description";
    private static final String DATE = "date";

    private final Channel channel;
    private String title;
    private String description;
    private final String link;
    private Date date;
    
    Item(Channel channel, String link) {
        this.channel = channel;
        this.link = link;
    }

    /* (non-Javadoc)
     * @see com.pnehrer.rss.core.IItem#getChannel()
     */
    public IChannel getChannel() {
        return channel;
    }
    
    /* (non-Javadoc)
     * @see com.pnehrer.rss.core.IItem#getTitle()
     */
    public String getTitle() {
        return title;
    }
    
    private void setTitle(String title) {
        this.title = title;
    }

    /* (non-Javadoc)
     * @see com.pnehrer.rss.core.IItem#getDescription()
     */
    public String getDescription() {
        return description;
    }
    
    private void setDescription(String description) {
        this.description = description;
    }

    /* (non-Javadoc)
     * @see com.pnehrer.rss.core.IItem#getLink()
     */
    public String getLink() {
        return link;
    }

    /* (non-Javadoc)
     * @see com.pnehrer.rss.core.IItem#getDate()
     */
    public Date getDate() {
        return date;
    }
    
    private void setDate(Date date) {
        this.date = date;
    }
    
    public boolean isUpdated() {
        IFile file = channel.getFile();
        try {
            IMarker[] markers =
                file.findMarkers(
                    RSSCore.MARKER_UPDATE, 
                    true, 
                    IResource.DEPTH_ZERO);
                    
            for(int i = 0; i < markers.length; ++i)
                if(link.equals(markers[i].getAttribute(RSSCore.ATTR_LINK)))
                    return true;
        }
        catch(CoreException ex) {
            return false;
        }
                
        return false;
    }
    
    public void resetUpdateFlag() {
        IFile file = channel.getFile();
        try {
            IMarker[] markers =
                file.findMarkers(
                    RSSCore.MARKER_UPDATE, 
                    true, 
                    IResource.DEPTH_ZERO);
                    
            for(int i = 0; i < markers.length; ++i)
                if(link.equals(markers[i].getAttribute(RSSCore.ATTR_LINK)))
                    markers[i].delete();
        }
        catch(CoreException ex) {
            // ignore
        }
    }
    
    void update(Element item) {
        setTitle(item.getAttribute(TITLE));
        setDescription(item.getAttribute(DESCRIPTION));
        String dateStr = item.getAttribute(DATE); 
        setDate(
            dateStr == null || dateStr.trim().length() == 0 ? 
                null : 
                Channel.parseDate(dateStr));
    }

    public boolean equals(Object other) {
        if(other instanceof Item)
            return link.equals(((Item)other).link);
        else
            return false;
    }
    
    public int hashCode() {
        return link.hashCode();
    }
    
    public String toString() {
        return link;
    }
}
