/*
 * Created on Mar 5, 2004
 * Version $Id$
 */
package com.pnehrer.rss.ui.actions;

import org.eclipse.jface.action.Action;

import com.pnehrer.rss.ui.views.ChannelDetailView;

/**
 * @author <a href="mailto:pnehrer@freeshell.org">Peter Nehrer</a>
 */
public class ToggleShowNewOnlyAction extends Action {
    
    private final ChannelDetailView channelDetailView;

    /**
     * @param text
     */
    public ToggleShowNewOnlyAction(ChannelDetailView channelDetailView) {
        super("Show New Items Only", AS_CHECK_BOX);
        this.channelDetailView = channelDetailView;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.action.IAction#run()
     */
    public void run() {
        channelDetailView.setShowNewOnly(isChecked());
    }
}
