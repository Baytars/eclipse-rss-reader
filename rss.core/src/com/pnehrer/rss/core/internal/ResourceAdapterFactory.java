/*
 * Created on Nov 26, 2003
 * Version $Id$
 */
package com.pnehrer.rss.core.internal;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IAdapterFactory;

import com.pnehrer.rss.core.IRSSElement;

/**
 * @author <a href="mailto:pnehrer@freeshell.org">Peter Nehrer</a>
 */
public class ResourceAdapterFactory implements IAdapterFactory {

    private static final Class[] ADAPTER_LIST = {
        IFile.class, 
        IRSSElement.class};

    /* (non-Javadoc)
     * @see org.eclipse.core.runtime.IAdapterFactory#getAdapter(java.lang.Object, java.lang.Class)
     */
    public Object getAdapter(Object adaptableObject, Class adapterType) {
        if(adapterType.isAssignableFrom(IFile.class)) { 
            if(adaptableObject instanceof IRSSElement)
                return ((IRSSElement)adaptableObject).getChannel().getFile();
        }
        else if(IRSSElement.class.isAssignableFrom(adapterType)) {
            IFile file = adaptableObject instanceof IAdaptable ? 
                (IFile)((IAdaptable)adaptableObject).getAdapter(IFile.class) :
                null;
                
            if(file != null) {
                try {
                    return ChannelManager.getInstance().getChannel(file);
                }
                catch(CoreException ex) {
                    return null;
                }
            }
        }

        return null;
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.runtime.IAdapterFactory#getAdapterList()
     */
    public Class[] getAdapterList() {
        return ADAPTER_LIST;
    }
}
