/*
 * Created on Nov 19, 2003
 * Version $Id$
 */
package com.pnehrer.rss.ui.wizards;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Text;

import com.pnehrer.rss.core.SourceTranslatorDelegate;

/**
 * @author <a href="mailto:pnehrer@freeshell.org">Peter Nehrer</a>
 */
public class WizardChannelOptionsPage extends WizardPage {

    private SourceTranslatorDelegate[] translators;
    private List translatorList;
    private Integer updateInterval;
    private Text updateIntervalText;

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
        GridData layoutData = new GridData();
        layoutData.horizontalSpan = 2;
        label.setLayoutData(layoutData);
        label.setText("Select channel source translator:");
        
        translatorList = new List(topLevel, SWT.SINGLE | SWT.BORDER);
        layoutData = new GridData(GridData.FILL_BOTH);
        layoutData.horizontalSpan = 2;
        translatorList.setLayoutData(layoutData);
        translatorList.addSelectionListener(new SelectionAdapter() {
                public void widgetSelected(SelectionEvent e) {
                    setPageComplete(translatorList.getSelectionCount() == 1);
                }
            });
            
        label = new Label(topLevel, SWT.SINGLE);
        label.setText("Update interval (minutes):");

        updateIntervalText = new Text(topLevel, SWT.SINGLE | SWT.BORDER);
        updateIntervalText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        
        setErrorMessage(null);
        setMessage(null);
        setControl(topLevel);
    }

    public void setTranslators(SourceTranslatorDelegate[] translators) {
        this.translators = translators;
        String[] items = new String[translators.length];
        for(int i = 0, n = items.length; i < n; ++i)
            items[i] = translators[i].getDescription();
            
        translatorList.deselectAll();
        translatorList.setItems(items);
    }
    
    public SourceTranslatorDelegate getTranslator() {
        return translators[translatorList.getSelectionIndex()];
    }
    
    public Integer getUpdateInterval() {
        return updateInterval = new Integer(updateIntervalText.getText());
    }
}
