/*
 * Created on Jan 16, 2004
 * Version $Id$
 */
package com.pnehrer.rss.ui.actions;

import org.eclipse.core.runtime.Preferences;
import org.eclipse.help.browser.IBrowser;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;

import com.pnehrer.rss.internal.ui.linkbrowser.BrowserFactoryDescriptor;
import com.pnehrer.rss.internal.ui.linkbrowser.HelpBrowser;
import com.pnehrer.rss.ui.RSSUI;

/**
 * @author <a href="mailto:pnehrer@freeshell.org">Peter Nehrer</a>
 */
public class OpenHomePageAction extends Action {

    private static final String HOME_PAGE_URL = "http://morphine.sourceforge.net/";
    private final BrowserFactoryDescriptor[] bfd;
    
    public OpenHomePageAction() {
        bfd = HelpBrowser.getBrowserFactoryDescriptors();
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.action.IAction#run()
     */
    public void run() {
        Preferences prefs = RSSUI.getDefault().getPluginPreferences();
        String browserId = prefs.getString(HelpBrowser.PREF_HELP_BROWSER);
        for(int i = 0; i < bfd.length; ++i)
            if(browserId.equals(bfd[i].getId())) {
                IBrowser browser = bfd[i].getFactory().createBrowser();
                if(browser != null)
                    try {
                        browser.displayURL(HOME_PAGE_URL);
                    }
                    catch(Exception e) {
                        MessageDialog.openError(
                            Display.getCurrent().getActiveShell(),
                            "Browser Error",
                            "Could not open browser. Exception: " + e);
                    }
            }
    }
}
