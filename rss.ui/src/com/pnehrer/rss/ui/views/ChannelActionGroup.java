/*
 * Created on Dec 4, 2003
 * Version $Id$
 */
package com.pnehrer.rss.ui.views;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.actions.ActionGroup;

import com.pnehrer.rss.ui.RSSUI;
import com.pnehrer.rss.ui.actions.OpenLinkAction;
import com.pnehrer.rss.ui.actions.UpdateAction;

/**
 * @author <a href="mailto:pnehrer@freeshell.org">Peter Nehrer</a>
 */
public class ChannelActionGroup extends ActionGroup {
    
    private final OpenLinkAction openChannelLinkAction;
    private final OpenLinkAction openItemLinkAction;
    private final UpdateAction updateAction;
    
    private final ChannelDetailView channelDetailView;
    
    public ChannelActionGroup(ChannelDetailView channelDetailView) {
        this.channelDetailView = channelDetailView;
        ImageRegistry reg = RSSUI.getDefault().getImageRegistry();

        openChannelLinkAction = new OpenLinkAction(
            channelDetailView.getSite().getShell());
        openChannelLinkAction.setToolTipText("Open channel link in browser.");
        openChannelLinkAction.setImageDescriptor(reg.getDescriptor(RSSUI.BROWSE_ICON));

        openItemLinkAction = new OpenLinkAction(
            channelDetailView.getSite().getShell());
        openItemLinkAction.setToolTipText("Open item link in browser.");
        openItemLinkAction.setImageDescriptor(reg.getDescriptor(RSSUI.BROWSE_ICON));

        updateAction = new UpdateAction();
        updateAction.setToolTipText("Update channel from its source.");
        updateAction.setImageDescriptor(reg.getDescriptor(RSSUI.UPDATE_ICON));
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.actions.ActionGroup#fillActionBars(org.eclipse.ui.IActionBars)
     */
    public void fillActionBars(IActionBars actionBars) {
        IMenuManager menu = actionBars.getMenuManager();
        menu.add(openChannelLinkAction);
        menu.add(updateAction);
        
        IToolBarManager toolBar = actionBars.getToolBarManager();
        toolBar.add(new Separator());
        toolBar.add(openChannelLinkAction);
        toolBar.add(updateAction);
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.actions.ActionGroup#fillContextMenu(org.eclipse.jface.action.IMenuManager)
     */
    public void fillContextMenu(IMenuManager menu) {
        IStructuredSelection selection =
            (IStructuredSelection)getContext().getSelection();
    
        menu.add(openItemLinkAction);
        openItemLinkAction.selectionChanged(selection);
        menu.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.ui.actions.ActionGroup#updateActionBars()
     */
    public void updateActionBars() {
        IStructuredSelection selection =
            (IStructuredSelection) getContext().getSelection();
        openChannelLinkAction.selectionChanged(selection);
        updateAction.selectionChanged(selection);
    }

    public void runDefaultAction(IStructuredSelection selection) {
        openItemLinkAction.selectionChanged(selection);
        if(openItemLinkAction.isEnabled())
            openItemLinkAction.run();
    }
}
