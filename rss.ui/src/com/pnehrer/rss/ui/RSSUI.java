/*
 * Created on Nov 16, 2003
 * Version $Id$
 */
package com.pnehrer.rss.ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdapterManager;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IPluginDescriptor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Preferences;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.help.internal.HelpPlugin;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import com.pnehrer.rss.core.IChannel;
import com.pnehrer.rss.core.IRSSElement;
import com.pnehrer.rss.internal.ui.BrowserEditor;
import com.pnehrer.rss.internal.ui.HelpBrowser;
import com.pnehrer.rss.internal.ui.ImageManager;
import com.pnehrer.rss.internal.ui.LinkBrowserDescriptor;
import com.pnehrer.rss.internal.ui.WorkbenchAdapterFactory;

/**
 * The main plugin class to be used in the desktop.
 * @author <a href="mailto:pnehrer@freeshell.org">Peter Nehrer</a>
 */
public class RSSUI extends AbstractUIPlugin {
    
    public static final String PLUGIN_ID = "com.pnehrer.rss.ui";

    public static final String XML_ICON = "xml_16.gif";
    public static final String NEW_DECORATOR_ICON = "new_dec_16.gif";
    public static final String BROWSE_ICON = "browse_16.png";
    public static final String DETAIL_ICON = "detail_16.png";
    public static final String NAVIGATOR_ICON = "navigator_16.png";
    public static final String NEW_ICON = "new_16.png";
    public static final String UPDATE_ICON = "update_16.png";
    public static final String ITEM_ICON = "item_16.gif";
    public static final String ITEM_NEW_ICON = "item_new_16.gif";
    public static final String TEXT_INPUT_ICON = "textinput_16.png";
    
    public static final String PREF_LINK_BROWSER = "browser";
    private static final QualifiedName PROP_LINK_BROWSER = 
        new QualifiedName(PLUGIN_ID, PREF_LINK_BROWSER);

	//The shared instance.
	private static RSSUI plugin;

	//Resource bundle.
	private ResourceBundle resourceBundle;
    
    private WorkbenchAdapterFactory workbenchAdapterFactory;

    private final Map browserConfig = new HashMap();
    private ILinkBrowserDescriptor[] browserDescriptors; 
    private final Map browserMap = new HashMap();

    private ImageManager imageManager;
	
	/**
	 * The constructor.
	 */
	public RSSUI(IPluginDescriptor descriptor) {
		super(descriptor);
		plugin = this;
		try {
			resourceBundle = 
                ResourceBundle.getBundle("com.pnehrer.rss.ui.RSSUIResources");
		} 
        catch(MissingResourceException x) {
			resourceBundle = null;
		}
	}

	/**
	 * Returns the shared instance.
	 */
	public static RSSUI getDefault() {
		return plugin;
	}

	/**
	 * Returns the string from the plugin's resource bundle,
	 * or 'key' if not found.
	 */
	public static String getResourceString(String key) {
		ResourceBundle bundle= RSSUI.getDefault().getResourceBundle();
		try {
			return bundle.getString(key);
		} 
        catch(MissingResourceException e) {
			return key;
		}
	}

	/**
	 * Returns the plugin's resource bundle,
	 */
	public ResourceBundle getResourceBundle() {
		return resourceBundle;
	}

