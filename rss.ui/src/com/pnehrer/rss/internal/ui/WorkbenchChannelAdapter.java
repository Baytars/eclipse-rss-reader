/*
 * Created on Nov 5, 2003
 * Version $Id$
 */
package com.pnehrer.rss.internal.ui;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.model.IWorkbenchAdapter;

import com.pnehrer.rss.core.IChannel;
import com.pnehrer.rss.ui.RSSUI;

/**
 * @author <a href="mailto:pnehrer@freeshell.org">Peter Nehrer</a>
 */
public class WorkbenchChannelAdapter implements IWorkbenchAdapter {

    /* (non-Javadoc)
     * @see org.eclipse.ui.model.IWorkbenchAdapter#getChildren(java.lang.Object)
     */
    public Object[] getChildren(Object o) {
        return ((IChannel)o).getItems();
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.model.IWorkbenchAdapter#getImageDescriptor(java.lang.Object)
     */
    public ImageDescriptor getImageDescriptor(Object object) {
        IChannel channel = (IChannel)object;
        ImageDescriptor descriptor = 
            RSSUI.getDefault().getImageDescriptor16x16(channel);
        
        if(channel.hasUpdates())
            return new NewChannelImageDescriptor(
                descriptor, 
                RSSUI.getDefault().getImageRegistry().getDescriptor(
                    RSSUI.NEW_DECORATOR_ICON));
        else
            return descriptor;
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.model.IWorkbenchAdapter#getLabel(java.lang.Object)
     */
    public String getLabel(Object object) {
        IChannel channel = (IChannel)object;
        String title = channel.getTitle();
        if(channel.hasUpdates())
            title += "*";
        
        return title;
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.model.IWorkbenchAdapter#getParent(java.lang.Object)
     */
    public Object getParent(Object o) {
        return ((IChannel)o).getFile().getParent();
    }
}
