/*
 * Created on Dec 16, 2003
 * Version $Id$
 */
package com.pnehrer.rss.internal.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;

import com.pnehrer.rss.ui.ILinkBrowserDescriptor;
import com.pnehrer.rss.ui.RSSUI;

/**
 * @author <a href="mailto:pnehrer@freeshell.org">Peter Nehrer</a>
 */
public class BrowserGroup {

    private final IPageContainer pageContainer;
    private final ILinkBrowserDescriptor[] browserDescriptors;
    private List browserList;
    
    public BrowserGroup(IPageContainer pageContainer) {
        this.pageContainer = pageContainer;
        browserDescriptors = RSSUI.getDefault().getLinkBrowserDescriptors();
    }
    
    public String getSelectedBrowser() {
        int i = browserList.getSelectionIndex();
        return i >= 0 ?
            browserDescriptors[i].getId() :
            null;
    }
    
    public void setSelectedBrowser(String id) {
        if(browserList != null) {
            browserList.deselectAll();
            for(int i = 0, n = browserDescriptors.length; i < n; ++i) {
                if(browserDescriptors[i].getId().equals(id)) { 
                    browserList.select(i);
                    break;
                }
            }

            pageContainer.setComplete(browserList.getSelectionCount() == 1);
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
        label.setText("Select link browser:");

        browserList = new List(
            topLevel, 
            SWT.BORDER | SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL);

        layoutData = new GridData(GridData.FILL_HORIZONTAL);
        layoutData.horizontalSpan = columns;
        browserList.setLayoutData(layoutData);
        for(int i = 0, n = browserDescriptors.length; i < n; ++i)
            browserList.add(browserDescriptors[i].getLabel());
        
        browserList.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                pageContainer.setComplete(
                    browserList.getSelectionCount() == 1);
            }
        });
    }
    
    public void setFocus() {
        browserList.setFocus();
    }
}
