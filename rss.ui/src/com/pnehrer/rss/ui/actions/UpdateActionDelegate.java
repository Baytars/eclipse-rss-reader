/*
 * Created on Dec 4, 2003
 * Version $Id$
 */
package com.pnehrer.rss.ui.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

/**
 * @author <a href="mailto:pnehrer@freeshell.org">Peter Nehrer</a>
 */
public class UpdateActionDelegate implements IObjectActionDelegate {
    
    private final UpdateAction updateAction;
    private IWorkbenchPart targetPart;
    
    public UpdateActionDelegate() {
        updateAction = new UpdateAction();
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.IObjectActionDelegate#setActivePart(org.eclipse.jface.action.IAction, org.eclipse.ui.IWorkbenchPart)
     */
    public void setActivePart(IAction action, IWorkbenchPart targetPart) {
        this.targetPart = targetPart;
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
     */
    public void run(IAction action) {
        updateAction.run();
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction, org.eclipse.jface.viewers.ISelection)
     */
    public void selectionChanged(IAction action, ISelection selection) {
        updateAction.selectionChanged(
            selection instanceof IStructuredSelection ?
                (IStructuredSelection)selection :
                new StructuredSelection());
                
        action.setEnabled(updateAction.isEnabled());
    }
}
