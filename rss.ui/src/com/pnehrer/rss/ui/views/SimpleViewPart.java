/*
 * Created on Nov 5, 2003
 * Version $Id$
 */
package com.pnehrer.rss.ui.views;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
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
import com.pnehrer.rss.core.RSSCore;

/**
 * @author <a href="mailto:pnehrer@freeshell.org">Peter Nehrer</a>
 * @see ViewPart
 */
public class SimpleViewPart extends ViewPart implements ISelectionListener {

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
                    viewer.setSorter(new ItemSorter(ItemSorter.TITLE));
                }
            });

        column = new TableColumn(table, SWT.LEFT);
        column.setText(COLUMNS[1]);
        column.setWidth(300);
        column.setResizable(true);
        column.addSelectionListener(new SelectionAdapter() {
                public void widgetSelected(SelectionEvent e) {
                    viewer.setSorter(new ItemSorter(ItemSorter.DESCRIPTION));
                }
            });
        
        viewer = new TableViewer(table);
        viewer.setUseHashlookup(true);
        
        viewer.setContentProvider(new ChannelContentProvider());
        viewer.setLabelProvider(new ChannelTableLabelProvider());
        
        getSite().getPage().addSelectionListener(this);
        selectionChanged(this, getSite().getPage().getSelection());
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
        if(selection instanceof IStructuredSelection) {
            Object obj = ((IStructuredSelection)selection).getFirstElement();
            if(obj instanceof IFile) {
                IFile file = (IFile)obj;
                if("rss".equals(file.getFileExtension())) {
                    IChannel channel;
                    try {
                        channel = RSSCore.getPlugin().create(file);
                    }
                    catch(CoreException ex) {
                        channel = null;
                    }
                    
                    if(channel != null) {
                        viewer.setInput(channel);
                        setTitle(channel.getTitle());
                    }
                }
            }
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
