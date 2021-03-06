/*
 * Created on Nov 5, 2003
 * Version $Id$
 */
package com.pnehrer.rss.internal.ui;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.ui.model.IWorkbenchAdapter;

import com.pnehrer.rss.core.IItem;
import com.pnehrer.rss.ui.RSSUI;

/**
 * @author <a href="mailto:pnehrer@freeshell.org">Peter Nehrer</a>
 */
public class WorkbenchItemAdapter implements IWorkbenchAdapter {

    private static final Object[] NO_CHILDREN = {};

    /* (non-Javadoc)
     * @see org.eclipse.ui.model.IWorkbenchAdapter#getChildren(java.lang.Object)
     */
    public Object[] getChildren(Object o) {
        return NO_CHILDREN;
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.model.IWorkbenchAdapter#getImageDescriptor(java.lang.Object)
     */
    public ImageDescriptor getImageDescriptor(Object object) {
        IItem item = (IItem)object;
        ImageRegistry reg = RSSUI.getDefault().getImageRegistry();
        return reg.getDescriptor(RSSUI.ITEM_ICON);
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.model.IWorkbenchAdapter#getLabel(java.lang.Object)
     */
    public String getLabel(Object object) {
        IItem item = (IItem)object;
        return item.getTitle();
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.ui.model.IWorkbenchAdapter#getParent(java.lang.Object)
     */
    public Object getParent(Object o) {
        return ((IItem)o).getChannel();
    }
}
