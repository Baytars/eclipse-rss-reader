/*
 * Created on Dec 17, 2003
 * Version $Id$
 */
package com.pnehrer.rss.internal.ui.linkbrowser;

import com.pnehrer.rss.ui.ILinkBrowserDescriptor;

/**
 * @author <a href="mailto:pnehrer@freeshell.org">Peter Nehrer</a>
 */
public class LinkBrowserDescriptor implements ILinkBrowserDescriptor {

    private final String id;
    private final String label;
    
    public LinkBrowserDescriptor(String id, String label) {
        this.id = id;
        this.label = label;
    }

    /* (non-Javadoc)
     * @see com.pnehrer.rss.ui.ILinkBrowserDescriptor#getId()
     */
    public String getId() {
        return id;
    }

    /* (non-Javadoc)
     * @see com.pnehrer.rss.ui.ILinkBrowserDescriptor#getLabel()
     */
    public String getLabel() {
        return label;
    }

    public boolean equals(Object other) {
        if(other instanceof LinkBrowserDescriptor)
            return id.equals(((LinkBrowserDescriptor)other).id);
        else
            return false;
    }
        
    public int hashCode() {
        return id.hashCode();
    }
    
    public String toString() {
        return id;
    }
}
