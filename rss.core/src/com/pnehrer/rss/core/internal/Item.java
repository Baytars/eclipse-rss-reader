/*
 * Created on Nov 11, 2003
 * Version $Id$
 */
package com.pnehrer.rss.core.internal;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;

import org.eclipse.core.runtime.PlatformObject;
import org.w3c.dom.Element;

import com.pnehrer.rss.core.IChannel;
import com.pnehrer.rss.core.IItem;

/**
 * @author <a href="mailto:pnehrer@freeshell.org">Peter Nehrer</a>
 */
public class Item extends PlatformObject implements IItem {

    private static final String TITLE = "title";
    private static final String DESCRIPTION = "description";
    static final String LINK = "link";
    private static final String DATE = "date";

    private final Channel channel;
    private String title;
    private String description;
    private String link;
    private Date date;
    
    Item(Channel channel) {            
        this.channel = channel;
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
    
    private void setLink(String link) {
        this.link = link;
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
    
    void update(Element item) {
        setTitle(item.getAttribute(TITLE));
        setDescription(item.getAttribute(DESCRIPTION));
        setLink(item.getAttribute(LINK));
        String dateStr = item.getAttribute(DATE); 
        setDate(dateStr == null ? null : parseDate(dateStr));
    }
    
    void save(Element item) {
        item.setAttribute(TITLE, title);
        item.setAttribute(DESCRIPTION, description);
        item.setAttribute(LINK, link);
        if(date != null)
            item.setAttribute(DATE, DateFormat.getInstance().format(date));
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

    private Date parseDate(String str) {
        try {
            return DateFormat.getInstance().parse(str);
        }
        catch(ParseException ex) {
            return null;
        }
    }
}
