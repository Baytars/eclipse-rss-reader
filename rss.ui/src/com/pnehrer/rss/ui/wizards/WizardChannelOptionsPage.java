/*
 * Created on Nov 19, 2003
 * Version $Id$
 */
package com.pnehrer.rss.ui.wizards;

import java.net.URL;

import org.eclipse.core.runtime.Preferences;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.w3c.dom.Document;

import com.pnehrer.rss.core.IRegisteredTranslator;
import com.pnehrer.rss.core.RSSCore;
import com.pnehrer.rss.ui.IPageContainer;
import com.pnehrer.rss.internal.ui.ChannelPropertyGroup;
import com.pnehrer.rss.internal.ui.UpdateIntervalGroup;

/**
 * @author <a href="mailto:pnehrer@freeshell.org">Peter Nehrer</a>
 */
public class WizardChannelOptionsPage extends WizardPage {

    private static final IRegisteredTranslator[] NO_TRANSLATORS = {};
    private static final short CHANNEL_OPTIONS_COMPLETE = 1;
    private static final short UPDATE_INTERVAL_COMPLETE = 2;
    private static final short PAGE_COMPLETE =
        CHANNEL_OPTIONS_COMPLETE
        + UPDATE_INTERVAL_COMPLETE;

    private ChannelPropertyGroup channelProperties;
    private final UpdateIntervalGroup updateIntervalGroup;
    private short pageComplete;

    /**
     * @param pageName
     * @param title
     * @param titleImage
     */
    public WizardChannelOptionsPage(
        String pageName,
        String title,
        ImageDescriptor titleImage) {

        super(pageName, title, titleImage);
        updateIntervalGroup = new UpdateIntervalGroup(new IPageContainer() {

            public void setMessage(String message) {
                WizardChannelOptionsPage.this.setMessage(message);
            }

            public void setErrorMessage(String message) {
                WizardChannelOptionsPage.this.setErrorMessage(message);
            }

            public void setComplete(boolean complete) {
                WizardChannelOptionsPage.this.setComplete(
                    UPDATE_INTERVAL_COMPLETE, 
                    complete);
            }
        });
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
     */
    public void createControl(Composite parent) {
        initializeDialogUnits(parent);
        Composite topLevel = new Composite(parent,SWT.NONE);
        topLevel.setLayout(new GridLayout(3, false));
        topLevel.setLayoutData(new GridData(
            GridData.VERTICAL_ALIGN_FILL | GridData.HORIZONTAL_ALIGN_FILL));
        topLevel.setFont(parent.getFont());

        channelProperties = new ChannelPropertyGroup(new IPageContainer() {

            public void setMessage(String message) {
                WizardChannelOptionsPage.this.setMessage(message);
            }

            public void setErrorMessage(String message) {
                WizardChannelOptionsPage.this.setErrorMessage(message);
            }

            public void setComplete(boolean complete) {
                WizardChannelOptionsPage.this.setComplete(
                    CHANNEL_OPTIONS_COMPLETE,
                    complete);
            } 
        });

        channelProperties.createContents(topLevel);
        updateIntervalGroup.createContents(topLevel);
        Integer updateInterval;
        Preferences prefs = RSSCore.getPlugin().getPluginPreferences(); 
        if(prefs.getBoolean(RSSCore.PREF_UPDATE_PERIODICALLY))
            updateInterval = new Integer(prefs.getInt(RSSCore.PREF_UPDATE_INTERVAL));
        else
            updateInterval = null;
            
        updateIntervalGroup.setUpdateInterval(updateInterval);
        
        setErrorMessage(null);
        setMessage(null);
        setControl(topLevel);
    }
    
    public URL getURL() {
        return channelProperties.getURL();
    }
    
    public Document getDocument() {
        return channelProperties.getDocument();
    }
    
    public IRegisteredTranslator getTranslator() {
        return channelProperties.getTranslator();
    }
    
    public Integer getUpdateInterval() {
        return updateIntervalGroup.getUpdateInterval();
    }

    private void setComplete(short bit, boolean complete) {
        if(complete) {
            pageComplete |= bit;
            setPageComplete(pageComplete == PAGE_COMPLETE);
        }
        else {
            pageComplete &= ~bit;
            setPageComplete(false);  
        }
    }
}
