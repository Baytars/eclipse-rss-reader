/*
 * Created on Nov 5, 2003
 * Version $Id$
 */
package com.pnehrer.rss.ui.views;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IOpenListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.OpenEvent;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.actions.ActionContext;
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

    private static final String[] COLUMNS = {
        "#", 
        "Title", 
        "Description", 
        "Link", 
        "Date"};

    private TableViewer viewer;
    private ChannelActionGroup actionGroup;

    private void initContextMenu() {
        MenuManager menuMgr = new MenuManager("#PopupMenu");
        menuMgr.setRemoveAllWhenShown(true);
        menuMgr.addMenuListener(new IMenuListener() {
            public void menuAboutToShow(IMenuManager manager) {
                ChannelDetailView.this.fillContextMenu(manager);
            }
        });

        Menu menu = menuMgr.createContextMenu(viewer.getControl());
        viewer.getControl().setMenu(menu);
    }

    private void fillContextMenu(IMenuManager menu) {
        IStructuredSelection selection = 
            (IStructuredSelection) viewer.getSelection();
        actionGroup.setContext(new ActionContext(selection));
        actionGroup.fillContextMenu(menu);
    }

    private void updateActionBars(IStructuredSelection selection) {
        actionGroup.setContext(new ActionContext(selection));
        actionGroup.updateActionBars();
    }

    private void updateStatusLine(IStructuredSelection selection) {
        String msg;
        switch(selection.size()) {
            case 0:
                msg = "No selection.";
                break;
                
            case 1:
                Object object = selection.getFirstElement();
                if(object instanceof IItem)
                    msg = ((IItem)object).getLink();
                else
                    msg = null;
                                    
                break;
                
            default:
                msg = "Multiple selection.";
        }

        getViewSite().getActionBars().getStatusLineManager().setMessage(msg);
    }

    private void handleSelectionChanged(SelectionChangedEvent event) {
        IStructuredSelection sel = (IStructuredSelection)event.getSelection();
        updateStatusLine(sel);
    }

    private void handleOpen(OpenEvent event) {
        IStructuredSelection selection = (IStructuredSelection)event.getSelection();
        actionGroup.runDefaultAction(selection);
    }

    private TableColumn createColumn(
        Table table, 
        int index, 
        int width, 
        final int sort) {

        TableColumn column = new TableColumn(table, SWT.LEFT);
        column.setText(COLUMNS[index]);
        column.setWidth(width);
        column.setResizable(true);
        column.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                ViewerSorter oldSorter = viewer.getSorter();
                viewer.setSorter(
                    new ItemSorter(
                        sort,
                        oldSorter instanceof ItemSorter ?
                            !((ItemSorter)oldSorter).isReverse() :
                            false,
                            oldSorter));
            }
        });
        
        return column;
    }

	/**
	 * @see ViewPart#createPartControl
	 */
	public void createPartControl(Composite parent) {
        Table table = new Table(parent, SWT.MULTI | SWT.FULL_SELECTION);
        table.setLayoutData(new GridData(GridData.FILL_BOTH));
        table.setHeaderVisible(true);
        table.setLinesVisible(true);

        createColumn(table, 0, 20, ItemSorter.NONE);
        createColumn(table, 1, 200, ItemSorter.TITLE);
        createColumn(table, 2, 300, ItemSorter.DESCRIPTION);
//        createColumn(table, 3, 300, ItemSorter.LINK);
//        createColumn(table, 4, 100, ItemSorter.DATE);

        viewer = new TableViewer(table);
        viewer.setUseHashlookup(true);
        
        viewer.setContentProvider(new ChannelDetailContentProvider());
        viewer.setLabelProvider(new ChannelDetailLabelProvider());
        
        viewer.addOpenListener(new IOpenListener() {
            public void open(OpenEvent event) {
                handleOpen(event);
            }
        });
        
        viewer.addSelectionChangedListener(new ISelectionChangedListener() {
            public void selectionChanged(SelectionChangedEvent event) {
                handleSelectionChanged(event);
            }
        });
        
        initContextMenu();
        actionGroup = new ChannelActionGroup(this);
        actionGroup.fillActionBars(getViewSite().getActionBars());

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
            setTitleImage(
                RSSUI.getDefault().getImageRegistry().get(RSSUI.DETAIL_ICON));
            setTitleToolTip("Please select a channel.");

            IStructuredSelection sel = new StructuredSelection();
            updateStatusLine(sel);
            updateActionBars(sel);
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
            updateStatusLine((IStructuredSelection)selection);
            updateActionBars((IStructuredSelection)selection);
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
