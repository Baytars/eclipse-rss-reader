/*
 * Created on Nov 17, 2003
 * Version $Id$
 */
package com.pnehrer.rss.ui.views;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.model.WorkbenchContentProvider;

import com.pnehrer.rss.core.ChannelChangeEvent;
import com.pnehrer.rss.core.IChannel;
import com.pnehrer.rss.core.IChannelChangeListener;
import com.pnehrer.rss.core.IRSSElement;
import com.pnehrer.rss.core.RSSCore;
import com.pnehrer.rss.ui.RSSUI;

/**
 * @author <a href="mailto:pnehrer@freeshell.org">Peter Nehrer</a>
 */
public class ChannelNavigatorContentProvider 
    extends WorkbenchContentProvider
    implements IChannelChangeListener {

    private static final Object[] NO_CHILDREN = {};

    public ChannelNavigatorContentProvider() {
        RSSCore.getPlugin().addChannelChangeListener(this);
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.IContentProvider#dispose()
     */
    public void dispose() {
        RSSCore.getPlugin().removeChannelChangeListener(this);
        super.dispose();
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
     */
    public Object[] getChildren(Object element) {
        Object[] children = super.getChildren(element);
        if(element instanceof IRSSElement) {
            return children;
        }
        else {
            Collection newChildren = new ArrayList(children.length / 2 + 1);
            for(int i = 0, n = children.length; i < n; ++i) {
                if(children[i] instanceof IAdaptable) {
                    IFile file = (IFile)
                        ((IAdaptable)children[i]).getAdapter(IFile.class);
                    if(file == null) {
                        newChildren.add(children[i]);
                    }
                    else {
                        try {
                            IChannel channel = 
                                RSSCore.getPlugin().getChannel(file);
                            if(channel != null)
                                newChildren.add(channel);
                        }
                        catch(CoreException ex) {
                            RSSUI.getDefault().getLog().log(
                                new Status(
                                    IStatus.ERROR,
                                    RSSUI.PLUGIN_ID,
                                    0,
                                    "could not load channel from file " + file,
                                    ex));
                        }
                    }
                }
            }

            return newChildren.toArray();
        }
    }

    /* (non-Javadoc)
     * @see com.pnehrer.rss.core.IChannelChangeListener#channelChanged(com.pnehrer.rss.core.ChannelChangeEvent)
     */
    public void channelChanged(final ChannelChangeEvent event) {
        Control ctrl = viewer.getControl();
        if(ctrl != null && !ctrl.isDisposed()) {
            final IChannel channel = event.getChannel();
            ctrl.getDisplay().syncExec(new Runnable() {
                public void run() {
                    Control ctrl = viewer.getControl();
                    if(ctrl != null && !ctrl.isDisposed()) {
                        StructuredViewer v = (StructuredViewer)viewer;
                        if((event.getFlags() & ChannelChangeEvent.ADDED) != 0
                            || (event.getFlags() & ChannelChangeEvent.REMOVED) != 0)

                            v.refresh(channel.getFile().getParent(), true);
                        else
                            v.refresh(channel, true);
                    }
                }
            });
        }
    }
}
