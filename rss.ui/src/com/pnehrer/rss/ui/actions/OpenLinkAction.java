/*
 * Created on Dec 5, 2003
 * Version $Id$
 */
package com.pnehrer.rss.ui.actions;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.help.browser.IBrowser;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorRegistry;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.SelectionListenerAction;

import com.pnehrer.rss.core.IChannel;
import com.pnehrer.rss.core.IRSSElement;
import com.pnehrer.rss.internal.ui.LinkEditorInput;
import com.pnehrer.rss.ui.BrowserFactoryDescriptor;
import com.pnehrer.rss.ui.RSSUI;

/**
 * @author <a href="mailto:pnehrer@freeshell.org">Peter Nehrer</a>
 */
public class OpenLinkAction extends SelectionListenerAction {
    
    private final Shell shell;

    /**
     * @param text
     */
    public OpenLinkAction(Shell shell) {
        super("Open &Link");
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
                return rssElement != null && rssElement.getLink() != null;
            }
        }

        return false;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.action.IAction#run()
     */
    public void run() {
        IStructuredSelection selection = getStructuredSelection();
        IRSSElement item = (IRSSElement)selection.getFirstElement();
        IChannel channel = item.getChannel();
        RSSUI ui = RSSUI.getDefault(); 
        try {
            String choice = ui.getOpenLinkChoice(channel);
            if(RSSUI.PREF_BROWSER.equals(choice)) {
                BrowserFactoryDescriptor bdf = 
                    ui.getBrowserFactoryDescriptor(channel);
                IBrowser browser = bdf.getFactory().createBrowser();
                browser.displayURL(item.getLink());
            }
            else {
                IWorkbench wb = ui.getWorkbench();
                IEditorRegistry reg = wb.getEditorRegistry();
                IEditorDescriptor ed = 
                    reg.findEditor(ui.getOpenLinkEditorId(channel));
                    
                if(ed == null)
                    ed = reg.getDefaultEditor();

                IWorkbenchWindow window = wb.getActiveWorkbenchWindow();
                if(window == null) {
                    IWorkbenchWindow[] windows = wb.getWorkbenchWindows();
                    if(windows != null && windows.length > 0)
                        window = windows[0];
                }
                
                if(window != null) {
                    window.getActivePage().openEditor(
                        new LinkEditorInput(item), 
                        ed.getId(), 
                        true);
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
        catch(Exception ex) {
            MessageDialog.openError(
                shell,
                "Browser Error",
                "Could not open link " + item.getLink() 
                    + ". Exception: " + ex);
        }
    }                    
}
