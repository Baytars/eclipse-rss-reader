/*
 * Created on Nov 5, 2003
 * Version $Id$
 */
package com.pnehrer.rss.ui.views;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

import com.pnehrer.rss.core.ChannelChangeEvent;
import com.pnehrer.rss.core.IChannel;
import com.pnehrer.rss.core.IChannelChangeListener;

/**
 * @author <a href="mailto:pnehrer@freeshell.org">Peter Nehrer</a>
 */
public class ChannelContentProvider
    implements IStructuredContentProvider, 
    IChannelChangeListener {

    private Viewer viewer;
    private IChannel channel;

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
     */
    public Object[] getElements(Object inputElement) {
        IChannel channel = (IChannel)inputElement;
        return channel.getItems();
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.IContentProvider#dispose()
     */
    public void dispose() {
        if(channel != null)
            channel.removeChannelChangeListener(this);
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
     */
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        this.viewer = viewer;
        
        if(channel != null)
            channel.removeChannelChangeListener(this);
            
        channel = (IChannel)newInput;
            
        if(channel != null)
            channel.addChannelChangeListener(this);
    }

    /* (non-Javadoc)
     * @see com.pnehrer.rss.core.IChannelChangeListener#channelChanged(com.pnehrer.rss.core.ChannelChangeEvent)
     */
    public void channelChanged(ChannelChangeEvent event) {
        viewer.refresh();
    }
}
