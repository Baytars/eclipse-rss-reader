/*
 * Created on Nov 16, 2003
 * Version $Id$
 */
package com.pnehrer.rss.ui;

import org.eclipse.ui.plugin.*;
import org.eclipse.core.runtime.*;

import com.pnehrer.rss.core.IChannel;
import com.pnehrer.rss.core.IItem;
import com.pnehrer.rss.ui.internal.WorkbenchAdapterFactory;

import java.util.*;

/**
 * The main plugin class to be used in the desktop.
 * @author <a href="mailto:pnehrer@freeshell.org">Peter Nehrer</a>
 */
public class RSSUI extends AbstractUIPlugin {
    
    public static final String PLUGIN_ID = "com.pnehrer.rss.ui";
	//The shared instance.
	private static RSSUI plugin;
	//Resource bundle.
	private ResourceBundle resourceBundle;
	
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
        WorkbenchAdapterFactory factory = new WorkbenchAdapterFactory();
        IAdapterManager mgr = Platform.getAdapterManager();        
        mgr.registerAdapters(factory, IChannel.class);
        mgr.registerAdapters(factory, IItem.class);
    }
}
