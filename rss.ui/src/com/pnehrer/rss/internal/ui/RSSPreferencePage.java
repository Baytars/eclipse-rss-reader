/*
 * Created on Nov 5, 2003
 * Version $Id$
 */
package com.pnehrer.rss.internal.ui;

import org.eclipse.core.runtime.Preferences;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.pnehrer.rss.core.RSSCore;
import com.pnehrer.rss.ui.RSSUI;

/**
 * @author <a href="mailto:pnehrer@freeshell.org">Peter Nehrer</a>
 * @see PreferencePage
 */
public class RSSPreferencePage 
    extends PreferencePage 
    implements IWorkbenchPreferencePage {

    private static final short UPDATE_INTERVAL_COMPLETE = 1;
    private static final short BROWSER_COMPLETE = 2;
    private static final short PAGE_COMPLETE =
        UPDATE_INTERVAL_COMPLETE
        + BROWSER_COMPLETE;

    private IWorkbench workbench;
    private final UpdateIntervalGroup updateIntervalGroup;
    private final BrowserGroup browserGroup;
    private short pageComplete;

    public RSSPreferencePage() {
        updateIntervalGroup = new UpdateIntervalGroup(new IPageContainer() {
            public void setMessage(String message) {
                RSSPreferencePage.this.setMessage(message);
            }
        
            public void setErrorMessage(String message) {
                RSSPreferencePage.this.setErrorMessage(message);
            }
        
            public void setComplete(boolean complete) {
                RSSPreferencePage.this.setComplete(
                    UPDATE_INTERVAL_COMPLETE, 
                    complete);
            }
        });
        
        browserGroup = new BrowserGroup(new IPageContainer() {
            public void setMessage(String message) {
                RSSPreferencePage.this.setMessage(message);
            }
        
            public void setErrorMessage(String message) {
                RSSPreferencePage.this.setErrorMessage(message);
            }
        
            public void setComplete(boolean complete) {
                RSSPreferencePage.this.setComplete(
                    BROWSER_COMPLETE, 
                    complete);
            }
        });
    }

	/**
	 * @see PreferencePage#init
	 */
	public void init(IWorkbench workbench) {
        this.workbench = workbench;
	}

	/**
	 * @see PreferencePage#createContents
	 */
	protected Control createContents(Composite parent) {
        initializeDialogUnits(parent);
        Composite topLevel = new Composite(parent,SWT.NONE);
        topLevel.setLayout(new GridLayout(3, false));
        topLevel.setLayoutData(new GridData(
            GridData.VERTICAL_ALIGN_FILL | GridData.HORIZONTAL_ALIGN_FILL));
        topLevel.setFont(parent.getFont());

        updateIntervalGroup.createContents(topLevel);
        Integer updateInterval;
        Preferences prefs = RSSCore.getPlugin().getPluginPreferences(); 
        if(prefs.getBoolean(RSSCore.PREF_UPDATE_PERIODICALLY))
            updateInterval = new Integer(prefs.getInt(RSSCore.PREF_UPDATE_INTERVAL));
        else
            updateInterval = null;
            
        updateIntervalGroup.setUpdateInterval(updateInterval);
        
        browserGroup.createContents(topLevel);
        prefs = RSSUI.getDefault().getPluginPreferences();
        browserGroup.setSelectedBrowser(prefs.getString(RSSUI.PREF_LINK_BROWSER));
        
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
        prefs.setToDefault(RSSCore.PREF_UPDATE_PERIODICALLY);
        prefs.setToDefault(RSSCore.PREF_UPDATE_INTERVAL);
        Integer updateInterval;
        if(prefs.getBoolean(RSSCore.PREF_UPDATE_PERIODICALLY))
            updateInterval = new Integer(prefs.getInt(RSSCore.PREF_UPDATE_INTERVAL));
        else
            updateInterval = null;
            
        updateIntervalGroup.setUpdateInterval(updateInterval);
        
        prefs = RSSUI.getDefault().getPluginPreferences();
        prefs.setToDefault(RSSUI.PREF_LINK_BROWSER);
        browserGroup.setSelectedBrowser(
            prefs.getString(RSSUI.PREF_LINK_BROWSER));
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.preference.IPreferencePage#performOk()
     */
    public boolean performOk() {
        Integer updateInterval = updateIntervalGroup.getUpdateInterval();
        Preferences prefs = RSSCore.getPlugin().getPluginPreferences(); 
        if(updateInterval == null) {
            prefs.setValue(RSSCore.PREF_UPDATE_PERIODICALLY, false);
            prefs.setToDefault(RSSCore.PREF_UPDATE_INTERVAL);
        }
        else {
            prefs.setValue(RSSCore.PREF_UPDATE_PERIODICALLY, true);
            prefs.setValue(
                RSSCore.PREF_UPDATE_INTERVAL, 
                updateInterval.intValue());
        }
                
        RSSCore.getPlugin().savePluginPreferences();

        prefs = RSSUI.getDefault().getPluginPreferences();
        String id = browserGroup.getSelectedBrowser();
        if(id == null)
            prefs.setToDefault(RSSUI.PREF_LINK_BROWSER);
        else
            prefs.setValue(RSSUI.PREF_LINK_BROWSER, id);
                            
        RSSUI.getDefault().savePluginPreferences();
        return true;
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
            updateIntervalGroup.setFocus();
    }
}
