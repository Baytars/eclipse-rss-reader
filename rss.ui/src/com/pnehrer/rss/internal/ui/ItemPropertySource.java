/*
 * Created on Nov 24, 2003
 * Version $Id$
 */
package com.pnehrer.rss.internal.ui;

import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.PropertyDescriptor;

import com.pnehrer.rss.core.IItem;

/**
 * @author <a href="mailto:pnehrer@freeshell.org">Peter Nehrer</a>
 */
public class ItemPropertySource implements IPropertySource {
    
    private static final int TITLE = 1;
    private static final int LINK = 2;
    private static final int DATE = 3;
    
    private static final IPropertyDescriptor[] PROPERTY_DESCRIPTORS = {
        new PropertyDescriptor(new Integer(TITLE), "title"),
        new PropertyDescriptor(new Integer(LINK), "link"),
        new PropertyDescriptor(new Integer(DATE), "date")};

    private final IItem item;
    
    ItemPropertySource(IItem item) {
        this.item = item;
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.views.properties.IPropertySource#getEditableValue()
     */
    public Object getEditableValue() {
        return null;
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.views.properties.IPropertySource#getPropertyDescriptors()
     */
    public IPropertyDescriptor[] getPropertyDescriptors() {
        return PROPERTY_DESCRIPTORS;
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.views.properties.IPropertySource#getPropertyValue(java.lang.Object)
     */
    public Object getPropertyValue(Object id) {
        switch(((Integer)id).intValue()) {
            case TITLE:
                return item.getTitle();
                
            case LINK:
                return item.getLink();
                
            case DATE:
                return item.getDate();
                
            default:
                return null;
        }
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.views.properties.IPropertySource#isPropertySet(java.lang.Object)
     */
    public boolean isPropertySet(Object id) {
        return false;
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.views.properties.IPropertySource#resetPropertyValue(java.lang.Object)
     */
    public void resetPropertyValue(Object id) {
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.views.properties.IPropertySource#setPropertyValue(java.lang.Object, java.lang.Object)
     */
    public void setPropertyValue(Object id, Object value) {
    }
}
