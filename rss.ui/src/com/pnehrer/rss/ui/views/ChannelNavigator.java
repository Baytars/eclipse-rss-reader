/*
 * Created on Nov 17, 2003
 * Version $Id$
 */
package com.pnehrer.rss.ui.views;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.help.browser.IBrowser;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IOpenListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.OpenEvent;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.actions.ActionContext;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.eclipse.ui.part.ISetSelectionTarget;
import org.eclipse.ui.part.ViewPart;

import com.pnehrer.rss.core.IChannel;
import com.pnehrer.rss.core.IItem;
import com.pnehrer.rss.core.IRSSElement;
import com.pnehrer.rss.ui.RSSUI;

/**
 * @author <a href="mailto:pnehrer@freeshell.org">Peter Nehrer</a>
 */
public class ChannelNavigator extends ViewPart implements ISetSelectionTarget {

    private TreeViewer viewer;
    private ChannelActionGroup actionGroup;

    private void initContextMenu() {
        MenuManager menuMgr = new MenuManager("#PopupMenu");
        menuMgr.setRemoveAllWhenShown(true);
        menuMgr.addMenuListener(new IMenuListener() {
            public void menuAboutToShow(IMenuManager manager) {
                ChannelNavigator.this.fillContextMenu(manager);
            }
        });

        Menu menu = menuMgr.createContextMenu(viewer.getTree());
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
                    msg = rssElement == null ?
                        String.valueOf(object) :
                        rssElement.getLink();
                }
                else
                    msg = String.valueOf(object);
                
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
                IStructuredSelection selection = 
                    (IStructuredSelection)event.getSelection();
                if(!selection.isEmpty()) {
                    Object element = selection.getFirstElement();
                    String url;
                    if(element instanceof IChannel)
                        url = ((IChannel)element).getLink();
                    else if(element instanceof IItem)
                        url = ((IItem)element).getLink();
                    else
                        url = null;

                    if(url != null) {                            
                        try {
                            IBrowser browser = 
                                RSSUI.getDefault().createBrowser();
                            browser.displayURL(url);
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
                                "Could not open link " + url 
                                    + ". Exception: " + ex);
                        }
                    }
                }                    
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
        actionGroup = new ChannelActionGroup();

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

    /* (non-Javadoc)
     * @see org.eclipse.ui.part.ISetSelectionTarget#selectReveal(org.eclipse.jface.viewers.ISelection)
     */
    public void selectReveal(ISelection selection) {
        viewer.setSelection(selection, true);
    }
}
