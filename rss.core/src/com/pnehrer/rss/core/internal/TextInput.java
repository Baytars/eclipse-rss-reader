/*
 * Created on Nov 11, 2003
 * Version $Id$
 */
package com.pnehrer.rss.core.internal;

import org.eclipse.core.runtime.PlatformObject;

import com.pnehrer.rss.core.IChannel;
import com.pnehrer.rss.core.ITextInput;

/**
 * @author <a href="mailto:pnehrer@freeshell.org">Peter Nehrer</a>
 */
public class TextInput extends PlatformObject implements ITextInput {

    private Channel channel;
    private String title;
    private String description;
    private String name;
    private String link;
    
    TextInput(
        Channel channel, 
        String title, 
        String description, 
        String name, 
        String link) {
            
        this.channel = channel;
        this.title = title;
        this.description = description;
        this.name = name;
        this.link = link;
    }

    /* (non-Javadoc)
     * @see com.pnehrer.rss.core.ITextInput#getChannel()
     */
    public IChannel getChannel() {
        return channel;
    }

    /* (non-Javadoc)
     * @see com.pnehrer.rss.core.ITextInput#getTitle()
     */
    public String getTitle() {
        return title;
    }

    /* (non-Javadoc)
     * @see com.pnehrer.rss.core.ITextInput#getDescription()
     */
    public String getDescription() {
        return description;
    }

    /* (non-Javadoc)
     * @see com.pnehrer.rss.core.ITextInput#getName()
     */
    public String getName() {
        return name;
    }

    /* (non-Javadoc)
     * @see com.pnehrer.rss.core.ITextInput#getLink()
     */
    public String getLink() {
        return link;
    }
}
