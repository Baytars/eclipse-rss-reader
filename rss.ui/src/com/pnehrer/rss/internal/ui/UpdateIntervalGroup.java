/*
 * Created on Nov 24, 2003
 * Version $Id$
 */
package com.pnehrer.rss.internal.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;


/**
 * @author <a href="mailto:pnehrer@freeshell.org">Peter Nehrer</a>
 */
public class UpdateIntervalGroup {

    private final IPageContainer pageContainer;
    private Integer updateInterval;
    private Button updateIntervalButton;
    private Label updateIntervalLabel;
    private Text updateIntervalText;
    private Label minutesLabel;

    public UpdateIntervalGroup(IPageContainer pageContainer) {
        this.pageContainer = pageContainer;
    }

    public Integer getUpdateInterval() {
        return updateInterval;
    }
    
    public void setUpdateInterval(Integer updateInterval) {
        if(updateInterval != null && updateInterval.intValue() <= 0)
            updateInterval = null;
            
        this.updateInterval = updateInterval;
        if(updateIntervalButton != null) {
            if(updateInterval == null) {
                updateIntervalButton.setSelection(false);
                updateIntervalLabel.setEnabled(false);
//                updateIntervalText.setText("");
                updateIntervalText.setEnabled(false);
                minutesLabel.setEnabled(false);
            }
            else {
                updateIntervalButton.setSelection(true);
                updateIntervalLabel.setEnabled(true);
                updateIntervalText.setText(updateInterval.toString());
                updateIntervalText.setEnabled(true);
                minutesLabel.setEnabled(true);
            }
            
            pageContainer.setComplete(true);
        }
    }

    public void createContents(Composite topLevel) {
        int columns = topLevel.getLayout() instanceof GridLayout ?
            ((GridLayout)topLevel.getLayout()).numColumns :
            3;

        updateIntervalButton = new Button(topLevel, SWT.CHECK);
        GridData layoutData = new GridData(GridData.FILL_HORIZONTAL);
        layoutData.horizontalSpan = columns;
        updateIntervalButton.setLayoutData(layoutData);
        updateIntervalButton.setText("Update periodically");
        updateIntervalButton.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                if(updateIntervalButton.getSelection()) {
                    updateIntervalLabel.setEnabled(true);
                    updateIntervalText.setEnabled(true);
                    minutesLabel.setEnabled(true);
                    if(updateInterval == null || updateInterval.intValue() <= 0) {
                        if(updateIntervalText.getText().trim().length() == 0) {
                            updateInterval = null;
                            pageContainer.setComplete(false);
                            pageContainer.setErrorMessage("Update interval must be a positive integer.");
                        }
                        else {
                            try {
                                updateInterval = new Integer(
                                    updateIntervalText.getText());
                                if(updateInterval.intValue() >= 0) {
                                    pageContainer.setComplete(true);
                                    pageContainer.setErrorMessage(null);
                                }
                                else {
                                    updateInterval = null;
                                    pageContainer.setComplete(false);
                                    pageContainer.setErrorMessage("Update interval must be a positive integer.");
                                }
                            }
                            catch(NumberFormatException ex) {
                                updateInterval = null;
                                pageContainer.setComplete(false);
                                pageContainer.setErrorMessage("Update interval must be a positive integer.");
                            }
                        }
                    }
                    else {
                        updateIntervalText.setText(updateInterval.toString());
                        pageContainer.setComplete(true);
                        pageContainer.setErrorMessage(null);
                    }
                    
                    updateIntervalText.setFocus();
                }
                else {
                    updateIntervalLabel.setEnabled(false);
                    updateIntervalText.setEnabled(false);
                    minutesLabel.setEnabled(false);
                    updateInterval = null;
                    pageContainer.setComplete(true);
                    pageContainer.setErrorMessage(null);
                }
            }
        });
        
        updateIntervalLabel = new Label(topLevel, SWT.SINGLE);
        layoutData = new GridData();
        layoutData.horizontalIndent = 20;
        updateIntervalLabel.setLayoutData(layoutData);
        updateIntervalLabel.setText("Update interval:");

        updateIntervalText = new Text(topLevel, SWT.SINGLE | SWT.BORDER);
        layoutData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
        layoutData.horizontalSpan = columns - 2;
        layoutData.widthHint = 100;
        updateIntervalText.setLayoutData(layoutData);

        updateIntervalText.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                if(updateIntervalText.getText().trim().length() == 0) {
                    updateInterval = null;
                    pageContainer.setComplete(false);
                    pageContainer.setErrorMessage("Update interval must be a positive integer.");
                }
                else {
                    try {
                        updateInterval = new Integer(
                            updateIntervalText.getText());
                        if(updateInterval.intValue() >= 0) {
                            pageContainer.setComplete(true);
                            pageContainer.setErrorMessage(null);
                        }
                        else {
                            updateInterval = null;
                            pageContainer.setComplete(false);
                            pageContainer.setErrorMessage("Update interval must be a positive integer.");
                        }
                    }
                    catch(NumberFormatException ex) {
                        updateInterval = null;
                        pageContainer.setComplete(false);
                        pageContainer.setErrorMessage("Update interval must be a positive integer.");
                    }
                }
            }
        });

        minutesLabel = new Label(topLevel, SWT.SINGLE);
        minutesLabel.setText("minutes");

        setUpdateInterval(updateInterval);
    }
    
    public void setFocus() {
        if(updateIntervalButton.getSelection()) {
            updateIntervalText.selectAll();
            updateIntervalText.setFocus();
        }
        else
            updateIntervalButton.setFocus();
    }
}
