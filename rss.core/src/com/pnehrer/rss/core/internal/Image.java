/*
 * Created on Nov 11, 2003
 * Version $Id$
 */
package com.pnehrer.rss.core.internal;

import org.eclipse.core.runtime.PlatformObject;

import com.pnehrer.rss.core.IChannel;
import com.pnehrer.rss.core.IImage;

/**
 * @author <a href="mailto:pnehrer@freeshell.org">Peter Nehrer</a>
 */
public class Image extends PlatformObject implements IImage {
    
    private Channel channel;
    private String title;
    private String url;
    private String link;

    Image(Channel channel, String title, String url, String link) {
        this.channel = channel;
        this.title = title;
        this.url = url;
        this.link = link;
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

    /* (non-Javadoc)
     * @see com.pnehrer.rss.core.IImage#getURL()
     */
    public String getURL() {
        return url;
    }

    /* (non-Javadoc)
     * @see com.pnehrer.rss.core.IImage#getLink()
     */
    public String getLink() {
        return link;
    }
}
