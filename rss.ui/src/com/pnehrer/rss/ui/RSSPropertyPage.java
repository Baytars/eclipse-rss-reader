/*
 * Created on Nov 16, 2003
 * Version $Id$
 */
package com.pnehrer.rss.ui;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbenchPropertyPage;
import org.eclipse.ui.dialogs.PropertyPage;

import com.pnehrer.rss.core.IChannel;
import com.pnehrer.rss.core.RSSCore;
import com.pnehrer.rss.ui.internal.*;

/**
 * @author <a href="mailto:pnehrer@freeshell.org">Peter Nehrer</a>
 * @see PropertyPage
 */
public class RSSPropertyPage 
    extends PropertyPage 
    implements IWorkbenchPropertyPage {

    private ChannelPropertyGroup channelProperties;
    private final UpdateIntervalGroup updateIntervalGroup;

    public RSSPropertyPage() {
        updateIntervalGroup = new UpdateIntervalGroup(new IPageContainer() {

                public void setMessage(String message) {
                    RSSPropertyPage.this.setMessage(message);
                }
    
                public void setErrorMessage(String message) {
                    RSSPropertyPage.this.setErrorMessage(message);
                }
    
                public void setComplete(boolean complete) {
                    setValid(complete);
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
                    setValid(complete);
                } 
            },
            channel);

        channelProperties.createContents(topLevel);
        updateIntervalGroup.setUpdateInterval(channel.getUpdateInterval());
        updateIntervalGroup.createContents(topLevel);
        
        setErrorMessage(null);
        setMessage(null);

        return topLevel;
	}
    
    /* (non-Javadoc)
     * @see org.eclipse.jface.preference.PreferencePage#performDefaults()
     */
    protected void performDefaults() {
        super.performDefaults();
        updateIntervalGroup.setUpdateInterval(
            new Integer(
                RSSCore.getPlugin().getPluginPreferences().getInt(
                    RSSCore.PREF_UPDATE_INTERVAL)));
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.preference.IPreferencePage#performOk()
     */
    public boolean performOk() {
        IChannel channel = getChannel();
        if(channel == null) return false;
        else {
            channel.setURL(channelProperties.getURL());
            channel.setUpdateInterval(updateIntervalGroup.getUpdateInterval());
            return true;
        }
    }
    
    private IChannel getChannel() {
        IFile file = (IFile)getElement();
        IChannel channel = null;
        try {
            channel = RSSCore.getPlugin().getChannel(file);
        }
        catch(CoreException ex) {
            MessageDialog.openError(
                getShell(), 
                "Error", 
                "Could not obtain channel for file " + file + ". Exception: " + ex);
        }
        
        return channel;        
    }
}
