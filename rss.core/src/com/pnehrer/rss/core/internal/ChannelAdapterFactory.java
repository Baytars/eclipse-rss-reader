/*
 * Created on Dec 5, 2003
 * Version $Id$
 */
package com.pnehrer.rss.core.internal;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdapterFactory;

import com.pnehrer.rss.core.IRSSElement;

/**
 * @author <a href="mailto:pnehrer@freeshell.org">Peter Nehrer</a>
 */
public class ChannelAdapterFactory implements IAdapterFactory {

    private static final Class[] ADAPTER_LIST = { IRSSElement.class };

    /* (non-Javadoc)
     * @see org.eclipse.core.runtime.IAdapterFactory#getAdapter(java.lang.Object, java.lang.Class)
     */
    public Object getAdapter(Object adaptableObject, Class adapterType) {
        try {
            return ChannelManager.getInstance().getChannel(
                (IFile)adaptableObject);
        }
        catch(CoreException ex) {
            return null;
        }
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.runtime.IAdapterFactory#getAdapterList()
     */
    public Class[] getAdapterList() {
        return ADAPTER_LIST;
    }
}
