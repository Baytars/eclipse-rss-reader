/*
 * Created on Dec 18, 2003
 * Version $Id$
 */
package com.pnehrer.rss.internal.ui;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;

import com.ibm.etools.webbrowser.WebBrowserEditorInput;
import com.pnehrer.rss.core.IRSSElement;
import com.pnehrer.rss.core.ITextInput;
import com.pnehrer.rss.ui.ILinkBrowser;
import com.pnehrer.rss.ui.RSSUI;

/**
 * @author <a href="mailto:pnehrer@freeshell.org">Peter Nehrer</a>
 */
public class WebBrowserEditor implements ILinkBrowser {
    
    private static final String WEB_BROWSER_ID = "com.ibm.etools.webbrowser";

    /* (non-Javadoc)
     * @see com.pnehrer.rss.ui.ILinkBrowser#open(com.pnehrer.rss.core.IRSSElement, org.eclipse.ui.IWorkbenchPage)
     */
    public void open(IRSSElement rssElement, IWorkbenchPage page) 
        throws CoreException {

        try {
            page.openEditor(
                new WebBrowserEditorInput(new URL(rssElement.getLink())),
                WEB_BROWSER_ID);
        }
        catch(MalformedURLException e) {
            throw new CoreException(
                new Status(
                    IStatus.ERROR,
                    RSSUI.PLUGIN_ID,
                    0,
                    "could not create URL for link " + rssElement.getLink(),
                    e));
        }
    }

    /* (non-Javadoc)
     * @see com.pnehrer.rss.ui.ILinkBrowser#open(com.pnehrer.rss.core.ITextInput, java.lang.String, org.eclipse.ui.IWorkbenchPage)
     */
    public void open(ITextInput textInput, String data, IWorkbenchPage page) 
        throws CoreException {
        
        String link = LinkUtil.createURL(
            textInput.getLink(),
            textInput.getName(), 
            data);
        try {
            page.openEditor(
                new WebBrowserEditorInput(new URL(link)),
                WEB_BROWSER_ID);
        }
        catch(MalformedURLException e) {
            throw new CoreException(
                new Status(
                    IStatus.ERROR,
                    RSSUI.PLUGIN_ID,
                    0,
                    "could not create URL for link " + link,
                    e));
        }
    }
}
