/*
 * Created on Nov 12, 2003
 * Version $Id$
 */
package com.pnehrer.rss.ui.views;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;

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
    
    private final int sortBy;
    
    public ItemSorter(int sortBy) {
        this.sortBy = sortBy;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.ViewerSorter#compare(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
     */
    public int compare(Viewer viewer, Object e1, Object e2) {
        IItem item1 = (IItem)e1;
        IItem item2 = (IItem)e2;
        switch(sortBy) {
            case TITLE:
                if(item1.getTitle() == null) return 1;
                else if(item2.getTitle() == null) return -1;
                else return item1.getTitle().compareTo(item2.getTitle());
                
            case DESCRIPTION:
                if(item1.getDescription() == null) return 1;
                else if(item2.getDescription() == null) return -1;
                else return item1.getDescription().compareTo(item2.getDescription());
                
            case LINK:
                if(item1.getLink() == null) return 1;
                else if(item2.getLink() == null) return -1;
                else return item1.getLink().compareTo(item2.getLink());
                
            case DATE:
                if(item1.getDate() == null) return 1;
                else if(item2.getDate() == null) return -1;
                else return item1.getDate().compareTo(item2.getDate());
                
            default:
                return 0;
        }
    }
}
