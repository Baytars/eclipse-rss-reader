/*
 * Created on Dec 16, 2003
 * Version $Id$
 */
package com.pnehrer.rss.internal.ui;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.IEditorDescriptor;

import com.pnehrer.rss.ui.BrowserFactoryDescriptor;
import com.pnehrer.rss.ui.IPageContainer;
import com.pnehrer.rss.ui.RSSUI;

/**
 * @author <a href="mailto:pnehrer@freeshell.org">Peter Nehrer</a>
 */
public class BrowserGroup {

    private final IPageContainer pageContainer;
    private String choice; 
    private Button useBrowserButton;
    private Button useEditorButton;
    private BrowserFactoryDescriptor[] browserFactories;
    private List browserFactoryList;
    private TableViewer editorList;
    
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
    
    public String getChoice() {
        return useBrowserButton.getSelection() ?
            RSSUI.PREF_BROWSER :
            RSSUI.PREF_EDITOR;
    }
    
    public void setChoice(String choice) {
        this.choice = choice;
        if(useBrowserButton != null) {
            if(RSSUI.PREF_BROWSER.equals(choice)) {
                useBrowserButton.setSelection(true);
                browserFactoryList.setEnabled(true);
                useEditorButton.setSelection(false);
                editorList.getControl().setEnabled(false);
                pageContainer.setComplete(
                    browserFactoryList.getSelectionCount() == 1);
            }
            else {
                useBrowserButton.setSelection(false);
                browserFactoryList.setEnabled(false);
                useEditorButton.setSelection(true);
                editorList.getControl().setEnabled(true);
                pageContainer.setComplete(
                    !editorList.getSelection().isEmpty());
            }
        }
    }
    
    public BrowserFactoryDescriptor getSelectedBrowserFactory() {
        if(useBrowserButton.getSelection()) {
            int i = browserFactoryList.getSelectionIndex();
            return i >= 0 ?
                browserFactories[i] :
                null;
        }
        else
            return null;
    }
    
    public void setSelectedBrowserFactory(
        BrowserFactoryDescriptor selectedBrowserFactory) {
            
        if(browserFactoryList != null) {
            browserFactoryList.deselectAll();
            for(int i = 0, n = browserFactories.length; i < n; ++i) {
                if(browserFactories[i].equals(selectedBrowserFactory)) { 
                    browserFactoryList.select(i);
                    break;
                }
            }
            
            if(useBrowserButton.getSelection())
                pageContainer.setComplete(
                    browserFactoryList.getSelectionCount() == 1);
        }
    }
    
    public String getSelectedEditorId() {
        if(useEditorButton.getSelection()) {
            ISelection sel = editorList.getSelection();
            if(sel instanceof IStructuredSelection) {
                Object element = ((IStructuredSelection)sel).getFirstElement();
                return element == null ?
                    null :
                    ((IEditorDescriptor)element).getId();
            }
            else 
                return null;
        }
        else
            return null;
    }
    
    public void setSelectedEditorId(String selectedEditorId) {
        if(editorList != null) {
            StructuredSelection sel;
            if(selectedEditorId == null)
                sel = StructuredSelection.EMPTY;
            else {
                IEditorDescriptor ed =  
                    RSSUI
                        .getDefault()
                        .getWorkbench()
                        .getEditorRegistry()
                        .findEditor(selectedEditorId);
                sel = ed == null ? 
                    StructuredSelection.EMPTY : 
                    new StructuredSelection(ed);
            }
                    
            editorList.setSelection(sel);
            if(useEditorButton.getSelection())
                pageContainer.setComplete(!sel.isEmpty());
        }
    }

    public void createContents(Composite topLevel) {
        int columns = topLevel.getLayout() instanceof GridLayout ?
            ((GridLayout)topLevel.getLayout()).numColumns :
            1;
        
        Composite group = new Composite(topLevel, SWT.NULL);
        GridData layoutData = new GridData(GridData.FILL_HORIZONTAL);
        layoutData.horizontalSpan = columns;
        group.setLayoutData(layoutData);
        group.setLayout(new GridLayout());

        Label label = new Label(group, SWT.SINGLE);
        label.setText("To open links:");

        useBrowserButton = new Button(group, SWT.RADIO);
        useBrowserButton.setText("Use help &browser");
        useBrowserButton.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                if(useBrowserButton.getSelection()) {
                    browserFactoryList.setEnabled(true);
                    editorList.getControl().setEnabled(false);
                    pageContainer.setComplete(
                        browserFactoryList.getSelectionCount() == 1);
                }
            }
        });

        browserFactoryList = new List(
            group, 
            SWT.BORDER | SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL);

        layoutData = new GridData(GridData.FILL_HORIZONTAL);
        layoutData.horizontalIndent = 20;
        browserFactoryList.setLayoutData(layoutData);
        for(int i = 0, n = browserFactories.length; i < n; ++i)
            browserFactoryList.add(browserFactories[i].getName());
        
        browserFactoryList.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                pageContainer.setComplete(
                    browserFactoryList.getSelectionCount() == 1);
            }
        });

        useEditorButton = new Button(group, SWT.RADIO);
        useEditorButton.setText("Use HTML &editor");
        useEditorButton.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                if(useEditorButton.getSelection()) {
                    browserFactoryList.setEnabled(false);
                    editorList.getControl().setEnabled(true);
                    pageContainer.setComplete(
                        !editorList.getSelection().isEmpty());
                }
            }
        });
        
        Table table = new Table(
            group, 
            SWT.BORDER | SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL);

        layoutData = new GridData(GridData.FILL_HORIZONTAL);
        layoutData.horizontalIndent = 20;
        table.setLayoutData(layoutData);

        editorList = new TableViewer(table);
        editorList.setUseHashlookup(true);
        editorList.setContentProvider(new IStructuredContentProvider() {
            public Object[] getElements(Object inputElement) {
                return (IEditorDescriptor[])inputElement;
            }

            public void dispose() {
            }

            public void inputChanged(
                Viewer viewer,
                Object oldInput,
                Object newInput) {
            }
        });
        
        editorList.setLabelProvider(new LabelProvider() {
            private final Map imageMap = new HashMap();
            
            public Image getImage(Object element) {
                IEditorDescriptor ed = (IEditorDescriptor)element;
                Image image = (Image)imageMap.get(ed.getId());
                if(image == null) {
                    image = ed.getImageDescriptor().createImage();
                    imageMap.put(ed.getId(), image);
                }
                
                return image;
            }

            public String getText(Object element) {
                return ((IEditorDescriptor)element).getLabel();
            }

            public void dispose() {
                for(Iterator i = imageMap.values().iterator(); i.hasNext();)
                    ((Image)i.next()).dispose();
                    
                super.dispose();
            }
        });
        
        editorList.setInput( 
            RSSUI
                .getDefault()
                .getWorkbench()
                .getEditorRegistry()
                .getEditors("x.html"));
                
        editorList.addSelectionChangedListener(new ISelectionChangedListener() {
            public void selectionChanged(SelectionChangedEvent event) {
                pageContainer.setComplete(
                    !editorList.getSelection().isEmpty());
            }
        });
        
        setChoice(choice);
    }
    
    public void setFocus() {
        browserFactoryList.setFocus();
    }
}
