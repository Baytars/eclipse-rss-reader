/*
 * Created on Nov 5, 2003
 * Version $Id$
 */
package com.pnehrer.rss.ui.views;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Control;

import com.pnehrer.rss.core.ChannelChangeEvent;
import com.pnehrer.rss.core.IChannel;
import com.pnehrer.rss.core.IChannelChangeListener;
import com.pnehrer.rss.core.RSSCore;

/**
 * @author <a href="mailto:pnehrer@freeshell.org">Peter Nehrer</a>
 */
public class ChannelDetailContentProvider
    implements IStructuredContentProvider, 
    IChannelChangeListener {

    private static final Object[] NO_CHILDREN = {};

    private Viewer viewer;
    private Object input;

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
     */
    public Object[] getElements(Object inputElement) {
        IChannel channel = inputElement instanceof IAdaptable ? 
            (IChannel)((IAdaptable)inputElement).getAdapter(IChannel.class) :
            (IChannel)inputElement;
        return channel == null ? NO_CHILDREN : channel.getItems();
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.IContentProvider#dispose()
     */
    public void dispose() {
        if(viewer != null)
            RSSCore.getPlugin().removeChannelChangeListener(this);
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
     */
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        if(this.viewer == null)
            RSSCore.getPlugin().addChannelChangeListener(this);

        this.viewer = viewer;
        this.input = newInput;

        if(this.viewer == null)
            RSSCore.getPlugin().removeChannelChangeListener(this);
    }

    /* (non-Javadoc)
     * @see com.pnehrer.rss.core.IChannelChangeListener#channelChanged(com.pnehrer.rss.core.ChannelChangeEvent)
     */
    public void channelChanged(ChannelChangeEvent event) {
        if(event.getChannel().equals(input)) {
            Control control = viewer.getControl();
            if(control != null && !control.isDisposed())
                control.getDisplay().asyncExec(new Runnable() {
                    public void run() {
                        viewer.refresh();
                    }
                });
        }
    }
}
