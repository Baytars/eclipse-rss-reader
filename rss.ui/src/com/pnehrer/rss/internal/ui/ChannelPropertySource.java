/*
 * Created on Nov 24, 2003
 * Version $Id$
 */
package com.pnehrer.rss.internal.ui;

import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

import com.pnehrer.rss.core.IChannel;
import com.pnehrer.rss.core.RSSCore;

/**
 * @author <a href="mailto:pnehrer@freeshell.org">Peter Nehrer</a>
 */
public class ChannelPropertySource implements IPropertySource {

    private static final int FILE = 1;
    private static final int URL = 2;
    private static final int UPDATE_INTERVAL = 3;
    private static final int LAST_UPDATED = 4;
    private static final int TITLE = 5;
    private static final int LINK = 6;
    private static final int DATE = 7;
    private static final int ITEM_COUNT = 8;
    
    private static final IPropertyDescriptor[] PROPERTY_DESCRIPTORS = {
        new PropertyDescriptor(new Integer(FILE), "file"),
        new PropertyDescriptor(new Integer(URL), "URL"),
        new TextPropertyDescriptor(new Integer(UPDATE_INTERVAL), "update interval"),
        new PropertyDescriptor(new Integer(LAST_UPDATED), "last updated"),
        new PropertyDescriptor(new Integer(TITLE), "title"),
        new PropertyDescriptor(new Integer(LINK), "link"),
        new PropertyDescriptor(new Integer(DATE), "date"),
        new PropertyDescriptor(new Integer(ITEM_COUNT), "items")};
        
    private final IChannel channel;
    
    ChannelPropertySource(IChannel channel) {
        this.channel = channel;
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
            case FILE:
                return channel.getFile().getName();
                
            case URL:
                return channel.getURL();

            case UPDATE_INTERVAL:
                return channel.getUpdateInterval();
                
            case LAST_UPDATED:
                return channel.getLastUpdated();
                
            case TITLE:
                return channel.getTitle();
                
            case LINK:
                return channel.getLink();
                
            case DATE:
                return channel.getDate();
                
            case ITEM_COUNT:
                return new Integer(channel.getItems().length);

            default: 
                return null;
        }
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.views.properties.IPropertySource#isPropertySet(java.lang.Object)
     */
    public boolean isPropertySet(Object id) {
        if(id.equals(new Integer(UPDATE_INTERVAL)))
            return channel.getUpdateInterval() != null;
        else 
            return false;
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.views.properties.IPropertySource#resetPropertyValue(java.lang.Object)
     */
    public void resetPropertyValue(Object id) {
        if(id.equals(new Integer(UPDATE_INTERVAL))) {
            channel.setUpdateInterval(new Integer(
                RSSCore.getPlugin().getPluginPreferences().getInt(
                    RSSCore.PREF_UPDATE_INTERVAL)));
        }
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.views.properties.IPropertySource#setPropertyValue(java.lang.Object, java.lang.Object)
     */
    public void setPropertyValue(Object id, Object value) {
        if(id.equals(new Integer(UPDATE_INTERVAL))) {
            try {
                channel.setUpdateInterval(
                    value == null ? 
                        null : 
                        new Integer(value.toString()));
            }
            catch(NumberFormatException ex) {
                // ignore
            }
        }
    }
}
