/*
 * Created on Nov 17, 2003
 * Version $Id$
 */
package com.pnehrer.rss.ui.views;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
import org.eclipse.ui.actions.ActionContext;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.eclipse.ui.part.ISetSelectionTarget;
import org.eclipse.ui.part.IShowInSource;
import org.eclipse.ui.part.IShowInTarget;
import org.eclipse.ui.part.ShowInContext;
import org.eclipse.ui.part.ViewPart;

import com.pnehrer.rss.core.IRSSElement;

/**
 * @author <a href="mailto:pnehrer@freeshell.org">Peter Nehrer</a>
 */
public class ChannelNavigator 
    extends ViewPart 
    implements ISetSelectionTarget, IShowInSource, IShowInTarget {

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
    
                IRSSElement rssElement = (IRSSElement)
                    ((IAdaptable)i.next()).getAdapter(IRSSElement.class);
                if(rssElement != null)
                    list.add(rssElement);
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
                if(o instanceof IResource) {
                    toSelect.add(o);
                }
                else if(o instanceof IMarker) {
                    IResource r = ((IMarker)o).getResource();
                    if (r.getType() != IResource.ROOT)
                        toSelect.add(r);
                }
                else if (o instanceof IAdaptable) {
                    IAdaptable adaptable = (IAdaptable)o;
                    o = adaptable.getAdapter(IResource.class);
                    if(o instanceof IResource) {
                        toSelect.add(o);
                    }
                    else {
                        o = adaptable.getAdapter(IMarker.class);
                        if(o instanceof IMarker) {
                            IResource r = ((IMarker)o).getResource();
                            if(r.getType() != IResource.ROOT)
                                toSelect.add(r);
                        }
                    }
                }
            }
        }

        if(toSelect.isEmpty()) {
            Object input = context.getInput();
            if(input instanceof IAdaptable) {
                IAdaptable adaptable = (IAdaptable) input;
                Object o = adaptable.getAdapter(IResource.class);
                if(o instanceof IResource) {
                    toSelect.add(o);
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
}
