/*
 * Created on Mar 4, 2004
 * Version $Id$
 */
package com.pnehrer.rss.ui.views;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.resource.ImageDescriptor;
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
            
    private Map images;
    private Map newImages;
    private Image itemIcon;
    private Image newItemIcon;
    private ImageDescriptor newDecoratorIcon;
    private Color oldItemColor;

    public ChannelNavigatorLabelProvider() {
        newDecoratorIcon = 
    		RSSUI
				.getDefault()
				.getImageRegistry()
				.getDescriptor(RSSUI.NEW_DECORATOR_ICON);
        
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
        IWorkbenchAdapter adapter = getAdapter(element);
        if(adapter == null)
            return null;

        ImageDescriptor descriptor = adapter.getImageDescriptor(element);
        if(descriptor == null)
            return null;

        IRSSElement rssElement = (IRSSElement)
        	((IAdaptable)element).getAdapter(IRSSElement.class);
	    if(rssElement instanceof IItem) {
	        if(((IItem)rssElement).isUpdated()) {
	        	if (newItemIcon == null) {
		            descriptor = 
		            	RSSUI
							.getDefault()
							.getImageRegistry()
							.getDescriptor(RSSUI.ITEM_NEW_ICON);
		            if (descriptor != null)
		            	newItemIcon = descriptor.createImage();
	        	}
	        	
	        	return newItemIcon;
	        }
	        else {
	        	if (itemIcon == null)
		            itemIcon = descriptor.createImage();
	        	
	        	return itemIcon;
	        }
	    }
	    else {
	    	if(rssElement instanceof IChannel && ((IChannel)rssElement).hasUpdates()) {
	    		if (newImages == null)
	    			newImages = new HashMap();

	        	Image image = (Image)newImages.get(rssElement);
	        	if (image == null) {
		        	descriptor = 
		        		new NewChannelImageDescriptor(
		        				descriptor.getImageData(), 
								newDecoratorIcon.getImageData());
		        	
		        	image = descriptor.createImage();
		        	newImages.put(rssElement, image);
	        	}

	        	return image;
	    	}
	    	else {
	    		if (images == null)
	    			images = new HashMap();
	        	
	        	Image image = (Image)images.get(element);
	        	if (image == null) {
		        	image = descriptor.createImage();
		        	images.put(element, image);
	        	}

	        	return image;
	        }
	    }
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
    	if (itemIcon != null) {
    		itemIcon.dispose();
    		itemIcon = null;
    	}
    	
    	if (newItemIcon != null) {
    		newItemIcon.dispose();
    		newItemIcon = null;
    	}
    	
        if(images != null) {
            for(Iterator i = images.values().iterator(); i.hasNext();)
                ((Image)i.next()).dispose();
            
            images = null;
        }

        if(newImages != null) {
            for(Iterator i = newImages.values().iterator(); i.hasNext();)
                ((Image)i.next()).dispose();
            
            newImages = null;
        }

        oldItemColor.dispose();
        super.dispose();
    }
}
