/*
 * Created on Nov 5, 2003
 * Version $Id$
 */
package com.pnehrer.rss.ui.views;

import java.util.Date;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import com.pnehrer.rss.core.IChannel;
import com.pnehrer.rss.core.IItem;

/**
 * @author <a href="mailto:pnehrer@freeshell.org">Peter Nehrer</a>
 */
public class ChannelDetailLabelProvider
    extends LabelProvider
    implements ITableLabelProvider {
        
    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang.Object, int)
     */
    public Image getColumnImage(Object element, int columnIndex) {
        return null;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object, int)
     */
    public String getColumnText(Object element, int columnIndex) {
        IItem item = (IItem)element;
        switch(columnIndex) {
            case 0: 
                IChannel channel = item.getChannel();
                IItem[] items = channel.getItems();
                for(int i = 0, n = items.length; i < n; ++i) {
                    if(item == items[i])
                        return String.valueOf(i + 1);
                }

                return "";
                
            case 1: return item.getTitle();
            case 2:
                String description = item.getDescription();
                return description == null ? "" : description;
                
            case 3: return item.getLink();
            case 4: 
                Date date = item.getDate();
                return date == null ? "" : date.toString();
                
            default: return "";
        }
    }
}
