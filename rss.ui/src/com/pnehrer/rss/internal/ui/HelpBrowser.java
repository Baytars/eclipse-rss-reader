/*
 * Created on Dec 17, 2003
 * Version $Id$
 */
package com.pnehrer.rss.internal.ui;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.core.runtime.Status;
import org.eclipse.help.browser.IBrowser;
import org.eclipse.help.browser.IBrowserFactory;
import org.eclipse.ui.IWorkbenchPage;

import com.pnehrer.rss.core.IRSSElement;
import com.pnehrer.rss.core.ITextInput;
import com.pnehrer.rss.ui.ILinkBrowser;
import com.pnehrer.rss.ui.RSSUI;

/**
 * @author <a href="mailto:pnehrer@freeshell.org">Peter Nehrer</a>
 */
public class HelpBrowser implements ILinkBrowser {

    public static final String PREF_HELP_BROWSER = "helpbrowser";
    private static final QualifiedName PROP_HELP_BROWSER =
        new QualifiedName(RSSUI.PLUGIN_ID, PREF_HELP_BROWSER);

    private final Map browserFactoryMap = new HashMap();
    
    public HelpBrowser() {
        IConfigurationElement configElements[] =
            Platform.getPluginRegistry().getConfigurationElementsFor(
                "org.eclipse.help",
                "browser");

        for(int i = 0; i < configElements.length; i++) {
            if(configElements[i].getName().equals("browser")) {
                IBrowserFactory factory;
                try {
                    factory = (IBrowserFactory)
                        configElements[i].createExecutableExtension(
                            "factoryclass");
                }
                catch(CoreException e) {
                    continue;
                }

                if(!factory.isAvailable())
                    continue;

                String id = configElements[i].getAttribute("id");
                BrowserFactoryDescriptor bfd = new BrowserFactoryDescriptor(
                    id, 
                    configElements[i].getAttribute("name"),
                    factory);

                browserFactoryMap.put(id, bfd);
            }
        }
    }

    public void open(IRSSElement rssElement, IWorkbenchPage page) 
        throws CoreException {

        open(rssElement, rssElement.getLink());
    }
    
    public void open(ITextInput textInput, String data, IWorkbenchPage page) 
        throws CoreException {

        open(
            textInput, 
            LinkUtil.createURL(textInput.getLink(), textInput.getName(), data));
    }

    private void open(IRSSElement rssElement, String url) throws CoreException {
        String id = getHelpBrowser(rssElement);
        BrowserFactoryDescriptor bfd = 
            (BrowserFactoryDescriptor)browserFactoryMap.get(id);

        if(bfd != null) {
            IBrowser browser = bfd.getFactory().createBrowser();
            try {
                browser.displayURL(url);
            }
            catch(Exception ex) {
                throw new CoreException(
                    new Status(
                        IStatus.ERROR,
                        RSSUI.PLUGIN_ID,
                        0,
                        "could not open browser for URL " + url,
                        ex));
            }
        }
    }

    public static String getHelpBrowser(IRSSElement rssElement) 
        throws CoreException {
        
        String id = rssElement.getChannel().getFile().getPersistentProperty(
            PROP_HELP_BROWSER);
            
        if(id == null)
            id = RSSUI.getDefault().getPluginPreferences().getString(
                PREF_HELP_BROWSER);
                
        return id;
    }
    
    public static void setHelpBrowser(IRSSElement rssElement, String id)
        throws CoreException {

        rssElement.getChannel().getFile().setPersistentProperty(
            PROP_HELP_BROWSER,
            id);        
    }
}
