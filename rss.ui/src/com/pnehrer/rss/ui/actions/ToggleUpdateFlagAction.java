/*
 * Created on Mar 23, 2004
 * Version $Id$
 */
package com.pnehrer.rss.ui.actions;

import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.SelectionListenerAction;
import org.eclipse.ui.actions.WorkspaceModifyOperation;

import com.pnehrer.rss.core.IRSSElement;
import com.pnehrer.rss.ui.RSSUI;

/**
 * @author <a href="mailto:pnehrer@freeshell.org">Peter Nehrer</a>
 */
public abstract class ToggleUpdateFlagAction
    extends SelectionListenerAction
    implements IObjectActionDelegate, IViewActionDelegate {
        
    private IWorkbenchWindow window;

    /**
     * @param text
     */
    public ToggleUpdateFlagAction(String text, IWorkbenchWindow window) {
        super(text);
        this.window = window;
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.IObjectActionDelegate#setActivePart(org.eclipse.jface.action.IAction, org.eclipse.ui.IWorkbenchPart)
     */
    public void setActivePart(IAction action, IWorkbenchPart targetPart) {
        window = targetPart.getSite().getWorkbenchWindow();
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.IViewActionDelegate#init(org.eclipse.ui.IViewPart)
     */
    public void init(IViewPart view) {
        window = view.getViewSite().getWorkbenchWindow();
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
     */
    public void run(IAction action) {
        final IStructuredSelection selection = getStructuredSelection();
        WorkspaceModifyOperation op = new WorkspaceModifyOperation() {
            protected void execute(IProgressMonitor monitor) 
                throws CoreException, InvocationTargetException, InterruptedException {

                for(Iterator i = selection.iterator(); i.hasNext();) {
                    IRSSElement rssElement = (IRSSElement)
                        ((IAdaptable)i.next()).getAdapter(IRSSElement.class);
                    if(rssElement != null)
                        update(rssElement);
                }
            }
        };
        
        try {
            window.run(true, true, op);
        }
        catch(InvocationTargetException e) {
            RSSUI.getDefault().getLog().log(
            	new Status(
            		Status.ERROR,
					RSSUI.PLUGIN_ID,
					0,
					"Could not toggle element's update status.",
					e));
            MessageDialog.openError(
                    window.getShell(), 
                    "RSS Error",
                    "Could not toggle element's update status. Exception: " + e);
        }
        catch(InterruptedException e) {
            // ignore
        }
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction, org.eclipse.jface.viewers.ISelection)
     */
    public final void selectionChanged(IAction action, ISelection selection) {
        if(selection instanceof IStructuredSelection)
            selectionChanged((IStructuredSelection)selection);
        else
            selectionChanged(StructuredSelection.EMPTY);
            
        action.setEnabled(isEnabled());
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.ui.actions.SelectionListenerAction#updateSelection(org.eclipse.jface.viewers.IStructuredSelection)
     */
    protected boolean updateSelection(IStructuredSelection selection) {
        if(selection.isEmpty())
            return false;
            
        for(Iterator i = selection.iterator(); i.hasNext();) {
            Object o = i.next();
            if(!(o instanceof IAdaptable))
                return false;
                
            IRSSElement rssElement = (IRSSElement)
                ((IAdaptable)o).getAdapter(IRSSElement.class);
            if(rssElement == null)
                return false;
        }
        
        return true;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.action.IAction#run()
     */
    public final void run() {
        run(this);
    }
    
    protected abstract void update(IRSSElement element);
}
