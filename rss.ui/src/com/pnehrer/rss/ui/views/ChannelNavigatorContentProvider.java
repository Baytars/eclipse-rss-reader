/*
 * Created on Nov 17, 2003
 * Version $Id$
 */
package com.pnehrer.rss.ui.views;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.ui.model.WorkbenchContentProvider;

import com.pnehrer.rss.core.RSSCore;

/**
 * @author <a href="mailto:pnehrer@freeshell.org">Peter Nehrer</a>
 */
public class ChannelNavigatorContentProvider extends WorkbenchContentProvider {

    private static final Object[] NO_CHILDREN = {};

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
     */
    public Object[] getChildren(Object element) {
        if(!(element instanceof IAdaptable)) 
            return NO_CHILDREN;
        
        IResource resource = element instanceof IResource ?
            (IResource)element :
            (IResource)((IAdaptable)element).getAdapter(IResource.class);
            
        if(resource instanceof IContainer) {
            RSSCore core = RSSCore.getPlugin();
            Collection list = new ArrayList();
            IContainer container = (IContainer)resource;
            IResource[] members;
            try {
                members = container.members();
            }
            catch(CoreException ex) {
                // TODO Log me!
                return NO_CHILDREN;
            }
            
            for(int i = 0, n = members.length; i < n; ++i) {
                if(members[i].getType() == IResource.FILE) {
                    if("rss".equals(members[i].getFileExtension())) {                
                        try {
                            list.add(core.getChannel((IFile)members[i]));
                        }
                        catch(CoreException ex) {
                            // TODO Log me!
                        }
                    }
                }
                else
                    list.add(members[i]);
            }
            
            return list.toArray();
        }
        else
            return super.getChildren(element);
    }
}
