/*
 * Created on Nov 17, 2003
 * Version $Id$
 */
package com.pnehrer.rss.ui.views;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.IOpenListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.OpenEvent;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.actions.ActionContext;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.eclipse.ui.part.ISetSelectionTarget;
import org.eclipse.ui.part.IShowInSource;
import org.eclipse.ui.part.IShowInTarget;
import org.eclipse.ui.part.ShowInContext;
import org.eclipse.ui.part.ViewPart;

import com.pnehrer.rss.core.IChannel;
import com.pnehrer.rss.core.IItem;
import com.pnehrer.rss.core.IRSSElement;

/**
 * @author <a href="mailto:pnehrer@freeshell.org">Peter Nehrer</a>
 */
public class ChannelNavigator 
    extends ViewPart 
    implements ISetSelectionTarget, IShowInSource, IShowInTarget {

    private static final String TAG_SELECTION = "selection";
    private static final String TAG_EXPANDED = "expanded";
    private static final String TAG_ELEMENT = "element";
    private static final String TAG_PATH = "path";
    private static final String TAG_LINK = "link";

    private IMemento memento;
    private TreeViewer viewer;
    private ChannelNavigatorActionGroup actionGroup;

    private void initContextMenu() {
        MenuManager menuMgr = new MenuManager("#PopupMenu");
        menuMgr.setRemoveAllWhenShown(true);
        menuMgr.addMenuListener(new IMenuListener() {
            public void menuAboutToShow(IMenuManager manager) {
                ChannelNavigator.this.fillContextMenu(manager);
            }
        });

        Menu menu = menuMgr.createContextMenu(viewer.getControl());
        viewer.getControl().setMenu(menu);
        getSite().registerContextMenu(menuMgr, viewer);
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
                if(object instanceof IAdaptable) {
                    IRSSElement rssElement = (IRSSElement)
                        ((IAdaptable)object).getAdapter(IRSSElement.class);
                    if(rssElement == null) {
                        IResource resource = (IResource)
                            ((IAdaptable)object).getAdapter(IResource.class);
                        msg = resource == null ?
                            null :
                            resource.getFullPath().toString();
                    }
                    else {
                        msg = rssElement == null ?
                            null :
                            rssElement.getLink();
                    }
                }
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
        updateActionBars(sel);
    }

    private void handleOpen(OpenEvent event) {
        IStructuredSelection selection = (IStructuredSelection)event.getSelection();
        actionGroup.runDefaultAction(selection);
        for(Iterator i = selection.iterator(); i.hasNext();) {
            Object o = i.next();
            if(o instanceof IItem) {
                IItem item = (IItem)o;
                item.resetUpdateFlag();
            }
        }
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
     */
    public void createPartControl(Composite parent) {
        Tree tree = new Tree(parent, SWT.MULTI);
        tree.setLayoutData(new GridData(GridData.FILL_BOTH));
        
        viewer = new TreeViewer(tree);
        viewer.setUseHashlookup(true);
        
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
        
        viewer.setContentProvider(new ChannelNavigatorContentProvider());
        viewer.setLabelProvider(new WorkbenchLabelProvider());
        viewer.setInput(ResourcesPlugin.getWorkspace());

        initContextMenu();
        actionGroup = new ChannelNavigatorActionGroup(this);
        actionGroup.fillActionBars(getViewSite().getActionBars());
        updateActionBars((IStructuredSelection) viewer.getSelection());

        getSite().setSelectionProvider(viewer);

        if(memento != null) {
            restoreState(memento);
            memento = null;
        }
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
     */
    public void setFocus() {
        viewer.getTree().setFocus();
    }
    
    public TreeViewer getViewer() {
        return viewer;
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.part.ISetSelectionTarget#selectReveal(org.eclipse.jface.viewers.ISelection)
     */
    public void selectReveal(ISelection selection) {
        if(selection instanceof IStructuredSelection) {
            List list = new ArrayList();
            for(Iterator i = ((IStructuredSelection)selection).iterator();
                i.hasNext();) {
    
                Object o = i.next();
                IRSSElement rssElement = (IRSSElement)
                    ((IAdaptable)o).getAdapter(IRSSElement.class);
                if(rssElement != null) {
                    list.add(rssElement);
                }
                else {                    
                    IResource res = (IResource)
                        ((IAdaptable)o).getAdapter(IResource.class);
                    if(res != null && res.getType() != IResource.ROOT)
                        list.add(res);
                }
            }

            viewer.setSelection(new StructuredSelection(list), true);
        }
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.part.IShowInSource#getShowInContext()
     */
    public ShowInContext getShowInContext() {
        return new ShowInContext(viewer.getInput(), viewer.getSelection());
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.part.IShowInTarget#show(org.eclipse.ui.part.ShowInContext)
     */
    public boolean show(ShowInContext context) {
        ArrayList toSelect = new ArrayList();
        ISelection sel = context.getSelection();
        if(sel instanceof IStructuredSelection) {
            IStructuredSelection ssel = (IStructuredSelection)sel;
            for(Iterator i = ssel.iterator(); i.hasNext();) {
                Object o = i.next();
                if(o instanceof IAdaptable) {
                    IRSSElement rssElement = (IRSSElement)
                        ((IAdaptable)o).getAdapter(IRSSElement.class);
                    if(rssElement != null) {
                        toSelect.add(rssElement);
                    }
                    else {                    
                        IResource res = (IResource)
                            ((IAdaptable)o).getAdapter(IResource.class);
                        if(res != null && res.getType() != IResource.ROOT)
                            toSelect.add(res);
                    }
                }
            }
        }

        if(toSelect.isEmpty()) {
            Object input = context.getInput();
            if(input instanceof IAdaptable) {
                IRSSElement rssElement = (IRSSElement)
                    ((IAdaptable)input).getAdapter(IRSSElement.class);
                if(rssElement != null) {
                    toSelect.add(rssElement);
                }
                else {                    
                    IResource res = (IResource)
                        ((IAdaptable)input).getAdapter(IResource.class);
                    if(res != null && res.getType() != IResource.ROOT)
                        toSelect.add(res);
                }
            }
        }

        if(toSelect.isEmpty())
            return false;
        else {
            selectReveal(new StructuredSelection(toSelect));
            return true;
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
        IContainer container = ResourcesPlugin.getWorkspace().getRoot();
        IMemento childMem = memento.getChild(TAG_EXPANDED);
        if(childMem != null) {
            ArrayList elements = new ArrayList();
            IMemento[] elementMem = childMem.getChildren(TAG_ELEMENT);
            for(int i = 0; i < elementMem.length; ++i) {
                IResource res = container.findMember(
                    elementMem[i].getString(TAG_PATH));
                if(res != null) {
                    IRSSElement rssElement = 
                        (IRSSElement)res.getAdapter(IRSSElement.class);
                    if(rssElement == null)
                        elements.add(res);
                    else
                        elements.add(rssElement);
                }
            }

            viewer.setExpandedElements(elements.toArray());
        }
        
        childMem = memento.getChild(TAG_SELECTION);
        if(childMem != null) {
            ArrayList elements = new ArrayList();
            IMemento[] elementMem = childMem.getChildren(TAG_ELEMENT);
            for(int i = 0; i < elementMem.length; ++i) {
                IResource res = container.findMember(
                    elementMem[i].getString(TAG_PATH));
                if(res != null) {
                    IRSSElement rssElement = 
                        (IRSSElement)res.getAdapter(IRSSElement.class);
                    if(rssElement == null)
                        elements.add(res);
                    else {
                        String link = elementMem[i].getString(TAG_LINK);
                        if(link == null)
                            elements.add(rssElement);
                        else {
                            IItem[] items = rssElement.getChannel().getItems();
                            boolean found = false;
                            for(int j = 0; j < items.length; ++j) {
                                if(link.equals(items[j].getLink())) {
                                    elements.add(items[j]);
                                    found = true;
                                    break;
                                }
                            }
                        
                            if(!found)
                                elements.add(rssElement);
                        }
                    }
                }
            }

            viewer.setSelection(new StructuredSelection(elements));
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
            Object expandedElements[] = viewer.getVisibleExpandedElements();
            if(expandedElements.length > 0) {
                IMemento expandedMem = memento.createChild(TAG_EXPANDED);
                for(int i = 0; i < expandedElements.length; ++i) {
                    IAdaptable adaptable = (IAdaptable)expandedElements[i];
                    IMemento elementMem = expandedMem.createChild(TAG_ELEMENT);
                    IResource res = (IResource)
                        ((IAdaptable)adaptable).getAdapter(IResource.class);
                    if(res != null && res.getType() != IResource.ROOT) {
                        elementMem.putString(
                            TAG_PATH,
                            res.getFullPath().toString());
                    }
                }
            }

            IStructuredSelection sel = 
                (IStructuredSelection)viewer.getSelection(); 
            if(!sel.isEmpty()) {
                IMemento selectionMem = memento.createChild(TAG_SELECTION);
                for(Iterator i = sel.iterator(); i.hasNext();) {
                    IAdaptable adaptable = (IAdaptable)i.next();
                    IMemento elementMem = selectionMem.createChild(TAG_ELEMENT);
                    IResource res = (IResource)
                        ((IAdaptable)adaptable).getAdapter(IResource.class);
                    if(res != null && res.getType() != IResource.ROOT) {
                        elementMem.putString(
                            TAG_PATH,
                            res.getFullPath().toString());
                    }

                    IRSSElement rssElement = (IRSSElement)
                        ((IAdaptable)adaptable).getAdapter(IRSSElement.class);
                    if(rssElement instanceof IItem) {
                        elementMem.putString(
                            TAG_LINK,
                            rssElement.getLink());
                    }
                }
            }
        }
    }
}
