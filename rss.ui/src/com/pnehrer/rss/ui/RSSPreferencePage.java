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
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.pnehrer.rss.core.RSSCore;
import com.pnehrer.rss.ui.internal.*;

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
        updateIntervalGroup.setUpdateInterval(
            new Integer(
                RSSCore.getPlugin().getPluginPreferences().getInt(
                    RSSCore.PREF_UPDATE_INTERVAL)));
        
        setErrorMessage(null);
        setMessage(null);

		return topLevel;
	}
    
    /* (non-Javadoc)
     * @see org.eclipse.jface.preference.PreferencePage#performDefaults()
     */
    protected void performDefaults() {
        super.performDefaults();
        RSSCore
            .getPlugin()
            .getPluginPreferences()
            .setToDefault(RSSCore.PREF_UPDATE_INTERVAL);
        updateIntervalGroup.setUpdateInterval(
            new Integer(
                RSSCore.getPlugin().getPluginPreferences().getInt(
                    RSSCore.PREF_UPDATE_INTERVAL)));
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.preference.IPreferencePage#performOk()
     */
    public boolean performOk() {
        Integer updateInterval = updateIntervalGroup.getUpdateInterval();
        if(updateInterval != null)
            RSSCore.getPlugin().getPluginPreferences().setValue(
                RSSCore.PREF_UPDATE_INTERVAL, 
                updateInterval.intValue());
                
        RSSCore.getPlugin().savePluginPreferences();
        return true;
    }
}
