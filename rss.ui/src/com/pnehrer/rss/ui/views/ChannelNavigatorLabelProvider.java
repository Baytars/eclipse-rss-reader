/*
 * Created on Mar 4, 2004
 * Version $Id$
 */
package com.pnehrer.rss.ui.views;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.model.IWorkbenchAdapter;

import com.pnehrer.rss.core.IChannel;
import com.pnehrer.rss.core.IItem;
import com.pnehrer.rss.core.IRSSElement;
import com.pnehrer.rss.internal.ui.NewChannelImageDescriptor;
import com.pnehrer.rss.ui.RSSUI;

/**
 * @author <a href="mailto:pnehrer@freeshell.org">Peter Nehrer</a>
 */
public class ChannelNavigatorLabelProvider
    extends LabelProvider
    implements IColorProvider {
            
    private Map imageTable;
    private ImageDescriptor newItemIcon;
    private ImageDescriptor newDecoratorIcon;
    private Color oldItemColor;

    public ChannelNavigatorLabelProvider() {
        ImageRegistry reg = RSSUI.getDefault().getImageRegistry(); 
        newItemIcon = reg.getDescriptor(RSSUI.ITEM_NEW_ICON);
        newDecoratorIcon = reg.getDescriptor(RSSUI.NEW_DECORATOR_ICON);
        
        Display display = Display.getCurrent();
        oldItemColor = new Color(display, 0x80, 0x80, 0x80); 
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.IColorProvider#getForeground(java.lang.Object)
     */
    public Color getForeground(Object element) {
        IRSSElement rssElement = (IRSSElement)
            ((IAdaptable)element).getAdapter(IRSSElement.class);
        if(rssElement instanceof IItem) {
            if(!((IItem)rssElement).isUpdated())
                return oldItemColor;
        }
        else if(rssElement instanceof IChannel) {
            if(!((IChannel)rssElement).hasUpdates())
                return oldItemColor;
        }
        
        return null;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.IColorProvider#getBackground(java.lang.Object)
     */
    public Color getBackground(Object element) {
        return null;
    }

    /* (non-Javadoc)
     * Method declared on ILabelProvider
     */
    public final Image getImage(Object element) {
        //obtain the base image by querying the element
        IWorkbenchAdapter adapter = getAdapter(element);
        if(adapter == null)
            return null;

        ImageDescriptor descriptor = adapter.getImageDescriptor(element);
        if(descriptor == null)
            return null;

        //add any annotations to the image descriptor
        descriptor = decorateImage(descriptor, element);

        //obtain the cached image corresponding to the descriptor
        if(imageTable == null) {
            imageTable = new Hashtable(40);
        }
        
        Image image = (Image)imageTable.get(descriptor);
        if(image == null) {
            image = descriptor.createImage();
            imageTable.put(descriptor, image);
        }
        
        return image;
    }

    /* (non-Javadoc)
     * Method declared on ILabelProvider
     */
    public final String getText(Object element) {
        //query the element for its label
        IWorkbenchAdapter adapter = getAdapter(element);
        if(adapter == null)
            return "";
            
        String label = adapter.getLabel(element);
        return label == null ? "" : label;
    }

    private ImageDescriptor decorateImage(ImageDescriptor image, Object element) {
        IRSSElement rssElement = (IRSSElement)
            ((IAdaptable)element).getAdapter(IRSSElement.class);
        if(rssElement instanceof IItem) {
            if(((IItem)rssElement).isUpdated())
                return newItemIcon;
        }
        else if(rssElement instanceof IChannel) {
            if(((IChannel)rssElement).hasUpdates())
                return new NewChannelImageDescriptor(
                    image.getImageData(), 
                    newDecoratorIcon.getImageData());
        }
        
        return image;
    }

    private IWorkbenchAdapter getAdapter(Object o) {
        if(o instanceof IAdaptable)
            return (IWorkbenchAdapter)((IAdaptable) o).getAdapter(
                IWorkbenchAdapter.class);
        else
            return null;
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.IBaseLabelProvider#dispose()
     */
    public void dispose() {
        if(imageTable != null) {
            for(Iterator i = imageTable.values().iterator(); i.hasNext();) {
                ((Image)i.next()).dispose();
            }
            
            imageTable = null;
        }

        oldItemColor.dispose();
        super.dispose();
    }
}
