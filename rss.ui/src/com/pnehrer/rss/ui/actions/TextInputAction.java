/*
 * Created on Dec 16, 2003
 * Version $Id$
 */
package com.pnehrer.rss.ui.actions;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.SelectionListenerAction;

import com.pnehrer.rss.core.IRSSElement;
import com.pnehrer.rss.core.ITextInput;
import com.pnehrer.rss.ui.ILinkBrowser;
import com.pnehrer.rss.ui.RSSUI;

/**
 * @author <a href="mailto:pnehrer@freeshell.org">Peter Nehrer</a>
 */
public class TextInputAction extends SelectionListenerAction {

    private final Shell shell;

    /**
     * @param text
     */
    public TextInputAction(Shell shell) {
        super("Text &Input...");
        this.shell = shell;
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.actions.SelectionListenerAction#updateSelection(org.eclipse.jface.viewers.IStructuredSelection)
     */
    protected boolean updateSelection(IStructuredSelection selection) {
        if(super.updateSelection(selection)) {
            Object object = selection.getFirstElement();
            if(object instanceof IAdaptable) {
                IRSSElement rssElement = (IRSSElement)
                    ((IAdaptable)object).getAdapter(IRSSElement.class);
                return rssElement != null 
                    && rssElement.getChannel().getTextInput() != null;
            }
        }

        return false;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.action.IAction#run()
     */
    public void run() {
        IStructuredSelection selection = getStructuredSelection();
        Object element = selection.getFirstElement();
        IRSSElement item = null;
        if(element instanceof IAdaptable)
            item = (IRSSElement)
                ((IAdaptable)element).getAdapter(IRSSElement.class);
        
        if(item == null)
            return;

        ITextInput textInput = item.getChannel().getTextInput();
        if(textInput == null)
            return;
            
        InputDialog dlg = new InputDialog(
            shell, 
            textInput.getTitle(),
            textInput.getDescription(),
            null,
            null);
                
        dlg.open();
        String term = dlg.getValue();

        if(term != null) {
            RSSUI ui = RSSUI.getDefault(); 
            try {
                ILinkBrowser linkBrowser = ui.getLinkBrowser(item);
                if(linkBrowser != null) {
                    IWorkbench wb = ui.getWorkbench();
                    IWorkbenchWindow window = wb.getActiveWorkbenchWindow();
                    if(window == null) {
                        IWorkbenchWindow[] windows = wb.getWorkbenchWindows();
                        if(windows != null && windows.length > 0)
                            window = windows[0];
                    }
                
                    if(window != null) {
                        linkBrowser.open(
                            textInput, 
                            term, 
                            window.getActivePage());
                    }
                }
            }
            catch(CoreException ex) {
                ErrorDialog.openError(
                    shell,
                    "Browser Error",
                    "Could not open browser.",
                    ex.getStatus());
            }
        }
    }
}
