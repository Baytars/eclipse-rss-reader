/*
 * Created on Nov 16, 2003
 * Version $Id$
 */
package com.pnehrer.rss.ui;

import java.util.HashMap;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdapterManager;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IPluginDescriptor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Preferences;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.core.runtime.Status;
import org.eclipse.help.browser.IBrowserFactory;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import com.pnehrer.rss.core.IChannel;
import com.pnehrer.rss.core.IRSSElement;
import com.pnehrer.rss.internal.ui.ImageManager;
import com.pnehrer.rss.internal.ui.WorkbenchAdapterFactory;

/**
 * The main plugin class to be used in the desktop.
 * @author <a href="mailto:pnehrer@freeshell.org">Peter Nehrer</a>
 */
public class RSSUI extends AbstractUIPlugin {
    
    public static final String PLUGIN_ID = "com.pnehrer.rss.ui";

    public static final String XML_ICON = "xml_16.gif";
    public static final String BROWSE_ICON = "browse_16.png";
    public static final String DETAIL_ICON = "detail_16.png";
    public static final String NAVIGATOR_ICON = "navigator_16.png";
    public static final String NEW_ICON = "new_16.png";
    public static final String UPDATE_ICON = "update_16.png";
    public static final String ITEM_ICON = "item_16.gif";
    public static final String TEXT_INPUT_ICON = "textinput_16.png";
    
    public static final String PREF_BROWSER = "browser";
    private static final QualifiedName PROP_BROWSER = 
        new QualifiedName(PLUGIN_ID, PREF_BROWSER);
    
	//The shared instance.
	private static RSSUI plugin;

	//Resource bundle.
	private ResourceBundle resourceBundle;
    
    private WorkbenchAdapterFactory workbenchAdapterFactory;
    private volatile Map browserFactoryMap; 
    private ImageManager imageManager;
    private boolean prefsInit;
	
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
    
    private synchronized void initBrowserFactoryDescriptors() 
        throws CoreException {
            
        if(browserFactoryMap == null) {
            IConfigurationElement configElements[] =
                Platform.getPluginRegistry().getConfigurationElementsFor(
                    "org.eclipse.help",
                    "browser");
                    
            Map m = new HashMap(configElements.length / 2 + 1);
            for(int i = 0; i < configElements.length; i++) {
                if(!configElements[i].getName().equals("browser"))
                    continue;

                Object adapter =
                    configElements[i].createExecutableExtension("factoryclass");

                if(!(adapter instanceof IBrowserFactory))
                    continue;

                if(((IBrowserFactory)adapter).isAvailable()) {
                    String id = configElements[i].getAttribute("id"); 
                    BrowserFactoryDescriptor bfd = 
                        new BrowserFactoryDescriptor(
                            id,
                            configElements[i].getAttribute("name"),
                            (IBrowserFactory)adapter);

                    m.put(id, bfd);
                }
            }
            
            browserFactoryMap = m;
        }
    }
    
    public BrowserFactoryDescriptor[] getBrowserFactoryDescriptors() 
        throws CoreException {
        
        if(browserFactoryMap == null)
            initBrowserFactoryDescriptors();    
        
        return (BrowserFactoryDescriptor[])browserFactoryMap.values().toArray(
            new BrowserFactoryDescriptor[browserFactoryMap.size()]);
    }
    
    public BrowserFactoryDescriptor getBrowserFactoryDescriptor(String id)
        throws CoreException {
            
        if(browserFactoryMap == null)
            initBrowserFactoryDescriptors();
            
        return (BrowserFactoryDescriptor)browserFactoryMap.get(id);    
    }
    
    public BrowserFactoryDescriptor getBrowserFactoryDescriptor(
        IChannel channel) 
        throws CoreException {

        if(browserFactoryMap == null)
            initBrowserFactoryDescriptors();
            
        IFile file = channel.getFile();
        String id = file.getPersistentProperty(PROP_BROWSER);
        BrowserFactoryDescriptor bfInfo = id == null ?
            null :
            (BrowserFactoryDescriptor)browserFactoryMap.get(id);

        if(bfInfo == null) {
            id = getPluginPreferences().getString(PREF_BROWSER);
            if(id != null && id.length() > 0)
                bfInfo = (BrowserFactoryDescriptor)browserFactoryMap.get(id);
        }
        
        if(bfInfo == null)
            throw new CoreException(
                new Status(
                    IStatus.ERROR,
                    PLUGIN_ID,
                    0,
                    "could not find browser factory for channel " + channel,
                    null));
        else
            return bfInfo;
    }
    
    public void setBrowserFactoryDescriptor(
        IChannel channel, 
        BrowserFactoryDescriptor bfd) 
        throws CoreException {
        
        if(bfd == null)    
            channel.getFile().setPersistentProperty(PROP_BROWSER, null);
        else
            channel.getFile().setPersistentProperty(PROP_BROWSER, bfd.getId());            
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#initializeImageRegistry(org.eclipse.jface.resource.ImageRegistry)
     */
    protected void initializeImageRegistry(ImageRegistry reg) {
        IPluginDescriptor pd = getDescriptor();
        reg.put(XML_ICON, ImageDescriptor.createFromURL(pd.find(new Path(XML_ICON))));
        reg.put(BROWSE_ICON, ImageDescriptor.createFromURL(pd.find(new Path(BROWSE_ICON))));
        reg.put(DETAIL_ICON, ImageDescriptor.createFromURL(pd.find(new Path(DETAIL_ICON))));
        reg.put(NAVIGATOR_ICON, ImageDescriptor.createFromURL(pd.find(new Path(NAVIGATOR_ICON))));
        reg.put(NEW_ICON, ImageDescriptor.createFromURL(pd.find(new Path(NEW_ICON))));
        reg.put(UPDATE_ICON, ImageDescriptor.createFromURL(pd.find(new Path(UPDATE_ICON))));
        reg.put(ITEM_ICON, ImageDescriptor.createFromURL(pd.find(new Path(ITEM_ICON))));
        reg.put(TEXT_INPUT_ICON, ImageDescriptor.createFromURL(pd.find(new Path(TEXT_INPUT_ICON))));
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.runtime.Plugin#initializeDefaultPluginPreferences()
     */
    protected void initializeDefaultPluginPreferences() {
        if(!prefsInit) {
            prefsInit = true;
            Preferences prefs = getPluginPreferences();
            try {
                if(browserFactoryMap == null)
                    initBrowserFactoryDescriptors();
            }
            catch(CoreException e) {
                // ignore
            }
            
            if(browserFactoryMap != null && browserFactoryMap.size() > 0)
                prefs.setDefault(
                    PREF_BROWSER, 
                    browserFactoryMap.keySet().iterator().next().toString());
        }
    }
    
}
