/*
 * Created on Dec 4, 2003
 * Version $Id$
 */
package com.pnehrer.rss.ui.views;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.actions.ActionGroup;

import com.pnehrer.rss.ui.actions.UpdateAction;

/**
 * @author <a href="mailto:pnehrer@freeshell.org">Peter Nehrer</a>
 */
public class ChannelActionGroup extends ActionGroup {
    
    private final UpdateAction updateAction;
    
    public ChannelActionGroup() {
        updateAction = new UpdateAction();
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.actions.ActionGroup#fillActionBars(org.eclipse.ui.IActionBars)
     */
    public void fillActionBars(IActionBars actionBars) {
        IMenuManager menu = actionBars.getMenuManager();
        menu.add(updateAction);
        
        IToolBarManager toolBar = actionBars.getToolBarManager();
        toolBar.add(new Separator());
        toolBar.add(updateAction);
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.actions.ActionGroup#fillContextMenu(org.eclipse.jface.action.IMenuManager)
     */
    public void fillContextMenu(IMenuManager menu) {
        IStructuredSelection selection =
            (IStructuredSelection)getContext().getSelection();
    
        menu.add(updateAction);
        updateAction.selectionChanged(selection);
        menu.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.ui.actions.ActionGroup#updateActionBars()
     */
    public void updateActionBars() {
        IStructuredSelection selection =
            (IStructuredSelection) getContext().getSelection();
        updateAction.selectionChanged(selection);
    }
}
