/*
 * Created on Nov 16, 2003
 * Version $Id$
 */
package com.pnehrer.rss.ui.actions;

import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.actions.SelectionListenerAction;

import com.pnehrer.rss.core.IChannel;
import com.pnehrer.rss.core.IRSSElement;
import com.pnehrer.rss.ui.RSSUI;

/**
 * @author <a href="mailto:pnehrer@freeshell.org">Peter Nehrer</a>
 */
public class UpdateAction extends SelectionListenerAction {
    
    public UpdateAction() {
        super("&Update from Source");
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.actions.SelectionListenerAction#updateSelection(org.eclipse.jface.viewers.IStructuredSelection)
     */
    protected boolean updateSelection(IStructuredSelection selection) {
        if(super.updateSelection(selection)) {
            Object object = selection.getFirstElement();
            if(object instanceof IAdaptable) {
                IRSSElement rssElement = (IRSSElement)
                    ((IAdaptable)object).getAdapter(IRSSElement.class);
                return rssElement != null;
            }
        }

        return false;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.action.IAction#run()
     */
    public void run() {
        IStructuredSelection selection = getStructuredSelection();
        final ArrayList channels = new ArrayList(selection.size());
        for(Iterator i = selection.iterator(); i.hasNext();) {
            Object item = i.next();
            IRSSElement rssElement = null;
            if(item instanceof IAdaptable)
                rssElement = (IRSSElement)
                    ((IAdaptable)item).getAdapter(IRSSElement.class);

            if(rssElement != null)
                channels.add(rssElement.getChannel());
        }
        
        Job job = new Job("Updating selected channels") {
			protected IStatus run(IProgressMonitor monitor) {
            	MultiStatus status = 
            		new MultiStatus(
            			RSSUI.PLUGIN_ID, 
						0, 
						"Errors occurred while updating selected channels.",
						null);
            	
            	if (monitor != null)
            		monitor.beginTask("Updating channels... ", channels.size());
            	try {
	            	for (Iterator i = channels.iterator(); i.hasNext();) {
	            		IChannel channel = (IChannel) i.next();
	            		try {
		            		channel.update(
		            			monitor == null 
								? null 
								: new SubProgressMonitor(monitor, 1));
	            		} catch (CoreException ex) {
	            			status.add(ex.getStatus());
	            		}
	            	}
	            	
	            	return status;
	        	} finally {
	        		if (monitor != null)
	        			monitor.done();
	        	}
			}
        };
        
        job.setUser(true);
        job.schedule();
    }
}
