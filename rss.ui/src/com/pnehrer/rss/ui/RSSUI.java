/*
 * Created on Nov 16, 2003
 * Version $Id$
 */
package com.pnehrer.rss.ui;

import java.net.Authenticator;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.URL;
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
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Preferences;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.help.internal.browser.BrowserManager;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import com.pnehrer.rss.core.IChannel;
import com.pnehrer.rss.core.IRSSElement;
import com.pnehrer.rss.internal.ui.ImageManager;
import com.pnehrer.rss.internal.ui.WorkbenchAdapterFactory;
import com.pnehrer.rss.internal.ui.linkbrowser.BrowserEditor;
import com.pnehrer.rss.internal.ui.linkbrowser.HelpBrowser;
import com.pnehrer.rss.internal.ui.linkbrowser.LinkBrowserDescriptor;

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
    
    public static final String PREF_USE_AUTHENTICATOR = "useAuthenticator";

	//The shared instance.
	private static RSSUI plugin;

	//Resource bundle.
	private ResourceBundle resourceBundle;
    
    private WorkbenchAdapterFactory workbenchAdapterFactory;

    private final Map browserConfig = new HashMap();
    private ILinkBrowserDescriptor[] browserDescriptors; 
    private final Map browserMap = new HashMap();

    private ImageManager imageManager;
    private Authenticator authenticator;
	
	/**
	 * The constructor.
	 */
	public RSSUI() {
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
     * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
     */
    public void start(BundleContext context) throws Exception {
        super.start(context);
        imageManager = new ImageManager();
        workbenchAdapterFactory = new WorkbenchAdapterFactory();
        IAdapterManager mgr = Platform.getAdapterManager();        
        mgr.registerAdapters(workbenchAdapterFactory, IRSSElement.class);

        IConfigurationElement configElements[] =
            Platform.getExtensionRegistry().getConfigurationElementsFor(
                PLUGIN_ID,
                "linkbrowser");

        Collection list = new ArrayList(configElements.length / 2 + 1);
        for(int i = 0; i < configElements.length; i++) {
            if(configElements[i].getName().equals("linkbrowser")) {
                String id = configElements[i]
                    .getDeclaringExtension()
                    .getNamespace()
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
        
        setUseAuthenticator(
        		getPluginPreferences().getBoolean(PREF_USE_AUTHENTICATOR));
    }

    /* (non-Javadoc)
     * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
     */
    public void stop(BundleContext context) throws Exception {
        IAdapterManager mgr = Platform.getAdapterManager();
        mgr.unregisterAdapters(workbenchAdapterFactory);        
        super.stop(context);
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

    public void setUseAuthenticator(boolean value) {
    	if (!value) {
    		if (authenticator != null)
    			Authenticator.setDefault(null);
    		
    		return;
    	}
    	
    	if (authenticator != null) {
    		Authenticator.setDefault(authenticator);
    		return;
    	}
    	
        authenticator = new Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				final URL url;
				try {
					url = new URL(
							getRequestingProtocol(), 
							getRequestingHost(), 
							getRequestingPort(), 
							"/");
				} catch (MalformedURLException e) {
					return null;
				}
				
				Map info = Platform.getAuthorizationInfo(
						url, 
						getRequestingPrompt(), 
						"");
				
				if (info == null) {
					final Display display = getWorkbench().getDisplay();
					if (display != null && !display.isDisposed()) {
						display.syncExec(new Runnable() {
							public void run() {
								new Dialog(display.getActiveShell()) {
									private Text usernameText;
									private Text passwordText;
									protected Control createDialogArea(Composite parent) {
										Composite composite = (Composite) super.createDialogArea(parent);
										((GridLayout) composite.getLayout()).numColumns = 2;
										Label label = new Label(composite, SWT.NONE);
										GridData gd = new GridData();
										gd.horizontalSpan = 2;
										label.setLayoutData(gd);
										label.setFont(composite.getFont());
										label.setText("Please enter username and password for ");
										label = new Label(composite, SWT.NONE);
										gd = new GridData();
										gd.horizontalSpan = 2;
										label.setLayoutData(gd);
										label.setFont(composite.getFont());
										label.setText(getRequestingPrompt() + " at " + url + ":");
										label = new Label(composite, SWT.NONE);
										label.setFont(composite.getFont());
										label.setText("User ID:");
										usernameText = new Text(composite, SWT.BORDER);
										usernameText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
										usernameText.setFont(composite.getFont());
										usernameText.addModifyListener(new ModifyListener() {
											public void modifyText(ModifyEvent e) {
												getButton(IDialogConstants.OK_ID).setEnabled(
														usernameText.getText().trim().length() > 0);
											}
										});
										
										label = new Label(composite, SWT.NONE);
										label.setFont(composite.getFont());
										label.setText("Password:");
										passwordText = new Text(composite, SWT.PASSWORD | SWT.BORDER);
										passwordText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
										passwordText.setFont(composite.getFont());
										return composite;
									}
									protected void okPressed() {
										HashMap info = new HashMap(2);
										info.put("username", usernameText.getText().trim());
										info.put("password", passwordText.getText().trim());
										try {
											Platform.addProtectionSpace(url, getRequestingPrompt());
											Platform.addAuthorizationInfo(
													url, 
													getRequestingPrompt(), 
													"", 
													info);
										} catch (CoreException e) {
											getLog().log(e.getStatus());
										}

										super.okPressed();
									}
									protected void createButtonsForButtonBar(Composite parent) {
										super.createButtonsForButtonBar(parent);
										getButton(IDialogConstants.OK_ID).setEnabled(false);
									}
									protected void configureShell(Shell newShell) {
										super.configureShell(newShell);
										newShell.setText("Authentication");
									}
								}.open();
							}
						});
						
						info = Platform.getAuthorizationInfo(
								url, 
								getRequestingPrompt(), 
								"");
					}
				}				

				if (info == null)
					return null;
				else {
					String username = String.valueOf(info.get("username"));
					Object password = info.get("password");
					return new PasswordAuthentication(
							username, 
							password == null 
								? null 
								: password.toString().toCharArray());
				}
			}
        };
        
        Authenticator.setDefault(authenticator);
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#initializeImageRegistry(org.eclipse.jface.resource.ImageRegistry)
     */
    protected void initializeImageRegistry(ImageRegistry reg) {
        reg.put(XML_ICON, ImageDescriptor.createFromURL(Platform.find(getBundle(), new Path(XML_ICON))));
        reg.put(NEW_DECORATOR_ICON, ImageDescriptor.createFromURL(Platform.find(getBundle(), new Path(NEW_DECORATOR_ICON))));
        reg.put(BROWSE_ICON, ImageDescriptor.createFromURL(Platform.find(getBundle(), new Path(BROWSE_ICON))));
        reg.put(DETAIL_ICON, ImageDescriptor.createFromURL(Platform.find(getBundle(), new Path(DETAIL_ICON))));
        reg.put(NAVIGATOR_ICON, ImageDescriptor.createFromURL(Platform.find(getBundle(), new Path(NAVIGATOR_ICON))));
        reg.put(NEW_ICON, ImageDescriptor.createFromURL(Platform.find(getBundle(), new Path(NEW_ICON))));
        reg.put(UPDATE_ICON, ImageDescriptor.createFromURL(Platform.find(getBundle(), new Path(UPDATE_ICON))));
        reg.put(ITEM_ICON, ImageDescriptor.createFromURL(Platform.find(getBundle(), new Path(ITEM_ICON))));
        reg.put(ITEM_NEW_ICON, ImageDescriptor.createFromURL(Platform.find(getBundle(), new Path(ITEM_NEW_ICON))));
        reg.put(TEXT_INPUT_ICON, ImageDescriptor.createFromURL(Platform.find(getBundle(), new Path(TEXT_INPUT_ICON))));
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.runtime.Plugin#initializeDefaultPluginPreferences()
     */
    protected void initializeDefaultPluginPreferences() {
        Preferences prefs = getPluginPreferences();
        prefs.setDefault(
            PREF_LINK_BROWSER, 
            PLUGIN_ID + ".browsereditor");
        prefs.setDefault(PREF_USE_AUTHENTICATOR, true);
    	// TODO Don't use internal API!
        prefs.setDefault(
            HelpBrowser.PREF_HELP_BROWSER,
            BrowserManager.getInstance().getDefaultBrowserID());
        prefs.setDefault(
            BrowserEditor.PREF_BROWSER_EDITOR,
            PLUGIN_ID + ".browser");
    }
}
