/*
 * Created on Nov 16, 2003
 * Version $Id$
 */
package com.pnehrer.rss.ui;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Preferences;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbenchPropertyPage;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.eclipse.ui.dialogs.PropertyPage;

import com.pnehrer.rss.core.IChannel;
import com.pnehrer.rss.core.IRSSElement;
import com.pnehrer.rss.core.RSSCore;
import com.pnehrer.rss.internal.ui.BrowserGroup;
import com.pnehrer.rss.internal.ui.ChannelPropertyGroup;
import com.pnehrer.rss.internal.ui.UpdateIntervalGroup;

/**
 * @author <a href="mailto:pnehrer@freeshell.org">Peter Nehrer</a>
 * @see PropertyPage
 */
public class RSSPropertyPage 
    extends PropertyPage 
    implements IWorkbenchPropertyPage {

    private static final short CHANNEL_OPTIONS_COMPLETE = 1;
    private static final short UPDATE_INTERVAL_COMPLETE = 2;
    private static final short BROWSER_COMPLETE = 4;
    private static final short PAGE_COMPLETE =
        CHANNEL_OPTIONS_COMPLETE
        + UPDATE_INTERVAL_COMPLETE
        + BROWSER_COMPLETE;

    private ChannelPropertyGroup channelProperties;
    private final UpdateIntervalGroup updateIntervalGroup;
    private final BrowserGroup browserGroup;
    private short pageComplete;

    public RSSPropertyPage() {
        updateIntervalGroup = new UpdateIntervalGroup(new IPageContainer() {
            public void setMessage(String message) {
                RSSPropertyPage.this.setMessage(message);
            }
        
            public void setErrorMessage(String message) {
                RSSPropertyPage.this.setErrorMessage(message);
            }
        
            public void setComplete(boolean complete) {
                RSSPropertyPage.this.setComplete(
                    UPDATE_INTERVAL_COMPLETE, 
                    complete);
            }
        });
        
        browserGroup = new BrowserGroup(new IPageContainer() {
            public void setMessage(String message) {
                RSSPropertyPage.this.setMessage(message);
            }
        
            public void setErrorMessage(String message) {
                RSSPropertyPage.this.setErrorMessage(message);
            }
        
            public void setComplete(boolean complete) {
                RSSPropertyPage.this.setComplete(
                    BROWSER_COMPLETE, 
                    complete);
            }
        });
    }

	/**
	 * @see PropertyPage#createContents
	 */
	protected Control createContents(Composite parent) {
        initializeDialogUnits(parent);
        Composite topLevel = new Composite(parent,SWT.NONE);
        topLevel.setLayout(new GridLayout(3, false));
        topLevel.setLayoutData(new GridData(
            GridData.VERTICAL_ALIGN_FILL | GridData.HORIZONTAL_ALIGN_FILL));
        topLevel.setFont(parent.getFont());

        IChannel channel = getChannel();
        channelProperties = new ChannelPropertyGroup(new IPageContainer() {

                public void setMessage(String message) {
                    RSSPropertyPage.this.setMessage(message);
                }
    
                public void setErrorMessage(String message) {
                    RSSPropertyPage.this.setErrorMessage(message);
                }
    
                public void setComplete(boolean complete) {
                    RSSPropertyPage.this.setComplete(
                        CHANNEL_OPTIONS_COMPLETE,
                        complete);
                } 
            },
            channel);

        channelProperties.createContents(topLevel);

        updateIntervalGroup.createContents(topLevel);
        updateIntervalGroup.setUpdateInterval(channel.getUpdateInterval());
        
        browserGroup.createContents(topLevel);
        try {
            browserGroup.setSelectedBrowserFactory(
                RSSUI.getDefault().getBrowserFactoryDescriptor(channel));
        }
        catch(CoreException e) {
            // ignore
        }
        
        setErrorMessage(null);
        setMessage(null);

        return topLevel;
	}
    
    /* (non-Javadoc)
     * @see org.eclipse.jface.preference.PreferencePage#performDefaults()
     */
    protected void performDefaults() {
        super.performDefaults();
        Preferences prefs = RSSCore.getPlugin().getPluginPreferences(); 
        Integer updateInterval;
        if(prefs.getBoolean(RSSCore.PREF_UPDATE_PERIODICALLY))
            updateInterval = new Integer(
                prefs.getInt(RSSCore.PREF_UPDATE_INTERVAL));
        else
            updateInterval = null;
            
        updateIntervalGroup.setUpdateInterval(updateInterval);
        
        prefs = RSSUI.getDefault().getPluginPreferences();
        try {
            browserGroup.setSelectedBrowserFactory(
                RSSUI.getDefault().getBrowserFactoryDescriptor(
                    prefs.getString(RSSUI.PREF_BROWSER)));
        }
        catch(CoreException e) {
            // ignore
        }
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.preference.IPreferencePage#performOk()
     */
    public boolean performOk() {
        final IChannel channel = getChannel();
        if(channel == null) return false;
        else {
            ProgressMonitorDialog dlg = new ProgressMonitorDialog(getShell());
            try {
                dlg.run(false, true, new WorkspaceModifyOperation() {
                    protected void execute(IProgressMonitor monitor) 
                        throws CoreException, 
                            InvocationTargetException, 
                            InterruptedException {
            
                        channel.setURL(channelProperties.getURL());
                        channel.setUpdateInterval(
                            updateIntervalGroup.getUpdateInterval());
            
                        channel.save(monitor);
                        
                        RSSUI.getDefault().setBrowserFactoryDescriptor(
                            channel, 
                            browserGroup.getSelectedBrowserFactory());
                    }
                });
            }
            catch(InvocationTargetException ex) {
                MessageDialog.openError(
                    getShell(), 
                    "Channel Error", 
                    "Could not save channel. Exception: " + ex);

                return false;
            }
            catch(InterruptedException ex) {
                // ignore
            }

            return true;
        }
    }
    
    private IChannel getChannel() {
        IRSSElement rssElement = (IRSSElement)
            getElement().getAdapter(IRSSElement.class);
        return rssElement.getChannel();
    }
    
    private void setComplete(short bit, boolean complete) {
        if(complete) {
            pageComplete |= bit;
            setValid(pageComplete == PAGE_COMPLETE);
        }
        else {
            pageComplete &= ~bit;
            setValid(false);  
        }
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.dialogs.IDialogPage#setVisible(boolean)
     */
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        if(visible)
            channelProperties.setFocus();
    }
}
