/*
 * Created on Nov 26, 2003
 * Version $Id$
 */
package com.pnehrer.rss.internal.core;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IStorage;
import org.eclipse.core.runtime.IAdapterFactory;

import com.pnehrer.rss.core.IRSSElement;

/**
 * @author <a href="mailto:pnehrer@freeshell.org">Peter Nehrer</a>
 */
public class ResourceAdapterFactory implements IAdapterFactory {

    private static final Class[] ADAPTER_LIST = {
        IResource.class,
        IStorage.class, 
        IFile.class};

    /* (non-Javadoc)
     * @see org.eclipse.core.runtime.IAdapterFactory#getAdapter(java.lang.Object, java.lang.Class)
     */
    public Object getAdapter(Object adaptableObject, Class adapterType) {
        return ((IRSSElement)adaptableObject).getChannel().getFile();
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.runtime.IAdapterFactory#getAdapterList()
     */
    public Class[] getAdapterList() {
        return ADAPTER_LIST;
    }
}
