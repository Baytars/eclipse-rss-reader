/*
 * Created on Dec 16, 2003
 * Version $Id$
 */
package com.pnehrer.rss.ui.actions;

import java.net.URLEncoder;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.help.browser.IBrowser;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.actions.SelectionListenerAction;

import com.pnehrer.rss.core.IChannel;
import com.pnehrer.rss.core.IRSSElement;
import com.pnehrer.rss.core.ITextInput;
import com.pnehrer.rss.ui.BrowserFactoryDescriptor;
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
        IRSSElement item = (IRSSElement)selection.getFirstElement();
        IChannel channel = item.getChannel();
        ITextInput textInput = channel.getTextInput();
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
            String link = textInput.getLink();
            StringBuffer buf = new StringBuffer(link);
            int i = link.lastIndexOf('/');
            String lastSegment = i >= 0 ? link.substring(i) : link;
            if((i = lastSegment.lastIndexOf('?')) >= 0) {
                if(i < link.length() - 1)
                    buf.append('&'); 
            }
            else 
                buf.append('?');
                
            buf.append(textInput.getName());
            buf.append('=');
            buf.append(URLEncoder.encode(term));
            String url = buf.toString();
                
            try {
                BrowserFactoryDescriptor bdf = 
                    RSSUI.getDefault().getBrowserFactoryDescriptor(
                        item.getChannel());

                IBrowser browser = bdf.getFactory().createBrowser();
                browser.displayURL(url);
                
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
                    "Could not open URL " + url 
                        + ". Exception: " + ex);
            }
        }
    }
}
