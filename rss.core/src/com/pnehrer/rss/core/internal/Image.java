/*
 * Created on Nov 11, 2003
 * Version $Id$
 */
package com.pnehrer.rss.core.internal;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.w3c.dom.Element;

import com.pnehrer.rss.core.IChannel;
import com.pnehrer.rss.core.IImage;
import com.pnehrer.rss.core.RSSCore;

/**
 * @author <a href="mailto:pnehrer@freeshell.org">Peter Nehrer</a>
 */
public class Image implements IImage {
    
    private static final String TITLE = "title";
    private static final String URL = "url";
    private static final String LINK = "link";
    
    private final Channel channel;
    private String title;
    private URL url;
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
        this.title = title;
    }

    /* (non-Javadoc)
     * @see com.pnehrer.rss.core.IImage#getURL()
     */
    public URL getURL() {
        return url;
    }
    
    private void setURL(URL url) {
        this.url = url;
    }

    /* (non-Javadoc)
     * @see com.pnehrer.rss.core.IImage#getLink()
     */
    public String getLink() {
        return link;
    }
    
    private void setLink(String link) {
        this.link = link;
    }
    
    void update(Element image) throws CoreException {
        setTitle(image.getAttribute(TITLE));
        String str = image.getAttribute(URL);
        try {
            setURL(new URL(str));
        }
        catch(MalformedURLException ex) {
            throw new CoreException(
                new Status(
                    IStatus.ERROR,
                    RSSCore.PLUGIN_ID,
                    0,
                    "invalid image url: " + str,
                    ex));
        }

        setLink(image.getAttribute(LINK));
    }
    
    void save(Element image) {
        image.setAttribute(TITLE, title);
        image.setAttribute(URL, url.toExternalForm());
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
        return url.toExternalForm();
    }
}
