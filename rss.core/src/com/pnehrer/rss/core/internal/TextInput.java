/*
 * Created on Nov 11, 2003
 * Version $Id$
 */
package com.pnehrer.rss.core.internal;

import org.eclipse.core.runtime.PlatformObject;
import org.w3c.dom.Element;

import com.pnehrer.rss.core.IChannel;
import com.pnehrer.rss.core.ITextInput;

/**
 * @author <a href="mailto:pnehrer@freeshell.org">Peter Nehrer</a>
 */
public class TextInput extends PlatformObject implements ITextInput {

    private static final String TITLE = "title";
    private static final String DESCRIPTION = "description";
    private static final String NAME = "name";
    private static final String LINK = "link";

    private final Channel channel;
    private String title;
    private String description;
    private String name;
    private String link;
    
    TextInput(Channel channel) {
        this.channel = channel;
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
    
    private void setTitle(String title) {
        this.title = title;
    }

    /* (non-Javadoc)
     * @see com.pnehrer.rss.core.ITextInput#getDescription()
     */
    public String getDescription() {
        return description;
    }

    private void setDescription(String description) {
        this.description = description;
    }

    /* (non-Javadoc)
     * @see com.pnehrer.rss.core.ITextInput#getName()
     */
    public String getName() {
        return name;
    }
    
    private void setName(String name) {
        this.name = name;
    }

    /* (non-Javadoc)
     * @see com.pnehrer.rss.core.ITextInput#getLink()
     */
    public String getLink() {
        return link;
    }
    
    private void setLink(String link) {
        this.link = link;
    }
    
    void update(Element textInput) {
        setTitle(textInput.getAttribute(TITLE));
        setDescription(textInput.getAttribute(DESCRIPTION));
        setName(textInput.getAttribute(NAME));
        setLink(textInput.getAttribute(LINK));
    }
    
    void save(Element textInput) {
        textInput.setAttribute(TITLE, title);
        textInput.setAttribute(DESCRIPTION, description);
        textInput.setAttribute(NAME, name);
        textInput.setAttribute(LINK, link);
    }
    
    public boolean equals(Object other) {
        if(other instanceof TextInput)
            return link.equals(((TextInput)other).link);
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
