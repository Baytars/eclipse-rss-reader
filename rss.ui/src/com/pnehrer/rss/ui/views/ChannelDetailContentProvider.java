/*
 * Created on Nov 5, 2003
 * Version $Id$
 */
package com.pnehrer.rss.ui.views;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

import com.pnehrer.rss.core.IRSSElement;

/**
 * @author <a href="mailto:pnehrer@freeshell.org">Peter Nehrer</a>
 */
public class ChannelDetailContentProvider implements IStructuredContentProvider {

    private static final Object[] NO_CHILDREN = {};

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
     */
    public Object[] getElements(Object inputElement) {
        IRSSElement rssElement = 
            inputElement instanceof IAdaptable ? 
                (IRSSElement)((IAdaptable)inputElement).getAdapter(IRSSElement.class) :
                null;
                
        return rssElement == null ? NO_CHILDREN : rssElement.getChannel().getItems();
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.IContentProvider#dispose()
     */
    public void dispose() {
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
     */
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
    }
}
