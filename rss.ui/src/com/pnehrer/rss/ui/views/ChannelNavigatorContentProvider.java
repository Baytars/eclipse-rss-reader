/*
 * Created on Nov 17, 2003
 * Version $Id$
 */
package com.pnehrer.rss.ui.views;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarkerDelta;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.AbstractTreeViewer;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.model.WorkbenchContentProvider;

import com.pnehrer.rss.core.IChannel;
import com.pnehrer.rss.core.IRSSElement;
import com.pnehrer.rss.core.RSSCore;
import com.pnehrer.rss.ui.RSSUI;

/**
 * @author <a href="mailto:pnehrer@freeshell.org">Peter Nehrer</a>
 */
public class ChannelNavigatorContentProvider 
    extends WorkbenchContentProvider {

    private static final Object[] NO_CHILDREN = {};
    private Viewer viewer;
    
    /* (non-Javadoc)
	 * @see org.eclipse.ui.model.WorkbenchContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
	 */
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		this.viewer = viewer;
		super.inputChanged(viewer, oldInput, newInput);
	}

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
     */
    public Object[] getChildren(Object element) {
        Object[] children = super.getChildren(element);
        if(element instanceof IRSSElement)
            return children;
        else {
            Collection newChildren = new ArrayList(children.length / 2 + 1);
            for(int i = 0, n = children.length; i < n; ++i) {
                if(children[i] instanceof IFile) {
                    IFile file = (IFile)children[i];
                    try {
                        IChannel channel = 
                            RSSCore.getPlugin().getChannel(file);
                        if(channel != null)
                            newChildren.add(channel);
                    }
                    catch(CoreException ex) {
                        RSSUI.getDefault().getLog().log(ex.getStatus());
                    }
                }
                else
                    newChildren.add(children[i]);
            }

            return newChildren.toArray();
        }
    }
	
	/**
	 * Process the resource delta.
	 * 
	 * @param delta
	 */
	protected void processDelta(IResourceDelta delta) {		

		Control ctrl = viewer.getControl();
		if (ctrl == null || ctrl.isDisposed())
			return;
		
		
		final Collection runnables = new ArrayList();
		processDelta(delta, runnables);

		if (runnables.isEmpty())
			return;

		//Are we in the UIThread? If so spin it until we are done
		if (ctrl.getDisplay().getThread() == Thread.currentThread()) {
			runUpdates(runnables);
		} else {
			ctrl.getDisplay().asyncExec(new Runnable(){
				/* (non-Javadoc)
				 * @see java.lang.Runnable#run()
				 */
				public void run() {
					//Abort if this happens after disposes
					Control ctrl = viewer.getControl();
					if (ctrl == null || ctrl.isDisposed())
						return;
					
					runUpdates(runnables);
				}
			});
		}

	}

	/**
	 * Run all of the runnables that are the widget updates
	 * @param runnables
	 */
	private void runUpdates(Collection runnables) {
		Iterator runnableIterator = runnables.iterator();
		while(runnableIterator.hasNext()){
			((Runnable)runnableIterator.next()).run();
		}
		
	}

	/**
	 * Process a resource delta. Add any runnables
	 */
	private void processDelta(IResourceDelta delta, Collection runnables) {
		//he widget may have been destroyed
		// by the time this is run. Check for this and do nothing if so.
		Control ctrl = viewer.getControl();
		if (ctrl == null || ctrl.isDisposed())
			return;

		// Get the affected resource
		final IResource resource = delta.getResource();
	
		// If any children have changed type, just do a full refresh of this
		// parent,
		// since a simple update on such children won't work,
		// and trying to map the change to a remove and add is too dicey.
		// The case is: folder A renamed to existing file B, answering yes to
		// overwrite B.
		IResourceDelta[] affectedChildren = delta
				.getAffectedChildren(IResourceDelta.CHANGED);
		for (int i = 0; i < affectedChildren.length; i++) {
			if ((affectedChildren[i].getFlags() & IResourceDelta.TYPE) != 0) {
				runnables.add(getRefreshRunnable(resource));
				return;
			}
		}

        IRSSElement rssElement = (IRSSElement)
        resource.getAdapter(IRSSElement.class);
	    final Object o;
	    if(rssElement == null)
	        o = resource;
	    else 
	        o = rssElement;
		
		// Check the flags for changes the Navigator cares about.
		int changeFlags = delta.getFlags();
		if ((changeFlags & (IResourceDelta.OPEN | IResourceDelta.SYNC
				| IResourceDelta.TYPE | IResourceDelta.DESCRIPTION)) != 0) {
			Runnable updateRunnable =  new Runnable(){
				/* (non-Javadoc)
				 * @see java.lang.Runnable#run()
				 */
				public void run() {
					((StructuredViewer) viewer).update(o, null);
			
				}
			};
			runnables.add(updateRunnable);
		}

        if(rssElement != null && (changeFlags & IResourceDelta.MARKERS) != 0) {
            IMarkerDelta[] markerDeltas = delta.getMarkerDeltas();
            for(int i = 0; i < markerDeltas.length; ++i) {
                if(RSSCore.MARKER_UPDATE.equals(markerDeltas[i].getType())) {
					runnables.add(getRefreshRunnable(o));
                    return;
                }
            }
        }
		
		// Replacing a resource may affect its label and its children
		if ((changeFlags & IResourceDelta.REPLACED) != 0) {
			runnables.add(getRefreshRunnable(o));
			return;
		}


		// Handle changed children .
		for (int i = 0; i < affectedChildren.length; i++) {
			processDelta(affectedChildren[i], runnables);
		}

		// @issue several problems here:
		//  - should process removals before additions, to avoid multiple equal
		// elements in viewer
		//   - Kim: processing removals before additions was the indirect cause of
		// 44081 and its varients
		//   - Nick: no delta should have an add and a remove on the same element,
		// so processing adds first is probably OK
		//  - using setRedraw will cause extra flashiness
		//  - setRedraw is used even for simple changes
		//  - to avoid seeing a rename in two stages, should turn redraw on/off
		// around combined removal and addition
		//   - Kim: done, and only in the case of a rename (both remove and add
		// changes in one delta).

		IResourceDelta[] addedChildren = delta
				.getAffectedChildren(IResourceDelta.ADDED);
		IResourceDelta[] removedChildren = delta
				.getAffectedChildren(IResourceDelta.REMOVED);

		if (addedChildren.length == 0 && removedChildren.length == 0)
			return;

		final Object[] addedObjects;
		final Object[] removedObjects;

		// Process additions before removals as to not cause selection
		// preservation prior to new objects being added
		// Handle added children. Issue one update for all insertions.
		if (addedChildren.length > 0) {
			addedObjects = new Object[addedChildren.length];
            for (int i = 0; i < addedChildren.length; i++) {
                IResource addedResource = addedChildren[i].getResource();
                IRSSElement addedRSSElement = (IRSSElement)
                    addedResource.getAdapter(IRSSElement.class);
                if(addedRSSElement == null)
                    addedObjects[i] = addedResource;
                else
                    addedObjects[i] = addedRSSElement;
            }
		} else
			addedObjects = new Object[0];

		// Handle removed children. Issue one update for all removals.
		if (removedChildren.length > 0) {
			removedObjects = new Object[removedChildren.length];
            for (int i = 0; i < removedChildren.length; i++) {
                IResource removedResource = removedChildren[i].getResource();
                // TODO Get rid of the rss file extension reference.
                if(removedResource.getType() == IResource.FILE
                    && "rss".equals(removedResource.getFileExtension())) {

                    // There is no way we can get an IRSSElement from a deleted file at this point
					runnables.add(getRefreshRunnable(resource));
                    return;
                } else
					removedObjects[i] = removedChildren[i].getResource();
            }

		} else
			removedObjects = new Object[0];
		
		Runnable addAndRemove = new Runnable(){
			/* (non-Javadoc)
			 * @see java.lang.Runnable#run()
			 */
			public void run() {

				// Disable redraw until the operation is finished so we don't
				// get a flash of both the new and old item (in the case of
				// rename)
				// Only do this if we're both adding and removing files (the
				// rename case)
				viewer.getControl().setRedraw(false);
				try {

					if (viewer instanceof AbstractTreeViewer) {
						AbstractTreeViewer treeViewer = (AbstractTreeViewer) viewer;
						if (addedObjects.length > 0)
							treeViewer.add(resource, addedObjects);
						if (removedObjects.length > 0)
							treeViewer.remove(removedObjects);

					} else
						((StructuredViewer) viewer).refresh(resource);

				} finally {
					viewer.getControl().setRedraw(true);
				}
				
			}
		};

		
		runnables.add(addAndRemove);
	}

	/**
	 * Return a runnable for refreshing a resource.
	 * @param resource
	 * @return Runnable
	 */
	private Runnable getRefreshRunnable(final Object resource) {
		return new Runnable(){
			/* (non-Javadoc)
			 * @see java.lang.Runnable#run()
			 */
			public void run() {
				((StructuredViewer) viewer).refresh(resource);

			}
		};
	}	
}
