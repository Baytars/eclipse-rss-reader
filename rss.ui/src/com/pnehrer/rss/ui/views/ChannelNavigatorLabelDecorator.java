/*
 * Created on Mar 4, 2004
 * Version $Id$
 */
package com.pnehrer.rss.ui.views;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

import com.pnehrer.rss.core.IChannel;
import com.pnehrer.rss.core.IItem;
import com.pnehrer.rss.core.IRSSElement;
import com.pnehrer.rss.internal.ui.NewChannelImageDescriptor;
import com.pnehrer.rss.ui.RSSUI;

/**
 * @author <a href="mailto:pnehrer@freeshell.org">Peter Nehrer</a>
 */
public class ChannelNavigatorLabelDecorator
    extends LabelProvider
    implements ILabelDecorator, 
        IColorProvider {
            
    private Image newItemIcon;
    private Image newDecoratorIcon;
    private Color oldItemColor;

    public ChannelNavigatorLabelDecorator() {
        ImageRegistry reg = RSSUI.getDefault().getImageRegistry(); 
        newItemIcon = reg.get(RSSUI.ITEM_NEW_ICON);
        newDecoratorIcon = reg.get(RSSUI.NEW_DECORATOR_ICON);
        
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
     * @see org.eclipse.jface.viewers.ILabelDecorator#decorateImage(org.eclipse.swt.graphics.Image, java.lang.Object)
     */
    public Image decorateImage(Image image, Object element) {
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
                    newDecoratorIcon.getImageData()).createImage();
        }
        
        return image;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.ILabelDecorator#decorateText(java.lang.String, java.lang.Object)
     */
    public String decorateText(String text, Object element) {
        IRSSElement rssElement = (IRSSElement)
            ((IAdaptable)element).getAdapter(IRSSElement.class);
        if(rssElement instanceof IItem) {
            if(((IItem)rssElement).isUpdated())
                return text + "*";
        }
        else if(rssElement instanceof IChannel) {
            if(((IChannel)rssElement).hasUpdates())
                return text + "*";
        }
        
        return text;
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.IBaseLabelProvider#dispose()
     */
    public void dispose() {
        oldItemColor.dispose();
        super.dispose();
    }
}
