/*
 * Created on Dec 17, 2003
 * Version $Id$
 */
package com.pnehrer.rss.internal.ui;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.IWorkbenchPage;

import com.pnehrer.rss.core.IRSSElement;
import com.pnehrer.rss.core.ITextInput;
import com.pnehrer.rss.ui.ILinkBrowser;
import com.pnehrer.rss.ui.RSSUI;

/**
 * @author <a href="mailto:pnehrer@freeshell.org">Peter Nehrer</a>
 */
public class BrowserEditor implements ILinkBrowser {

    public static final String PREF_BROWSER_EDITOR = "browsereditor";
    private static final QualifiedName PROP_BROWSER_EDITOR =
        new QualifiedName(RSSUI.PLUGIN_ID, PREF_BROWSER_EDITOR);
        
    private final Map editorMap = new HashMap();
    
    public BrowserEditor() {
        IConfigurationElement[] configElements = getEditors();
        for(int i = 0, n = configElements.length; i < n; ++i)
            editorMap.put(
                configElements[i].getAttribute("id"),
                configElements[i]);
    }
    
    public void open(IRSSElement rssElement, IWorkbenchPage page)
        throws CoreException {

        String id = getBrowserEditor(rssElement);
        if(id != null)
            try {
                page.openEditor(new LinkEditorInput(rssElement), id);
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

    public void open(ITextInput textInput, String data, IWorkbenchPage page) 
        throws CoreException {

        String id = getBrowserEditor(textInput);
        if(id != null)
            try {
                page.openEditor(
                    new LinkEditorInput(
                        textInput,
                        new URL(
                            LinkUtil.createURL(
                                textInput.getLink(), 
                                textInput.getName(), 
                                data))), 
                    id);
            }
            catch(MalformedURLException e) {
                throw new CoreException(
                    new Status(
                        IStatus.ERROR,
                        RSSUI.PLUGIN_ID,
                        0,
                        "could not create URL for link " + textInput.getLink(),
                        e));
            }
    }
    
    public static IConfigurationElement[] getEditors() {
        IConfigurationElement configElements[] =
            Platform.getPluginRegistry().getConfigurationElementsFor(
                "org.eclipse.ui.editors");

        Collection list = new ArrayList(configElements.length / 2 + 1);
        for(int i = 0; i < configElements.length; i++) {
            if(configElements[i].getName().equals("editor")) {
                String id = configElements[i].getAttribute("id");
                list.add(configElements[i]);
            }
        }
        
        return (IConfigurationElement[])list.toArray(
            new IConfigurationElement[list.size()]);
    }
    
    public static String getBrowserEditor(IRSSElement rssElement) 
        throws CoreException {
        
        String id = rssElement.getChannel().getFile().getPersistentProperty(
            PROP_BROWSER_EDITOR);
            
        if(id == null)
            id = RSSUI.getDefault().getPluginPreferences().getString(
                PREF_BROWSER_EDITOR);
        
        return id;
    }
    
    public static void setBrowserEditor(IRSSElement rssElement, String id)
        throws CoreException {

        rssElement.getChannel().getFile().setPersistentProperty(
            PROP_BROWSER_EDITOR,
            id);        
    }
}
