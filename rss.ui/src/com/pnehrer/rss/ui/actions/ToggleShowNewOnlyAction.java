/*
 * Created on Mar 5, 2004
 * Version $Id$
 */
package com.pnehrer.rss.ui.actions;

import org.eclipse.jface.action.Action;

/**
 * @author <a href="mailto:pnehrer@freeshell.org">Peter Nehrer</a>
 */
public class ToggleShowNewOnlyAction extends Action {
    
    public interface INewItemFilteringPart {
        
        public void setShowNewOnly(boolean value);
    }
    
    private final INewItemFilteringPart part;

    /**
     * @param text
     */
    public ToggleShowNewOnlyAction(INewItemFilteringPart part) {
        super("Show New Items Only", AS_CHECK_BOX);
        this.part = part;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.action.IAction#run()
     */
    public void run() {
        part.setShowNewOnly(isChecked());
    }
}
