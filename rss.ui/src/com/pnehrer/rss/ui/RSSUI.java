/*
 * Created on Nov 16, 2003
 * Version $Id$
 */
package com.pnehrer.rss.ui;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdapterManager;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IPluginDescriptor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.help.browser.IBrowser;
import org.eclipse.help.browser.IBrowserFactory;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import com.pnehrer.rss.core.IChannel;
import com.pnehrer.rss.core.IRSSElement;
import com.pnehrer.rss.ui.internal.ImageManager;
import com.pnehrer.rss.ui.internal.WorkbenchAdapterFactory;

/**
 * The main plugin class to be used in the desktop.
 * @author <a href="mailto:pnehrer@freeshell.org">Peter Nehrer</a>
 */
public class RSSUI extends AbstractUIPlugin {
    
    public static final String PLUGIN_ID = "com.pnehrer.rss.ui";

    public static final String XML_ICON = "xml_16.png";
    public static final String BROWSE_ICON = "browse_16.png";
    public static final String DETAIL_ICON = "detail_16.png";
    public static final String NAVIGATOR_ICON = "navigator_16.png";
    public static final String NEW_ICON = "new_16.png";
    public static final String UPDATE_ICON = "update_16.png";
    public static final String ITEM_ICON = "item_16.gif";
    
	//The shared instance.
	private static RSSUI plugin;

	//Resource bundle.
	private ResourceBundle resourceBundle;
    
    private WorkbenchAdapterFactory workbenchAdapterFactory;
    private IBrowserFactory browserFactory;
    private ImageManager imageManager;
	
	/**
	 * The constructor.
	 */
	public RSSUI(IPluginDescriptor descriptor) {
		super(descriptor);
		plugin = this;
		try {
			resourceBundle= ResourceBundle.getBundle("com.pnehrer.rss.ui.RSSUIResources");
		} catch (MissingResourceException x) {
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
		} catch (MissingResourceException e) {
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

    public IBrowser createBrowser() throws CoreException {
        if(browserFactory == null) {
            IConfigurationElement configElements[] =
                Platform.getPluginRegistry().getConfigurationElementsFor(
                    "org.eclipse.help",
                    "browser");
            for (int i = 0; i < configElements.length; i++) {
                if (!configElements[i].getName().equals("browser"))
                    continue;
                Object adapter =
                    configElements[i].createExecutableExtension("factoryclass");
                if (!(adapter instanceof IBrowserFactory))
                    continue;
                if (((IBrowserFactory) adapter).isAvailable()) {
                    browserFactory = (IBrowserFactory)adapter;
                    break;
                }
            }
        }
        
        if(browserFactory == null)
            throw new CoreException(
                new Status(
                    IStatus.ERROR,
                    PLUGIN_ID,
                    0,
                    "could not create browser",
                    null));
        
        return browserFactory.createBrowser();
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
    }
}
