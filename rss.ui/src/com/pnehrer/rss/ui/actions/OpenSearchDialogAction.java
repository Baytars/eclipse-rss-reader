/*
 * Created on 3/15/2004
 * Version $Id$
 */
package com.pnehrer.rss.ui.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.search.ui.NewSearchUI;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

import com.pnehrer.rss.internal.ui.search.RSSSearchPage;

/**
 * @author <a href="mailto:pnehrer@freeshell.org">Peter Nehrer</a>
 * @see IWorkbenchWindowActionDelegate
 */
public class OpenSearchDialogAction implements IWorkbenchWindowActionDelegate {

    private IWorkbenchWindow window;

    /**
     * @see IWorkbenchWindowActionDelegate#init
     */
    public void init(IWorkbenchWindow window) {
        this.window = window;
    }

    /**
     * @see IWorkbenchWindowActionDelegate#selectionChanged
     */
    public void selectionChanged(IAction action, ISelection selection) {
    }

	/**
	 * @see IWorkbenchWindowActionDelegate#run
	 */
	public void run(IAction action) {
        NewSearchUI.openSearchDialog(window, RSSSearchPage.PAGE_ID);
	}

	/**
	 * @see IWorkbenchWindowActionDelegate#dispose
	 */
	public void dispose() {
	}
}
