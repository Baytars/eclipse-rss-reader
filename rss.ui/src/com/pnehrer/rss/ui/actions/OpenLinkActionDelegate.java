/*
 * Created on Dec 7, 2003
 * Version $Id$
 */
package com.pnehrer.rss.ui.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

/**
 * @author <a href="mailto:pnehrer@freeshell.org">Peter Nehrer</a>
 */
public class OpenLinkActionDelegate implements IObjectActionDelegate {

    private OpenLinkAction openLinkAction;

    /* (non-Javadoc)
     * @see org.eclipse.ui.IObjectActionDelegate#setActivePart(org.eclipse.jface.action.IAction, org.eclipse.ui.IWorkbenchPart)
     */
    public void setActivePart(IAction action, IWorkbenchPart targetPart) {
        openLinkAction = new OpenLinkAction(targetPart.getSite().getShell());
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
     */
    public void run(IAction action) {
        openLinkAction.run();
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction, org.eclipse.jface.viewers.ISelection)
     */
    public void selectionChanged(IAction action, ISelection selection) {
        if(openLinkAction == null) {
            IWorkbench workbench = PlatformUI.getWorkbench();
            IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();            
            if(window == null) {
                IWorkbenchWindow[] windows = workbench.getWorkbenchWindows();
                if(windows != null && windows.length > 0)
                    window = windows[0];
                else
                    return;
            }

            openLinkAction = new OpenLinkAction(window.getShell());
        }

        openLinkAction.selectionChanged(
            selection instanceof IStructuredSelection ?
                (IStructuredSelection)selection :
                new StructuredSelection());
            
        action.setEnabled(openLinkAction.isEnabled());
    }
}
