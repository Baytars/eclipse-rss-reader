/*
 * Created on Nov 5, 2003
 * Version $Id$
 */
package com.pnehrer.rss.ui.views;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.help.browser.IBrowser;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IOpenListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.OpenEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.ViewPart;

import com.pnehrer.rss.core.IChannel;
import com.pnehrer.rss.core.IItem;
import com.pnehrer.rss.core.IRSSElement;
import com.pnehrer.rss.ui.RSSUI;

/**
 * @author <a href="mailto:pnehrer@freeshell.org">Peter Nehrer</a>
 * @see ViewPart
 */
public class ChannelDetailView extends ViewPart implements ISelectionListener {

    private static final String[] COLUMNS = {"Title", "Description"};
    private TableViewer viewer;

	/**
	 * @see ViewPart#createPartControl
	 */
	public void createPartControl(Composite parent) {
        Table table = new Table(parent, SWT.MULTI | SWT.FULL_SELECTION);
        table.setLayoutData(new GridData(GridData.FILL_BOTH));
        table.setHeaderVisible(true);
        table.setLinesVisible(true);
        
        TableColumn column = new TableColumn(table, SWT.LEFT);
        column.setText(COLUMNS[0]);
        column.setWidth(200);
        column.setResizable(true);
        column.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                ViewerSorter oldSorter = viewer.getSorter();
                viewer.setSorter(
                    new ItemSorter(
                        ItemSorter.TITLE,
                        oldSorter instanceof ItemSorter ?
                            !((ItemSorter)oldSorter).isReverse() :
                            false,
                            oldSorter));
            }
        });

        column = new TableColumn(table, SWT.LEFT);
        column.setText(COLUMNS[1]);
        column.setWidth(300);
        column.setResizable(true);
        column.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                ViewerSorter oldSorter = viewer.getSorter();
                viewer.setSorter(
                    new ItemSorter(
                        ItemSorter.DESCRIPTION,
                        oldSorter instanceof ItemSorter ?
                            !((ItemSorter)oldSorter).isReverse() :
                            false,
                            oldSorter));
            }
        });
        
        viewer = new TableViewer(table);
        viewer.setUseHashlookup(true);
        
        viewer.setContentProvider(new ChannelDetailContentProvider());
        viewer.setLabelProvider(new ChannelDetailLabelProvider());
        
        viewer.addOpenListener(new IOpenListener() {
            public void open(OpenEvent event) {
                IStructuredSelection selection = 
                    (IStructuredSelection)event.getSelection();
                if(!selection.isEmpty()) {
                    IItem item = (IItem)selection.getFirstElement();
                    try {
                        IBrowser browser = 
                            RSSUI.getDefault().createBrowser();
                        browser.displayURL(item.getLink());
                    }
                    catch(CoreException ex) {
                        ErrorDialog.openError(
                            getViewSite().getShell(),
                            "Browser Error",
                            "Could not open browser.",
                            ex.getStatus());
                    }
                    catch(Exception ex) {
                        MessageDialog.openError(
                            getViewSite().getShell(),
                            "Browser Error",
                            "Could not open link " + item.getLink() 
                                + ". Exception: " + ex);
                    }
                }                    
            }
        });
        
        selectionChanged(this, getSite().getPage().getSelection());
        getSite().getPage().addSelectionListener(
            "org.eclipse.ui.views.ResourceNavigator", 
            this);

        getSite().getPage().addSelectionListener(
            "com.pnehrer.rss.ui.views.ChannelNavigator", 
            this);

        getSite().setSelectionProvider(viewer);
	}

	/**
	 * @see ViewPart#setFocus
	 */
	public void setFocus() {
        viewer.getTable().setFocus();
	}

    /* (non-Javadoc)
     * @see org.eclipse.ui.ISelectionListener#selectionChanged(org.eclipse.ui.IWorkbenchPart, org.eclipse.jface.viewers.ISelection)
     */
    public void selectionChanged(IWorkbenchPart part, ISelection selection) {
        IRSSElement rssElement;
        if(!(selection instanceof IStructuredSelection) || selection.isEmpty())
            rssElement = null;
        else {
            Object obj = ((IStructuredSelection)selection).getFirstElement();
            if(obj instanceof IAdaptable) {
                rssElement = (IRSSElement)
                    ((IAdaptable)obj).getAdapter(IRSSElement.class);
            }
            else 
                rssElement = null;
        }
        
        if(rssElement == null) {
            viewer.setInput(null);
            setTitle("No channel selected");
            setTitleImage(null);
            setTitleToolTip("Please select a channel.");
        }
        else {
            IChannel channel = rssElement.getChannel();
            viewer.setInput(channel);
            viewer.setSelection(selection, true);
            
            setTitle(channel.getTitle());
            ImageDescriptor imageDescriptor = 
                RSSUI.getDefault().getImageDescriptor16(channel);
            setTitleImage(imageDescriptor == null ? 
                null : 
                imageDescriptor.createImage());
                    
            setTitleToolTip(channel.getLink());
        }
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.ui.IWorkbenchPart#dispose()
     */
    public void dispose() {
        getSite().getPage().removeSelectionListener(this);
        super.dispose();
    }
}
