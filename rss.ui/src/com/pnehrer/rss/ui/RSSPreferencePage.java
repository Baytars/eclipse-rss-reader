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

/**
 * @author <a href="mailto:pnehrer@freeshell.org">Peter Nehrer</a>
 * @see PreferencePage
 */
public class RSSPreferencePage 
    extends PreferencePage 
    implements IWorkbenchPreferencePage {

    private IWorkbench workbench;
    private CorePropertyGroup coreProperties;

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
        topLevel.setLayout(new GridLayout());
        topLevel.setLayoutData(new GridData(
            GridData.VERTICAL_ALIGN_FILL | GridData.HORIZONTAL_ALIGN_FILL));
        topLevel.setFont(parent.getFont());

        coreProperties = new CorePropertyGroup(this, topLevel, SWT.SHADOW_NONE);
        coreProperties.setLayoutData(new GridData(GridData.FILL_BOTH));
        coreProperties.setUpdateInterval(
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
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.preference.IPreferencePage#performOk()
     */
    public boolean performOk() {
        Integer updateInterval = coreProperties.getUpdateInterval();
        if(updateInterval != null)
            RSSCore.getPlugin().getPluginPreferences().setValue(
                RSSCore.PREF_UPDATE_INTERVAL, 
                updateInterval.intValue());
                
        return true;
    }
}
