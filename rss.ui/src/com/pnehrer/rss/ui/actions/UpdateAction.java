/*
 * Created on Nov 16, 2003
 * Version $Id$
 */
package com.pnehrer.rss.ui.actions;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.actions.WorkspaceModifyOperation;

import com.pnehrer.rss.core.IChannel;
import com.pnehrer.rss.core.RSSCore;
import com.pnehrer.rss.ui.RSSUI;

/**
 * @author <a href="mailto:pnehrer@freeshell.org">Peter Nehrer</a>
 */
public class UpdateAction implements IActionDelegate {
    
    private ISelection selection;

    /* (non-Javadoc)
     * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction, org.eclipse.jface.viewers.ISelection)
     */
    public void selectionChanged(IAction action, ISelection selection) {
        this.selection = selection;
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
     */
    public void run(IAction action) {
        if(selection instanceof IStructuredSelection && !selection.isEmpty()) {
            Object firstElement = ((IStructuredSelection)selection).getFirstElement();
            IFile file = firstElement instanceof IFile ?
                (IFile)firstElement :
                (IFile)((IAdaptable)firstElement).getAdapter(IFile.class);

            final IChannel channel;
            try {
                channel = RSSCore.getPlugin().getChannel(file);
            }
            catch(CoreException ex) {
                RSSUI.getDefault().getLog().log(
                    new Status(
                        IStatus.ERROR,
                        RSSUI.PLUGIN_ID,
                        0,
                        "could not obtain channel for file " + file,
                        ex));

                return;
            }
            
            Shell shell = Display.getCurrent().getActiveShell(); 
            ProgressMonitorDialog dlg = new ProgressMonitorDialog(shell);
            try {
                dlg.run(true, true, new WorkspaceModifyOperation() {
                        protected void execute(IProgressMonitor monitor) 
                            throws CoreException, 
                            InvocationTargetException, 
                            InterruptedException {

                            channel.update();                    
                        }
                    });
            }
            catch(InterruptedException ex) {
                // ignore
            }
            catch(InvocationTargetException ex) {
                MessageDialog.openError(
                    shell, 
                    "RSS Feed Update Error",
                    "Could not update RSS feed. Exception: " + ex);
            }
        }
    }
}
