/*
 * Created on Nov 12, 2003
 * Version $Id$
 */
package com.pnehrer.rss.ui.views;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.ui.IMemento;

import com.pnehrer.rss.core.IChannel;
import com.pnehrer.rss.core.IItem;

/**
 * @author <a href="mailto:pnehrer@freeshell.org">Peter Nehrer</a>
 */
public class ItemSorter extends ViewerSorter {

    public static final int NONE = 0;
    public static final int TITLE = 1;
    public static final int DESCRIPTION = 2;
    public static final int LINK = 3;
    public static final int DATE = 4;
    
    private static final String TAG_COLUMN = "column";
    private static final String TAG_REVERSE = "reverse";
    private static final String TAG_SORTER = "sorter";
    
    private final int sortBy;
    private int sign;
    private final ViewerSorter oldSorter;
    
    public ItemSorter(int sortBy, boolean reverse, ViewerSorter oldSorter) {
        this.sortBy = sortBy;
        sign = reverse ? -1 : 1;
        this.oldSorter = oldSorter;
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.ViewerSorter#compare(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
     */
    public int compare(Viewer viewer, Object e1, Object e2) {
        IItem item1 = (IItem)e1;
        IItem item2 = (IItem)e2;
        switch(sortBy) {
            case TITLE:
                if(item1.getTitle() == null) return sign;
                else if(item2.getTitle() == null) return -sign;
                else {
                    int result = item1.getTitle().compareTo(item2.getTitle());
                    if(result == 0 && oldSorter != null)
                        return oldSorter.compare(viewer, e1, e2);
                    else
                        return sign * result;
                }
                
            case DESCRIPTION:
                if(item1.getDescription() == null) return sign;
                else if(item2.getDescription() == null) return -sign;
                else {
                    int result = item1.getDescription().compareTo(
                        item2.getDescription());
                    if(result == 0 && oldSorter != null)
                        return oldSorter.compare(viewer, e1, e2);
                    else
                        return sign * result;
                }
                
            case LINK:
                if(item1.getLink() == null) return sign;
                else if(item2.getLink() == null) return -sign;
                else {
                    int result = item1.getLink().compareTo(item2.getLink());
                    if(result == 0 && oldSorter != null)
                        return oldSorter.compare(viewer, e1, e2);
                    else
                        return sign * result;
                }
                
            case DATE:
                if(item1.getDate() == null) return sign;
                else if(item2.getDate() == null) return -sign;
                else {
                    int result = item1.getDate().compareTo(
                        item2.getDate());
                    if(result == 0 && oldSorter != null)
                        return oldSorter.compare(viewer, e1, e2);
                    else
                        return sign * result;
                }
                
            default:
            	if (isReverse()) {
	            	IChannel channel = item1.getChannel();
	            	IItem[] items = channel.getItems();
	            	int i1 = -1, i2 = -1;
	            	for (int i = 0; i < items.length; ++i) {
	            		if (i1 < 0 && item1.equals(items[i]))
	            			i1 = i;
	            		else if (i2 < 0 && item2.equals(items[i]))
	            			i2 = i;
	            		
	            		if (i1 >= 0 && i2 >= 0)
	            			break;
	            	}
	            	
	                return i1 < i2 ? 1 : i1 == i2 ? 0 : -1;
            	}
            	else
            		return 0;
        }
    }
    
    public int getColumn() {
        return sortBy;
    }
    
    public boolean isReverse() {
        return sign == -1;
    }

    public void flipOrder() {
        sign *= -1;
    }
    
    public static ItemSorter restoreState(IMemento memento) {
        Integer sortBy = memento.getInteger(TAG_COLUMN);
        boolean reverse = 
            Boolean.valueOf(memento.getString(TAG_REVERSE)).booleanValue();
        IMemento childMem = memento.getChild(TAG_SORTER);
        ViewerSorter oldSorter = childMem == null ? 
            null : 
            restoreState(childMem);
            
        return new ItemSorter(
            sortBy == null ? 0 : sortBy.intValue(), 
            reverse, 
            oldSorter);
    }
    
    public void saveState(IMemento memento, int maxSorters) {
        memento.putInteger(TAG_COLUMN, sortBy);
        memento.putString(TAG_REVERSE, String.valueOf(sign == -1));        
        if(maxSorters > 0 && oldSorter instanceof ItemSorter) {
            IMemento childMem = memento.createChild(TAG_SORTER);
            ((ItemSorter)oldSorter).saveState(childMem, maxSorters - 1);
        }
    }
}
