/*
 * Created on Nov 16, 2003
 * Version $Id$
 */
package com.pnehrer.rss.ui;

import org.eclipse.jface.dialogs.DialogPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * @author <a href="mailto:pnehrer@freeshell.org">Peter Nehrer</a>
 */
public class CorePropertyGroup extends Composite {

    private final Text intervalText;
    private Integer updateInterval;

    /**
     * @param parent
     * @param style
     */
    public CorePropertyGroup(
        final DialogPage dialogPage, 
        Composite parent, 
        int style) {
            
        super(parent, style);
        
        setLayout(new GridLayout(2, false));
        setFont(parent.getFont());

        Label label = new Label(this, SWT.SINGLE);
        label.setText("Update Interval (minutes):");
        
        intervalText = new Text(this, SWT.BORDER);
        intervalText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        intervalText.addModifyListener(new ModifyListener() {
                public void modifyText(ModifyEvent e) {
                    if(intervalText.getText().trim().length() == 0) {
                        updateInterval = null;
                        dialogPage.setErrorMessage(null);
                    }
                    else {
                        try {
                            updateInterval = new Integer(intervalText.getText().trim());
                            dialogPage.setErrorMessage(null);
                        }
                        catch(NumberFormatException ex) {
                            updateInterval = null;
                            dialogPage.setErrorMessage(ex.getMessage());
                        }
                    }
                }
            });
    }
    
    public Integer getUpdateInterval() {
        return updateInterval;
    }
    
    public void setUpdateInterval(Integer interval) {
        this.updateInterval = interval;
        if(interval == null) intervalText.setText("");
        else intervalText.setText(interval.toString());
    }
}
