/*
 * Created on Nov 11, 2003
 * Version $Id$
 */
package com.pnehrer.rss.core.internal;

import java.util.Date;

import org.eclipse.core.runtime.PlatformObject;

import com.pnehrer.rss.core.IChannel;
import com.pnehrer.rss.core.IItem;

/**
 * @author <a href="mailto:pnehrer@freeshell.org">Peter Nehrer</a>
 */
public class Item extends PlatformObject implements IItem {

    private Channel channel;
    private String title;
    private String description;
    private String link;
    private Date date;
    
    Item(
        Channel channel, 
        String title, 
        String description, 
        String link, 
        Date date) {
            
        this.channel = channel;
        this.title = title;
        this.description = description;
        this.link = link;
        this.date = date;
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

    /* (non-Javadoc)
     * @see com.pnehrer.rss.core.IItem#getDescription()
     */
    public String getDescription() {
        return description;
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
}