    /* (non-Javadoc)
     * @see org.eclipse.core.runtime.Plugin#startup()
     */
    public void startup() throws CoreException {
        super.startup();
        imageManager = new ImageManager();
        workbenchAdapterFactory = new WorkbenchAdapterFactory();
        IAdapterManager mgr = Platform.getAdapterManager();        
        mgr.registerAdapters(workbenchAdapterFactory, IRSSElement.class);

        IConfigurationElement configElements[] =
            Platform.getPluginRegistry().getConfigurationElementsFor(
                PLUGIN_ID,
                "linkbrowser");

        Collection list = new ArrayList(configElements.length / 2 + 1);
        for(int i = 0; i < configElements.length; i++) {
            if(configElements[i].getName().equals("linkbrowser")) {
                String id = configElements[i]
                    .getDeclaringExtension()
                    .getDeclaringPluginDescriptor()
                    .getUniqueIdentifier()
                        + "." 
                        + configElements[i].getAttribute("id");
                        
                browserConfig.put(id, configElements[i]);
                list.add(
                    new LinkBrowserDescriptor(
                        id, 
                        configElements[i].getAttribute("label")));
            }
        }
        
        browserDescriptors = (ILinkBrowserDescriptor[])
            list.toArray(new ILinkBrowserDescriptor[list.size()]);
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.runtime.Plugin#shutdown()
     */
    public void shutdown() throws CoreException {
        IAdapterManager mgr = Platform.getAdapterManager();
        mgr.unregisterAdapters(workbenchAdapterFactory);        
        super.shutdown();
    }
    
    public ImageDescriptor getImageDescriptor(IChannel channel) {
        return imageManager.getImageDescriptor(channel);
    }
    
    public ImageDescriptor getImageDescriptor16(IChannel channel) {
        return imageManager.getImageDescriptor16(channel);
    }
    
    public ImageDescriptor getImageDescriptor16x16(IChannel channel) {
        return imageManager.getImageDescriptor16x16(channel);
    }
    
    public ILinkBrowserDescriptor[] getLinkBrowserDescriptors() {
         return browserDescriptors;
    }
    
    public ILinkBrowser getLinkBrowser(IRSSElement rssElement) 
        throws CoreException {
            
        String id = getLinkBrowserId(rssElement);
        synchronized(browserMap) {
            ILinkBrowser browser = (ILinkBrowser)browserMap.get(id);
            if(browser == null) {
                IConfigurationElement configElement = (IConfigurationElement)
                    browserConfig.get(id);
                browser = (ILinkBrowser)
                    configElement.createExecutableExtension("class");
                browserMap.put(id, browser);
            }
            
            return browser;
        }
    }
    
    public String getLinkBrowserId(IRSSElement rssElement) 
        throws CoreException {
            
        IFile file = rssElement.getChannel().getFile();
        String id = file.getPersistentProperty(PROP_LINK_BROWSER);
        if(id == null)
            id = getPluginPreferences().getString(PREF_LINK_BROWSER);
            
        return id;
    }
    
    public void setLinkBrowserId(IRSSElement rssElement, String id) 
        throws CoreException {
        
        rssElement.getChannel().getFile().setPersistentProperty(
            PROP_LINK_BROWSER, 
            id);            
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#initializeImageRegistry(org.eclipse.jface.resource.ImageRegistry)
     */
    protected void initializeImageRegistry(ImageRegistry reg) {
        IPluginDescriptor pd = getDescriptor();
        reg.put(XML_ICON, ImageDescriptor.createFromURL(pd.find(new Path(XML_ICON))));
        reg.put(NEW_DECORATOR_ICON, ImageDescriptor.createFromURL(pd.find(new Path(NEW_DECORATOR_ICON))));
        reg.put(BROWSE_ICON, ImageDescriptor.createFromURL(pd.find(new Path(BROWSE_ICON))));
        reg.put(DETAIL_ICON, ImageDescriptor.createFromURL(pd.find(new Path(DETAIL_ICON))));
        reg.put(NAVIGATOR_ICON, ImageDescriptor.createFromURL(pd.find(new Path(NAVIGATOR_ICON))));
        reg.put(NEW_ICON, ImageDescriptor.createFromURL(pd.find(new Path(NEW_ICON))));
        reg.put(UPDATE_ICON, ImageDescriptor.createFromURL(pd.find(new Path(UPDATE_ICON))));
        reg.put(ITEM_ICON, ImageDescriptor.createFromURL(pd.find(new Path(ITEM_ICON))));
        reg.put(ITEM_NEW_ICON, ImageDescriptor.createFromURL(pd.find(new Path(ITEM_NEW_ICON))));
        reg.put(TEXT_INPUT_ICON, ImageDescriptor.createFromURL(pd.find(new Path(TEXT_INPUT_ICON))));
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.runtime.Plugin#initializeDefaultPluginPreferences()
     */
    protected void initializeDefaultPluginPreferences() {
        Preferences prefs = getPluginPreferences();
        prefs.setDefault(
            PREF_LINK_BROWSER, 
            PLUGIN_ID + ".helpbrowser");
        prefs.setDefault(
            HelpBrowser.PREF_HELP_BROWSER,
            getDefaultBrowserId());
        prefs.setDefault(
            BrowserEditor.PREF_BROWSER_EDITOR,
            getWorkbench().getEditorRegistry().getDefaultEditor().getId());
    }
    
    private String getDefaultBrowserId() {
        // Ugly, but we don't have a choice...
        // get default browser from preferences
        String defBrowserID =
            HelpPlugin.getDefault().getPluginPreferences().getString(
                "default_browser");
        if (defBrowserID != null && (!"".equals(defBrowserID)))
            return defBrowserID;
        // Set default browser to prefered implementation
        if (System.getProperty("os.name").startsWith("Win")) {
            if (Platform
                .getPluginRegistry()
                .getPluginDescriptor("org.eclipse.help.ui")
                != null)
                return "org.eclipse.help.ui.iexplorer";
            else
                return "org.eclipse.help.custombrowser";
        } else if (System.getProperty("os.name").startsWith("Linux")) {
            return "org.eclipse.help.mozillaLinux";
        } else if (System.getProperty("os.name").startsWith("SunOS")) {
            return "org.eclipse.help.netscapeSolaris";
        } else if (System.getProperty("os.name").startsWith("AIX")) {
            return "org.eclipse.help.netscapeAIX";
        } else if (
            System.getProperty("os.name").toLowerCase().startsWith("hp")) {
            return "org.eclipse.help.netscapeHPUX";
        } else {
            return "org.eclipse.help.mozillaLinux";
        }
    }
}
