/*
 * Created on Nov 17, 2003
 * Version $Id$
 */
package com.pnehrer.rss.ui.views;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.ui.views.navigator.ResourceNavigator;

/**
 * @author <a href="mailto:pnehrer@freeshell.org">Peter Nehrer</a>
 */
public class ChannelNavigator extends ResourceNavigator {

    /* (non-Javadoc)
     * @see org.eclipse.ui.views.navigator.ResourceNavigator#initContentProvider(org.eclipse.jface.viewers.TreeViewer)
     */
    protected void initContentProvider(TreeViewer viewer) {
        viewer.setContentProvider(new ChannelNavigatorContentProvider());
    }
}
