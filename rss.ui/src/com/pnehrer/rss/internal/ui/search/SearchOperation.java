/*
 * Created on Mar 14, 2004
 * Version $Id$
 */
package com.pnehrer.rss.internal.ui.search;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.search.ui.IGroupByKeyComputer;
import org.eclipse.search.ui.ISearchResultView;
import org.eclipse.search.ui.ISearchResultViewEntry;
import org.eclipse.search.ui.SearchUI;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.actions.WorkspaceModifyOperation;

import com.pnehrer.rss.core.IChannel;
import com.pnehrer.rss.core.IItem;
import com.pnehrer.rss.core.IRSSElement;
import com.pnehrer.rss.core.RSSCore;
import com.pnehrer.rss.ui.RSSUI;
import com.pnehrer.rss.ui.views.ChannelDetailView;

/**
 * @author <a href="mailto:pnehrer@freeshell.org">Peter Nehrer</a>
 */
public class SearchOperation extends WorkspaceModifyOperation {
    
    private static final String MARKER_HIT = RSSUI.PLUGIN_ID + ".rsssearchhitmarker";
    private static final String ATTR_LINK = "link";
    
    private final String term;
    private final boolean caseSensitive; 
    private final int fieldMask;
    private final Object[] workingSet;

    public SearchOperation(
        String term, 
        boolean caseSensitive, 
        int fieldMask, 
        Object[] workingSet) {
            
        this.term = term;
        this.caseSensitive = caseSensitive;
        this.fieldMask = fieldMask;
        this.workingSet = workingSet;
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.actions.WorkspaceModifyOperation#execute(org.eclipse.core.runtime.IProgressMonitor)
     */
    protected void execute(IProgressMonitor monitor)
        throws CoreException, InvocationTargetException, InterruptedException {
                 
        ISearchResultView view = SearchUI.getSearchResultView();
        view.searchStarted(
            null,
            term + " - 1 RSS Item",
            term + " - {0} RSS Items",
            RSSUI.getDefault().getImageRegistry().getDescriptor(RSSUI.XML_ICON),
            RSSSearchPage.PAGE_ID,
            new LabelProvider() {
                private final HashMap images = new HashMap();
                
                public Image getImage(Object element) {
                    ISearchResultViewEntry entry = (ISearchResultViewEntry)element;
                    IChannel channel = (IChannel)entry.getGroupByKey();
                    ImageDescriptor descriptor = 
                        RSSUI.getDefault().getImageDescriptor16x16(channel); 
                    Image image = (Image)images.get(descriptor);
                    if(image == null) {
                        image = descriptor.createImage();
                        images.put(descriptor, image);
                    }
                    
                    return image;
                }

                public String getText(Object element) {
                    ISearchResultViewEntry entry = (ISearchResultViewEntry)element;
                    IChannel channel = (IChannel)entry.getGroupByKey();
                    return channel.getTitle();
                }
                
                public void dispose() {
                    for(Iterator i = images.values().iterator(); i.hasNext();)
                        ((Image)i.next()).dispose();
                        
                    super.dispose();
                }
            },
            new Action() {
                public void run() {
                    ISearchResultView view = SearchUI.getSearchResultView();
                    ChannelDetailView channelDetailView; 
                    try {
                        channelDetailView = 
                            (ChannelDetailView)view
                                .getViewSite()
                                .getPage()
                                .showView("com.pnehrer.rss.ui.views.ChannelDetail");
                    }
                    catch(PartInitException ex) {
                        RSSUI.getDefault().getLog().log(ex.getStatus());
                        return;
                    }
                    
                    ISelection selection = view.getSelection();
                    Object element = null;
                    if(selection instanceof IStructuredSelection)
                        element = ((IStructuredSelection)selection).getFirstElement();
                        
                    if(element instanceof ISearchResultViewEntry) {
                        ISearchResultViewEntry entry = (ISearchResultViewEntry)element;
                        IMarker marker = entry.getSelectedMarker();
                        String link = marker.getAttribute(ATTR_LINK, "");
                        IChannel channel = (IChannel)entry.getGroupByKey();
                        IItem[] items = channel.getItems();
                        for(int i = 0; i < items.length; ++i) {
                            if(link.equals(items[i].getLink())) {
                                channelDetailView
                                    .selectionChanged(
                                        view,
                                        new StructuredSelection(items[i]));
                                break;
                            }
                        }
                    }
                }
            },
            new IGroupByKeyComputer() {
                public Object computeGroupByKey(IMarker marker) {
                    String link = marker.getAttribute(ATTR_LINK, "");
                    IRSSElement rssElement = (IRSSElement)
                        marker.getResource().getAdapter(IRSSElement.class);
                    if(rssElement == null)
                        return null;
                    else 
                        return rssElement.getChannel();
                }
            },
            this);
                 
        try {
            IItem[] items = 
                RSSCore.getPlugin().search(
                    term,
                    caseSensitive,
                    fieldMask,
                    workingSet,
                    monitor);
            
            HashSet resources = new HashSet();
            for(int i = 0; i < items.length; ++i) {
                IFile file = items[i].getChannel().getFile();
                if(resources.add(file))
                    file.deleteMarkers(MARKER_HIT, false, IResource.DEPTH_INFINITE);
            }
                        
            for(int i = 0; i < items.length; ++i) {
                IChannel channel = items[i].getChannel();
                IFile file = channel.getFile();
                IMarker marker = file.createMarker(MARKER_HIT);
                marker.setAttribute(ATTR_LINK, items[i].getLink());
                view.addMatch(
                    items[i].getTitle(), 
                    channel, 
                    file, 
                    marker);
            }
        }
        finally {
            view.searchFinished();
        }
    }
}
