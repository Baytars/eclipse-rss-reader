/*
 * Created on 3/13/2004
 * Version $Id$
 */
package com.pnehrer.rss.internal.ui.search;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.dialogs.DialogPage;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.search.ui.ISearchPage;
import org.eclipse.search.ui.ISearchPageContainer;
import org.eclipse.search.ui.SearchUI;
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
import org.eclipse.ui.IWorkingSet;

import com.pnehrer.rss.core.RSSCore;

/**
 * @author <a href="mailto:pnehrer@freeshell.org">Peter Nehrer</a>
 * @see DialogPage
 */
public class RSSSearchPage extends DialogPage implements ISearchPage {

    static final String PAGE_ID = "com.pnehrer.rss.ui.page1";

    private static final int TERM_COMPLETE = 1;
    private static final int FIELD_COMPLETE = 2;
    private static final int ALL_COMPLETE =
        TERM_COMPLETE
        | FIELD_COMPLETE;

    private ISearchPageContainer container;
    private Text termText;
    private Button caseButton;
    private Button titleButton;
    private Button descriptionButton;
    
    private int complete;

    /* (non-Javadoc)
     * @see org.eclipse.search.ui.ISearchPage#setContainer(org.eclipse.search.ui.ISearchPageContainer)
     */
    public void setContainer(ISearchPageContainer container) {
        this.container = container;
    }

	/**
	 * @see DialogPage#createControl
	 */
	public void createControl(Composite parent) {
        Composite composite = new Composite(parent, SWT.NONE);
        composite.setLayoutData(
            new GridData(
                GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING));
        composite.setLayout(new GridLayout());
        
        Label label = new Label(composite, SWT.SINGLE);
        label.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING));
        label.setText("Search string:");

        Composite termGroup = new Composite(composite, SWT.NONE);
        termGroup.setLayoutData(
            new GridData(
                GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING));
        termGroup.setLayout(new GridLayout(2, false));
        
        termText = new Text(termGroup, SWT.SINGLE | SWT.BORDER);
        termText.setLayoutData(
            new GridData(
                GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING));
        termText.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                setComplete(TERM_COMPLETE, termText.getText().trim().length() > 0);
            }
        });
        
        caseButton = new Button(termGroup, SWT.CHECK);
        caseButton.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING));
        caseButton.setText("Case sensitive");
        
        Composite fieldGroup = new Composite(composite, SWT.NONE);
        fieldGroup.setLayoutData(
            new GridData(
                GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING));
        fieldGroup.setLayout(new GridLayout(3, false));

        label = new Label(fieldGroup, SWT.SINGLE);
        label.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING));
        label.setText("Search in");
        
        titleButton = new Button(fieldGroup, SWT.CHECK);
        titleButton.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING));
        titleButton.setText("Title");

        descriptionButton = new Button(fieldGroup, SWT.CHECK);
        descriptionButton.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING));
        descriptionButton.setText("Description");
        
        SelectionAdapter fieldSelectionValidator =
            new SelectionAdapter() {
                public void widgetSelected(SelectionEvent e) {
                    setComplete(
                        FIELD_COMPLETE, 
                        titleButton.getSelection() | descriptionButton.getSelection());
                }
            };

        titleButton.addSelectionListener(fieldSelectionValidator);
        descriptionButton.addSelectionListener(fieldSelectionValidator);
        
        setControl(composite);
        container.setPerformActionEnabled(false);
	}

    /* (non-Javadoc)
     * @see org.eclipse.search.ui.ISearchPage#performAction()
     */
    public boolean performAction() {
        int fieldMask = 0;
        if(titleButton.getSelection())
            fieldMask |= RSSCore.SEARCH_TITLE;
            
        if(descriptionButton.getSelection())
            fieldMask |= RSSCore.SEARCH_DESCRIPTION;
             
        Object[] workingSet;
        switch(container.getSelectedScope()) {
            case ISearchPageContainer.WORKSPACE_SCOPE:
                workingSet = new Object[] {
                    ResourcesPlugin.getWorkspace().getRoot()};
                break;
                
            case ISearchPageContainer.SELECTION_SCOPE:
                ISelection selection = container.getSelection();
                if(selection instanceof IStructuredSelection)
                    workingSet = ((IStructuredSelection)selection).toArray();
                else
                    workingSet = new Object[0];
                
                break;
                
            case ISearchPageContainer.WORKING_SET_SCOPE:
                ArrayList list = new ArrayList();
                IWorkingSet[] ws = container.getSelectedWorkingSets();
                for(int i = 0; i < ws.length; ++i)
                    list.addAll(Arrays.asList(ws[i].getElements()));
                    
                workingSet = list.toArray();
                break;

            default:
                workingSet = new Object[0];
        }

        SearchUI.activateSearchResultView();
        try {
            container.getRunnableContext().run(
                false, 
                true, 
                new SearchOperation(
                    termText.getText(), 
                    caseButton.getSelection(), 
                    fieldMask, 
                    workingSet));
                
            return true;
        }
        catch(InvocationTargetException ex) {
            MessageDialog.openError(
                getShell(), 
                "RSS Search Error",
                "Could not perform search. Exception: " + ex.getTargetException());
        }
        catch(InterruptedException ex) {
            // ignore
        }
                
        return false;
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.jface.dialogs.IDialogPage#setVisible(boolean)
     */
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        if(visible)
            termText.setFocus();
    }

    private void setComplete(int component, boolean value) {
        if(value)
            complete |= component;
        else
            complete &= ~component;
            
        container.setPerformActionEnabled(complete == ALL_COMPLETE);
    }
}
