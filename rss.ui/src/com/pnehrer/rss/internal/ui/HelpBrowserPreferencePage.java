/*
 * Created on Dec 18, 2003
 * Version $Id$
 */
package com.pnehrer.rss.internal.ui;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.pnehrer.rss.ui.RSSUI;

/**
 * @author <a href="mailto:pnehrer@freeshell.org">Peter Nehrer</a>
 */
public class HelpBrowserPreferencePage
    extends PreferencePage
    implements IWorkbenchPreferencePage {

    private IWorkbench workbench;
    private List browserList;
    private final BrowserFactoryDescriptor[] browserDescriptors;

    public HelpBrowserPreferencePage() {
        browserDescriptors = HelpBrowser.getBrowserFactoryDescriptors();
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.preference.PreferencePage#createContents(org.eclipse.swt.widgets.Composite)
     */
    protected Control createContents(Composite parent) {
        initializeDialogUnits(parent);
        Composite topLevel = new Composite(parent,SWT.NONE);
        topLevel.setLayout(new GridLayout());
        topLevel.setLayoutData(new GridData(
            GridData.VERTICAL_ALIGN_FILL | GridData.HORIZONTAL_ALIGN_FILL));
        topLevel.setFont(parent.getFont());

        Label label = new Label(topLevel, SWT.SINGLE);
        label.setText("Select help browser to use for opening RSS links:");
        
        browserList = new List(
            topLevel, 
            SWT.BORDER | SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL);

        browserList.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        for(int i = 0, n = browserDescriptors.length; i < n; ++i)
            browserList.add(browserDescriptors[i].getName());
        
        browserList.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                setValid(browserList.getSelectionCount() == 1);
            }
        });

        setSelection();

        setErrorMessage(null);
        setMessage(null);

        return topLevel;
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
     */
    public void init(IWorkbench workbench) {
        this.workbench = workbench;
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.jface.preference.PreferencePage#performDefaults()
     */
    protected void performDefaults() {
        RSSUI.getDefault().getPluginPreferences().setToDefault(
            HelpBrowser.PREF_HELP_BROWSER);
        setSelection();
        super.performDefaults();
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.preference.IPreferencePage#performOk()
     */
    public boolean performOk() {
        RSSUI.getDefault().getPluginPreferences().setValue(
            HelpBrowser.PREF_HELP_BROWSER,
            browserDescriptors[browserList.getSelectionIndex()].getId());

        return super.performOk();
    }
    
    private void setSelection() {
        String id = RSSUI.getDefault().getPluginPreferences().getString(
            HelpBrowser.PREF_HELP_BROWSER);
        browserList.deselectAll();
        for(int i = 0, n = browserDescriptors.length; i < n; ++i)
            if(browserDescriptors[i].getId().equals(id)) {
                browserList.select(i);
                break;
            }

        setValid(browserList.getSelectionCount() == 1);
    }
}
