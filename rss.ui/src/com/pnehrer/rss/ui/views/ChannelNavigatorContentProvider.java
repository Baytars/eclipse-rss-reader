/*
 * Created on Nov 17, 2003
 * Version $Id$
 */
package com.pnehrer.rss.ui.views;

import java.util.ArrayList;
import java.util.Collection;

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

    /* (non-Javadoc)
     * @see org.eclipse.ui.model.WorkbenchContentProvider#processDelta(org.eclipse.core.resources.IResourceDelta)
     */
    protected void processDelta(IResourceDelta delta) {
        // This method runs inside a syncExec.  The widget may have been destroyed
        // by the time this is run.  Check for this and do nothing if so.
        Control ctrl = viewer.getControl();
        if (ctrl == null || ctrl.isDisposed())
            return;

        // Get the affected resource
        IResource resource = delta.getResource();

        // If any children have changed type, just do a full refresh of this parent,
        // since a simple update on such children won't work, 
        // and trying to map the change to a remove and add is too dicey.
        // The case is: folder A renamed to existing file B, answering yes to overwrite B.
        IResourceDelta[] affectedChildren =
            delta.getAffectedChildren(IResourceDelta.CHANGED);
        for (int i = 0; i < affectedChildren.length; i++) {
            if ((affectedChildren[i].getFlags() & IResourceDelta.TYPE) != 0) {
                ((StructuredViewer) viewer).refresh(resource);
                return;
            }
        }

        IRSSElement rssElement = (IRSSElement)
            resource.getAdapter(IRSSElement.class);
        Object o;
        if(rssElement == null)
            o = resource;
        else 
            o = rssElement;

        // Check the flags for changes the Navigator cares about.
        int changeFlags = delta.getFlags();
        if ((changeFlags
            & (IResourceDelta.OPEN | IResourceDelta.SYNC))
            != 0) {
            ((StructuredViewer) viewer).update(o, null);
        }
        
        if(rssElement != null && (changeFlags & IResourceDelta.MARKERS) != 0) {
            IMarkerDelta[] markerDeltas = delta.getMarkerDeltas();
            for(int i = 0; i < markerDeltas.length; ++i) {
                if(RSSCore.MARKER_UPDATE.equals(markerDeltas[i].getType())) {
                    ((StructuredViewer) viewer).refresh(o, true);
                    return;
                }
            }
        }

        // Replacing a resource may affect its label and its children
        if ((changeFlags & IResourceDelta.REPLACED) != 0) {
            ((StructuredViewer) viewer).refresh(o, true);
            return;
        }

        // Handle changed children .
        for (int i = 0; i < affectedChildren.length; i++) {
            processDelta(affectedChildren[i]);
        }

        // Process removals before additions, to avoid multiple equal elements in the viewer.

        // Handle removed children. Issue one update for all removals.
        affectedChildren = delta.getAffectedChildren(IResourceDelta.REMOVED);
        if (affectedChildren.length > 0) {
            Object[] affected = new Object[affectedChildren.length];
            for (int i = 0; i < affectedChildren.length; i++) {
                IResource affectedResource = affectedChildren[i].getResource();
                // TODO Get rid of the rss file extension reference.
                if(affectedResource.getType() == IResource.FILE
                    && "rss".equals(affectedResource.getFileExtension())) {

                    // There is no way we can get an IRSSElement from a deleted file at this point
                    ((StructuredViewer)viewer).refresh(resource);
                    return;
                }
            }

            if (viewer instanceof AbstractTreeViewer) {
                ((AbstractTreeViewer) viewer).remove(affected);
            } else {
                ((StructuredViewer) viewer).refresh(resource);
            }
        }

        // Handle added children. Issue one update for all insertions.
        affectedChildren = delta.getAffectedChildren(IResourceDelta.ADDED);
        if (affectedChildren.length > 0) {
            Object[] affected = new Object[affectedChildren.length];
            for (int i = 0; i < affectedChildren.length; i++) {
                IResource affectedResource = affectedChildren[i].getResource();
                IRSSElement affectedRSSElement = (IRSSElement)
                    affectedResource.getAdapter(IRSSElement.class);
                if(affectedRSSElement == null)
                    affected[i] = affectedResource;
                else
                    affected[i] = affectedRSSElement;
            }

            if (viewer instanceof AbstractTreeViewer) {
                ((AbstractTreeViewer) viewer).add(resource, affected);
            }
            else {
                ((StructuredViewer) viewer).refresh(resource);
            }
        }
    }
}
