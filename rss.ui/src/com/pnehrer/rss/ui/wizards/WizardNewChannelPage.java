/*
 * Created on Nov 14, 2003
 * Version $Id$
 */
package com.pnehrer.rss.ui.wizards;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.pnehrer.rss.core.RSSCore;
import com.pnehrer.rss.ui.CorePropertyGroup;

/**
 * @author <a href="mailto:pnehrer@freeshell.org">Peter Nehrer</a>
 */
public class WizardNewChannelPage extends WizardPage {

    private URL url;
    private Text urlText;
    private CorePropertyGroup coreProperties;

    /**
     * @param pageName
     * @param title
     * @param titleImage
     */
    public WizardNewChannelPage(
        String pageName,
        String title,
        ImageDescriptor titleImage) {

        super(pageName, title, titleImage);
        setPageComplete(false);
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
     */
    public void createControl(Composite parent) {
        initializeDialogUnits(parent);
        Composite topLevel = new Composite(parent,SWT.NONE);
        topLevel.setLayout(new GridLayout(2, false));
        topLevel.setLayoutData(new GridData(
            GridData.VERTICAL_ALIGN_FILL | GridData.HORIZONTAL_ALIGN_FILL));
        topLevel.setFont(parent.getFont());

        Label label = new Label(topLevel, SWT.SINGLE);
        label.setText("RSS Feed URL:");
        
        urlText = new Text(topLevel, SWT.BORDER);
        urlText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        urlText.addModifyListener(new ModifyListener() {
                public void modifyText(ModifyEvent e) {
                    if(urlText.getText().trim().length() == 0) {
                        url = null;
                        setPageComplete(false);
                    }
                    else {
                        try {
                            url = new URL(urlText.getText().trim());
                            setPageComplete(true);
                        }
                        catch(MalformedURLException ex) {
                            url = null;
                            setPageComplete(false);
                        }
                    }
                }
            });
        
        coreProperties = new CorePropertyGroup(this, topLevel, SWT.SHADOW_NONE);
        coreProperties.setLayoutData(new GridData(GridData.FILL_BOTH));
        coreProperties.setUpdateInterval(
            new Integer(
                RSSCore
                    .getPlugin()
                    .getPluginPreferences()
                    .getInt(RSSCore.PREF_UPDATE_INTERVAL)));
        
        setErrorMessage(null);
        setMessage(null);
        setControl(topLevel);
    }
    
    public URL getURL() {
        return url;
    }
    
    public Integer getUpdateInterval() {
        return coreProperties.getUpdateInterval();
    }
}
