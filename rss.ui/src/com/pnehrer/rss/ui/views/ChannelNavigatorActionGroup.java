/*
 * Created on Dec 5, 2003
 * Version $Id$
 */
package com.pnehrer.rss.ui.views;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionGroup;
import org.eclipse.ui.actions.AddBookmarkAction;
import org.eclipse.ui.actions.AddTaskAction;
import org.eclipse.ui.actions.CloseResourceAction;
import org.eclipse.ui.actions.NewWizardMenu;
import org.eclipse.ui.actions.OpenInNewWindowAction;
import org.eclipse.ui.actions.OpenResourceAction;
import org.eclipse.ui.actions.OpenWithMenu;
import org.eclipse.ui.actions.RefreshAction;
import org.eclipse.ui.dialogs.PropertyDialogAction;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.ui.views.navigator.OpenActionGroup;
import org.eclipse.ui.views.navigator.ResourceNavigatorMessages;

import com.pnehrer.rss.core.IRSSElement;
import com.pnehrer.rss.ui.RSSUI;
import com.pnehrer.rss.ui.actions.MarkReadAction;
import com.pnehrer.rss.ui.actions.OpenLinkAction;
import com.pnehrer.rss.ui.actions.TextInputAction;
import com.pnehrer.rss.ui.actions.ToggleShowNewOnlyAction;
import com.pnehrer.rss.ui.actions.UpdateAction;

/**
 * @author <a href="mailto:pnehrer@freeshell.org">Peter Nehrer</a>
 */
public class ChannelNavigatorActionGroup extends ActionGroup {

    private final ToggleShowNewOnlyAction toggleShowNewOnlyAction;

    private final AddBookmarkAction addBookmarkAction;
    private final AddTaskAction addTaskAction;  
    private final PropertyDialogAction propertyDialogAction;
    private final Action collapseAllAction;
    
    private final OpenResourceAction openProjectAction;
    private final CloseResourceAction closeProjectAction;
    private final RefreshAction refreshAction;
    
    private final OpenLinkAction openLinkAction;
    private final TextInputAction textInputAction;
    private final UpdateAction updateAction;
    private final MarkReadAction markReadAction;
    
    private final ChannelNavigator navigator;

    public ChannelNavigatorActionGroup(ChannelNavigator navigator) {
        this.navigator = navigator;

        Shell shell = navigator.getSite().getShell();
        
        addBookmarkAction = new AddBookmarkAction(shell);
        addTaskAction = new AddTaskAction(shell);       
        propertyDialogAction =
            new PropertyDialogAction(shell, navigator.getViewer());
        
        collapseAllAction = new Action(ResourceNavigatorMessages.getString("CollapseAllAction.title")) {
            public void run() {
                ChannelNavigatorActionGroup.this.navigator.getViewer().collapseAll();
            }
        };
        
        collapseAllAction.setToolTipText(ResourceNavigatorMessages.getString("CollapseAllAction.toolTip")); //$NON-NLS-1$
        collapseAllAction.setImageDescriptor(getImageDescriptor("elcl16/collapseall.gif")); //$NON-NLS-1$
        collapseAllAction.setHoverImageDescriptor(getImageDescriptor("clcl16/collapseall.gif")); //$NON-NLS-1$

        IWorkspace workspace = ResourcesPlugin.getWorkspace();
        openProjectAction = new OpenResourceAction(shell);
        workspace.addResourceChangeListener(openProjectAction, IResourceChangeEvent.POST_CHANGE);
        closeProjectAction = new CloseResourceAction(shell);
        workspace.addResourceChangeListener(closeProjectAction, IResourceChangeEvent.POST_CHANGE);
        refreshAction = new RefreshAction(shell);
        refreshAction.setDisabledImageDescriptor(getImageDescriptor("dlcl16/refresh_nav.gif"));//$NON-NLS-1$
        refreshAction.setImageDescriptor(getImageDescriptor("elcl16/refresh_nav.gif"));//$NON-NLS-1$
        refreshAction.setHoverImageDescriptor(getImageDescriptor("clcl16/refresh_nav.gif"));//$NON-NLS-1$       
        
        ImageRegistry reg = RSSUI.getDefault().getImageRegistry();
        
        openLinkAction = new OpenLinkAction(navigator.getSite().getShell());
        openLinkAction.setToolTipText("Open selected element's link in browser.");
        openLinkAction.setImageDescriptor(reg.getDescriptor(RSSUI.BROWSE_ICON));

        textInputAction = new TextInputAction(navigator.getSite().getShell());
        textInputAction.setToolTipText("Submit text input to channel site.");
        textInputAction.setImageDescriptor(reg.getDescriptor(RSSUI.TEXT_INPUT_ICON));

        updateAction = new UpdateAction();
        updateAction.setToolTipText("Update selected channel(s) from their sources.");
        updateAction.setImageDescriptor(reg.getDescriptor(RSSUI.UPDATE_ICON));

        markReadAction = new MarkReadAction(navigator.getViewSite().getWorkbenchWindow());
        markReadAction.setToolTipText("Mark selection as read.");
        markReadAction.setImageDescriptor(reg.getDescriptor(RSSUI.ITEM_ICON));

        toggleShowNewOnlyAction = new ToggleShowNewOnlyAction(
            new ToggleShowNewOnlyAction.INewItemFilteringPart() {
                public void setShowNewOnly(boolean value) {
                    ChannelNavigatorActionGroup.this.navigator.setShowNewOnly(value);
                }
            });
            
        toggleShowNewOnlyAction.setToolTipText("Toggle showing only new items.");
        toggleShowNewOnlyAction.setImageDescriptor(reg.getDescriptor(RSSUI.ITEM_NEW_ICON));
    }

