/*
 * Created on Dec 18, 2003
 * Version $Id$
 */
package com.pnehrer.rss.internal.ui;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IPluginDescriptor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.pnehrer.rss.ui.RSSUI;

/**
 * @author <a href="mailto:pnehrer@freeshell.org">Peter Nehrer</a>
 */
public class BrowserEditorPreferencePage
    extends PreferencePage
    implements IWorkbenchPreferencePage {

    private IWorkbench workbench;
    private TableViewer editorTable;
    private final Map editorMap = new HashMap();
    
    public BrowserEditorPreferencePage() {
        IConfigurationElement[] configElements = BrowserEditor.getEditors();
        for(int i = 0, n = configElements.length; i < n; ++i)
            editorMap.put(
                configElements[i].getAttribute("id"), 
                configElements[i]);
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
        label.setText("Select preferred Browser Editor:");
        
        Table table = new Table(
            topLevel, 
            SWT.BORDER | SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL);

        GridData gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.heightHint = 200;
        table.setLayoutData(gd);
        
        editorTable = new TableViewer(table);
        editorTable.setUseHashlookup(true);
        
        editorTable.setContentProvider(new IStructuredContentProvider() {
            public Object[] getElements(Object inputElement) {
                return (IConfigurationElement[])inputElement;
            }

            public void inputChanged(
                Viewer viewer, 
                Object oldInput, 
                Object newInput) {
            }

            public void dispose() {                
            }
        });
        
        editorTable.setLabelProvider(new LabelProvider() {
            private final Collection images = new ArrayList();
            
            /* (non-Javadoc)
             * @see org.eclipse.jface.viewers.LabelProvider#getImage(java.lang.Object)
             */
            public Image getImage(Object element) {
                IConfigurationElement configElement =
                    (IConfigurationElement)element;
                String icon = configElement.getAttribute("icon");
                if(icon == null)
                    return null;
                else {
                    IPluginDescriptor pd = 
                        configElement
                            .getDeclaringExtension()
                            .getDeclaringPluginDescriptor();
                    URL iconURL = pd.find(new Path(icon));
                    if(iconURL == null)
                        return null;
                        
                    ImageDescriptor id = ImageDescriptor.createFromURL(iconURL);
                    Image image = id.createImage();
                    images.add(image);
                    return image;
                }
            }
            
            /* (non-Javadoc)
             * @see org.eclipse.jface.viewers.LabelProvider#getText(java.lang.Object)
             */
            public String getText(Object element) {
                return ((IConfigurationElement)element).getAttribute("name");
            }

            /* (non-Javadoc)
             * @see org.eclipse.jface.viewers.LabelProvider#dispose()
             */
            public void dispose() {
                for(Iterator i = images.iterator(); i.hasNext();)
                    ((Image)i.next()).dispose();
                    
                super.dispose();
            }
        });
        
        editorTable.setSorter(new ViewerSorter() {
            /* (non-Javadoc)
             * @see org.eclipse.jface.viewers.ViewerSorter#compare(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
             */
            public int compare(Viewer viewer, Object e1, Object e2) {
                String name1 = ((IConfigurationElement)e1).getAttribute("name"); 
                String name2 = ((IConfigurationElement)e2).getAttribute("name");
                return name1.compareTo(name2);
            }
        });
        
        editorTable.setInput(editorMap.values().toArray(
            new IConfigurationElement[editorMap.size()]));
        
        editorTable.addSelectionChangedListener(new ISelectionChangedListener() {
            public void selectionChanged(SelectionChangedEvent event) {
                setValid(!editorTable.getSelection().isEmpty());
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
            BrowserEditor.PREF_BROWSER_EDITOR);
        setSelection();
        super.performDefaults();
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.preference.IPreferencePage#performOk()
     */
    public boolean performOk() {
        IStructuredSelection sel = (IStructuredSelection)
            editorTable.getSelection();
        RSSUI.getDefault().getPluginPreferences().setValue(
            BrowserEditor.PREF_BROWSER_EDITOR,
            ((IConfigurationElement)sel.getFirstElement()).getAttribute("id"));

        return super.performOk();
    }
    
    private void setSelection() {
        String id = RSSUI.getDefault().getPluginPreferences().getString(
            BrowserEditor.PREF_BROWSER_EDITOR);
        StructuredSelection sel = new StructuredSelection(
            editorMap.get(id));
        editorTable.setSelection(sel, true);
        setValid(!sel.isEmpty());
    }
}
