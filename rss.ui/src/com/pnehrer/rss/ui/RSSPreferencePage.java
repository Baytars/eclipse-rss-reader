/*
 * Created on Nov 5, 2003
 * Version $Id$
 */
package com.pnehrer.rss.ui;

import org.eclipse.ui.IWorkbench;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.core.runtime.Preferences;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.pnehrer.rss.core.RSSCore;
import com.pnehrer.rss.internal.ui.*;

/**
 * @author <a href="mailto:pnehrer@freeshell.org">Peter Nehrer</a>
 * @see PreferencePage
 */
public class RSSPreferencePage 
    extends PreferencePage 
    implements IWorkbenchPreferencePage {

    private final UpdateIntervalGroup updateIntervalGroup;
    private IWorkbench workbench;

    public RSSPreferencePage() {
        updateIntervalGroup = new UpdateIntervalGroup(new IPageContainer() {

            public void setMessage(String message) {
                RSSPreferencePage.this.setMessage(message);
            }

            public void setErrorMessage(String message) {
                RSSPreferencePage.this.setErrorMessage(message);
            }

            public void setComplete(boolean complete) {
                setValid(complete);
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
        return true;
    }
}