    public void fillContextMenu(IMenuManager menu) {
        IStructuredSelection selection =
            (IStructuredSelection) getContext().getSelection();
        
        MenuManager newMenu =
            new MenuManager(ResourceNavigatorMessages.getString("ResourceNavigator.new"));
        menu.add(newMenu);
        new NewWizardMenu(newMenu, navigator.getSite().getWorkbenchWindow(), false);
        
        boolean onlyFilesSelected;
        if(selection.isEmpty())
            onlyFilesSelected = false;
        else {
            onlyFilesSelected = true;
            for(Iterator i = selection.iterator(); i.hasNext();) {
                Object item = i.next();
                if(!(item instanceof IAdaptable)) {
                    onlyFilesSelected = false;
                    break;
                }
                    
                IFile file = (IFile)((IAdaptable)item).getAdapter(IFile.class);                    
                if(file == null) {
                    onlyFilesSelected = false;
                    break;
                }
            }
        }

        if(onlyFilesSelected) {
            menu.add(openLinkAction);
            openLinkAction.selectionChanged(selection);

            fillOpenWithMenu(menu, selection);

            menu.add(textInputAction);
            textInputAction.selectionChanged(selection);
        }

        boolean anyResourceSelected;
        if(selection.isEmpty())
            anyResourceSelected = false;
        else {
            anyResourceSelected = true;
            for(Iterator i = selection.iterator(); i.hasNext();) {
                Object item = i.next();
                if(!(item instanceof IAdaptable)) {
                    anyResourceSelected = false;
                    break;
                }

                IResource resource = (IResource)
                    ((IAdaptable)item).getAdapter(IResource.class);
                    
                if(resource == null) {
                    anyResourceSelected = false;
                    break;
                }
            }
        }

        if(anyResourceSelected) {
            addNewWindowAction(menu, selection);
        }

        menu.add(new Separator());
        
        if(onlyFilesSelected) {
            addBookmarkAction.selectionChanged(selection);
            menu.add(addBookmarkAction);
        }

        menu.add(new Separator());
        
        boolean isProjectSelection = true; 
        boolean hasOpenProjects = false;
        boolean hasClosedProjects = false;
        Iterator resources = selection.iterator();

        while(resources.hasNext() &&
                (!hasOpenProjects || !hasClosedProjects || isProjectSelection)) {

            Object next = resources.next();
            IProject project = null;
            
            if(next instanceof IProject)
                project = (IProject) next;
            else if(next instanceof IAdaptable)
                project = (IProject)((IAdaptable) next).getAdapter(IProject.class);
            
            if(project == null) {
                isProjectSelection = false;
                continue;
            }
            
            if(project.isOpen()) {
                hasOpenProjects = true;
            } 
            else {
                hasClosedProjects = true;
            }
        }   

        if(!hasClosedProjects) {
            refreshAction.selectionChanged(selection);
            menu.add(refreshAction);
        }
        if(isProjectSelection) {
            if(hasClosedProjects) {
                openProjectAction.selectionChanged(selection);
                menu.add(openProjectAction);                
            }
            if(hasOpenProjects) {
                closeProjectAction.selectionChanged(selection);
                menu.add(closeProjectAction);
            }
        }                   

        if(onlyFilesSelected) {
            menu.add(new Separator());
            menu.add(updateAction);
            updateAction.selectionChanged(selection);

            menu.add(new Separator());
            menu.add(markReadAction);
            markReadAction.selectionChanged(selection);
        }
                
        menu.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
        menu.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS + "-end"));
        menu.add(new Separator());
    
        if (selection.size() == 1) {
            propertyDialogAction.selectionChanged(selection);
            menu.add(propertyDialogAction);
        }
    }

    private void fillOpenWithMenu(IMenuManager menu, IStructuredSelection selection) {
        if (selection.size() != 1)
            return;
        Object element = selection.getFirstElement();
        if (!(element instanceof IRSSElement))
            return;

        MenuManager submenu =
            new MenuManager("Open &With", OpenActionGroup.OPEN_WITH_ID);
        submenu.add(new OpenWithMenu(navigator.getSite().getPage(), ((IRSSElement)element).getChannel()));
        menu.add(submenu);
    }

    private void addNewWindowAction(IMenuManager menu, IStructuredSelection selection) {
        if (selection.size() != 1)
            return;
        Object element = selection.getFirstElement();
        if (!(element instanceof IContainer))
            return;
        if (element instanceof IProject && !(((IProject)element).isOpen()))
            return;             

        menu.add(new OpenInNewWindowAction(navigator.getSite().getWorkbenchWindow(), (IContainer) element));
    }

    public void runDefaultAction(IStructuredSelection selection) {
        openLinkAction.selectionChanged(selection);
        if(openLinkAction.isEnabled())
            openLinkAction.run();
    }

    public void fillActionBars(IActionBars actionBars) {
        actionBars.setGlobalActionHandler(
            IWorkbenchActionConstants.PROPERTIES,
            propertyDialogAction);
        actionBars.setGlobalActionHandler(
            IWorkbenchActionConstants.BOOKMARK,
            addBookmarkAction);
        actionBars.setGlobalActionHandler(
            IWorkbenchActionConstants.ADD_TASK,
            addTaskAction);
            
        actionBars.setGlobalActionHandler(
            IWorkbenchActionConstants.REFRESH,
            refreshAction);
        actionBars.setGlobalActionHandler(
            IWorkbenchActionConstants.OPEN_PROJECT,
            openProjectAction);
        actionBars.setGlobalActionHandler(
            IWorkbenchActionConstants.CLOSE_PROJECT,
            closeProjectAction);
        
        IToolBarManager toolBar = actionBars.getToolBarManager();
        toolBar.add(new Separator());
        toolBar.add(toggleShowNewOnlyAction);
        toolBar.add(collapseAllAction);     
    }
    
    public void updateActionBars() {
        IStructuredSelection selection =
            (IStructuredSelection) getContext().getSelection();
        propertyDialogAction.setEnabled(selection.size() == 1);
        addBookmarkAction.selectionChanged(selection);
        addTaskAction.selectionChanged(selection);      
        
        refreshAction.selectionChanged(selection);
        openProjectAction.selectionChanged(selection);
        closeProjectAction.selectionChanged(selection);
        
        openLinkAction.selectionChanged(selection);
        textInputAction.selectionChanged(selection);
        updateAction.selectionChanged(selection);
        markReadAction.selectionChanged(selection);
    } 
    
    public void dispose() {
        IWorkspace workspace = ResourcesPlugin.getWorkspace();
        workspace.removeResourceChangeListener(openProjectAction);
        workspace.removeResourceChangeListener(closeProjectAction);
        super.dispose();
    }

    protected ImageDescriptor getImageDescriptor(String relativePath) {
        String iconPath = "icons/full/";
        try {
            AbstractUIPlugin plugin = (AbstractUIPlugin)
                Platform.getPlugin(PlatformUI.PLUGIN_ID);
            URL installURL = plugin.getDescriptor().getInstallURL();
            URL url = new URL(installURL, iconPath + relativePath);
            return ImageDescriptor.createFromURL(url);
        } 
        catch(MalformedURLException ex) {
            return ImageDescriptor.getMissingImageDescriptor();
        }
    }

    public void setShowNewOnly(boolean value) {
        toggleShowNewOnlyAction.setChecked(value);
    }
}
