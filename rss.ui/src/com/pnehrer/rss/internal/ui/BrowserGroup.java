/*
 * Created on Dec 16, 2003
 * Version $Id$
 */
package com.pnehrer.rss.internal.ui;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;

import com.pnehrer.rss.ui.BrowserFactoryDescriptor;
import com.pnehrer.rss.ui.IPageContainer;
import com.pnehrer.rss.ui.RSSUI;

/**
 * @author <a href="mailto:pnehrer@freeshell.org">Peter Nehrer</a>
 */
public class BrowserGroup {

    private final IPageContainer pageContainer;
    private BrowserFactoryDescriptor selectedBrowserFactory;
    private BrowserFactoryDescriptor[] browserFactories;
    private List browserFactoryList;
    
    public BrowserGroup(IPageContainer pageContainer) {
        this.pageContainer = pageContainer;
        try {
            browserFactories = 
                RSSUI.getDefault().getBrowserFactoryDescriptors();
        }
        catch(CoreException e) {
            browserFactories = new BrowserFactoryDescriptor[0];
        }
    }
    
    public BrowserFactoryDescriptor getSelectedBrowserFactory() {
        return selectedBrowserFactory;
    }
    
    public void setSelectedBrowserFactory(
        BrowserFactoryDescriptor selectedBrowserFactory) {
            
        this.selectedBrowserFactory = selectedBrowserFactory;
        if(browserFactoryList != null) {
            browserFactoryList.deselectAll();
            for(int i = 0, n = browserFactories.length; i < n; ++i) {
                if(browserFactories[i].equals(selectedBrowserFactory)) { 
                    browserFactoryList.select(i);
                    break;
                }
            }
            
            pageContainer.setComplete(
                browserFactoryList.getSelectionCount() == 1);
        }
    }

    public void createContents(Composite topLevel) {
        int columns = topLevel.getLayout() instanceof GridLayout ?
            ((GridLayout)topLevel.getLayout()).numColumns :
            1;
        
        Label label = new Label(topLevel, SWT.SINGLE);
        GridData layoutData = new GridData();
        layoutData.horizontalSpan = columns;
        label.setLayoutData(layoutData);
        label.setText("Select preferred browser:");

        browserFactoryList = new List(
            topLevel, 
            SWT.BORDER | SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL);

        layoutData = new GridData(GridData.FILL_HORIZONTAL);
        layoutData.horizontalSpan = columns;
        browserFactoryList.setLayoutData(layoutData);
        for(int i = 0, n = browserFactories.length; i < n; ++i)
            browserFactoryList.add(browserFactories[i].getName());
        
        browserFactoryList.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                int i = browserFactoryList.getSelectionIndex();
                if(i >= 0) {
                    selectedBrowserFactory = browserFactories[i];
                    pageContainer.setComplete(true);
                }
                else {
                    pageContainer.setComplete(false);
                }
            }
        });

        setSelectedBrowserFactory(selectedBrowserFactory);
    }
}
