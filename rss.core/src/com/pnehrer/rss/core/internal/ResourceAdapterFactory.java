/*
 * Created on Nov 26, 2003
 * Version $Id$
 */
package com.pnehrer.rss.core.internal;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdapterFactory;

import com.pnehrer.rss.core.IChannel;

/**
 * @author <a href="mailto:pnehrer@freeshell.org">Peter Nehrer</a>
 */
public class ResourceAdapterFactory implements IAdapterFactory {

    private static final Class[] ADAPTER_LIST = { IFile.class, IChannel.class };

    /* (non-Javadoc)
     * @see org.eclipse.core.runtime.IAdapterFactory#getAdapter(java.lang.Object, java.lang.Class)
     */
    public Object getAdapter(Object adaptableObject, Class adapterType) {
        if(adapterType.isAssignableFrom(IFile.class) 
            && adaptableObject instanceof IChannel)

            return ((IChannel)adaptableObject).getFile();
        else if(IChannel.class.equals(adapterType)
            && adaptableObject instanceof IFile) {

            try {
                return ChannelManager.getInstance().getChannel(
                    (IFile)adaptableObject);
            }
            catch(CoreException ex) {
                return null;
            }
        }
        else
            return null;
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.runtime.IAdapterFactory#getAdapterList()
     */
    public Class[] getAdapterList() {
        return ADAPTER_LIST;
    }
}
