/*
 * Created on Nov 5, 2003
 * Version $Id$
 */
package com.pnehrer.rss.ui.views;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
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
import org.eclipse.ui.IMemento;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
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

    private static final String TAG_WIDTH = "width";
    private static final String TAG_SORTER = "sorter";    
    private static final String TAG_SELECTION = "selection";
    private static final String TAG_ELEMENT = "element";
    private static final String TAG_PATH = "path";
    private static final String TAG_LINK = "link";

    private IMemento memento;
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
                if(oldSorter instanceof ItemSorter) {
                    ItemSorter itemSorter = (ItemSorter)oldSorter;
                    if(itemSorter.getColumn() == sort) {
                        ((ItemSorter)oldSorter).flipOrder();
                        viewer.refresh();
                    }
                    else {
                        viewer.setSorter(
                            new ItemSorter(sort, false, oldSorter));
                    }
                }
                else {
                    viewer.setSorter(new ItemSorter(sort, true, oldSorter));
                }
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

        getSite().setSelectionProvider(viewer);
        selectionChanged(null, getSite().getPage().getSelection());
        getSite().getPage().addSelectionListener(this);
        
        if(memento != null) {
            restoreState(memento);
            memento = null;
        }
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
        if(equals(part)) return;
        
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
        
        if(rssElement != null) {
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
     * @see org.eclipse.ui.IViewPart#init(org.eclipse.ui.IViewSite, org.eclipse.ui.IMemento)
     */
    public void init(IViewSite site, IMemento memento)
        throws PartInitException {

        super.init(site, memento);
        this.memento = memento;
    }
    
    private void restoreState(IMemento memento) {
        Table table = viewer.getTable();
        TableColumn[] columns = table.getColumns();
        for(int i = 0; i < columns.length; ++i) {
            Integer width = memento.getInteger(TAG_WIDTH + i);
            if(width != null)
                columns[i].setWidth(width.intValue());                
        }
            
        IMemento sorterMem = memento.getChild(TAG_SORTER);
        if(sorterMem != null) {
            viewer.setSorter(ItemSorter.restoreState(sorterMem));
        }

        String path = memento.getString(TAG_PATH);
        if(path == null) return;
        
        IContainer container = ResourcesPlugin.getWorkspace().getRoot();
        IResource res = container.findMember(path);
        if(res != null) {
            IRSSElement rssElement = 
                (IRSSElement)res.getAdapter(IRSSElement.class);
            if(rssElement != null) {
                IChannel channel = rssElement.getChannel();
                boolean found = false;                
                IMemento childMem = memento.getChild(TAG_SELECTION);
                if(childMem != null) {
                    HashSet links = new HashSet(); 
                    IMemento[] elementMem = childMem.getChildren(TAG_ELEMENT);
                    for(int i = 0; i < elementMem.length; ++i) {
                        links.add(elementMem[i].getString(TAG_LINK));
                    }
                    
                    ArrayList elements = new ArrayList();
                    IItem[] items = channel.getItems();
                    for(int i = 0; i < items.length; ++i) {
                        if(links.contains(items[i].getLink()))
                            elements.add(items[i]);
                    }
                    
                    if(!elements.isEmpty()) {
                        selectionChanged(null, new StructuredSelection(elements));
                        found = true;
                    }
                }

                if(!found)
                    selectionChanged(null, new StructuredSelection(channel));
            }
        }        
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.IViewPart#saveState(org.eclipse.ui.IMemento)
     */
    public void saveState(IMemento memento) {
        if(viewer == null) {
            if(this.memento != null) 
                memento.putMemento(this.memento);
        }
        else {
            Table table = viewer.getTable();
            TableColumn[] columns = table.getColumns();
            for(int i = 0; i < columns.length; ++i) {
                memento.putInteger(TAG_WIDTH + i, columns[i].getWidth());                
            }
            
            ViewerSorter sorter = viewer.getSorter();
            if(sorter instanceof ItemSorter) {
                IMemento childMem = memento.createChild(TAG_SORTER);
                ((ItemSorter)sorter).saveState(childMem, columns.length);
            }
            
            Object input = viewer.getInput();
            if(input instanceof IChannel) {
                IChannel channel = (IChannel)input;
                memento.putString(
                    TAG_PATH, 
                    channel.getFile().getFullPath().toString());

                IStructuredSelection sel = 
                    (IStructuredSelection)viewer.getSelection(); 
                if(!sel.isEmpty()) {
                    IMemento selectionMem = memento.createChild(TAG_SELECTION);
                    for(Iterator i = sel.iterator(); i.hasNext();) {
                        IItem item = (IItem)i.next();
                        IMemento elementMem = 
                            selectionMem.createChild(TAG_ELEMENT);
                        elementMem.putString(
                            TAG_LINK,
                            item.getLink());
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
