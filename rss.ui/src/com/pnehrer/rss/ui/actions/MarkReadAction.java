/*
 * Created on Mar 23, 2004
 * Version $Id$
 */
package com.pnehrer.rss.ui.actions;

import org.eclipse.ui.IWorkbenchWindow;

import com.pnehrer.rss.core.IChannel;
import com.pnehrer.rss.core.IItem;
import com.pnehrer.rss.core.IRSSElement;

/**
 * @author <a href="mailto:pnehrer@freeshell.org">Peter Nehrer</a>
 */
public class MarkReadAction extends ToggleUpdateFlagAction {

    public MarkReadAction() {
        this(null, null);
    }
    
    public MarkReadAction(IWorkbenchWindow window) {
        this("Mark as Read", window);
    }

    /**
     * @param text
     */
    public MarkReadAction(String text, IWorkbenchWindow window) {
        super(text, window);
    }

    /* (non-Javadoc)
     * @see com.pnehrer.rss.ui.actions.ToggleUpdateFlagAction#update(com.pnehrer.rss.core.IRSSElement)
     */
    protected void update(IRSSElement rssElement) {
        if(rssElement instanceof IItem) {
            IItem item = (IItem)rssElement;
            if(item.isUpdated()) 
                item.resetUpdateFlag();
        }
        else if(rssElement instanceof IChannel) {
            IChannel channel = (IChannel)rssElement;
            if(channel.hasUpdates())
                channel.resetUpdateFlags();
        }
    }
}
