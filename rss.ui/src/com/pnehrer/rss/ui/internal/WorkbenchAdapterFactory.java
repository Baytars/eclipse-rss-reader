/*
 * Created on Nov 17, 2003
 * Version $Id$
 */
package com.pnehrer.rss.ui.internal;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.ui.model.IWorkbenchAdapter;

import com.pnehrer.rss.core.IChannel;
import com.pnehrer.rss.core.IItem;

/**
 * @author <a href="mailto:pnehrer@freeshell.org">Peter Nehrer</a>
 */
public class WorkbenchAdapterFactory implements IAdapterFactory {

    private static final Class[] ADAPTER_LIST = { IWorkbenchAdapter.class };
    private static final WorkbenchChannelAdapter channelAdapter =
        new WorkbenchChannelAdapter();
    private static final WorkbenchItemAdapter itemAdapter =
        new WorkbenchItemAdapter();

    /* (non-Javadoc)
     * @see org.eclipse.core.runtime.IAdapterFactory#getAdapter(java.lang.Object, java.lang.Class)
     */
    public Object getAdapter(Object adaptableObject, Class adapterType) {
        if(IWorkbenchAdapter.class.equals(adapterType)) {
            if(adaptableObject instanceof IChannel)
                return channelAdapter;
            else if(adaptableObject instanceof IItem)
                return itemAdapter;
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
