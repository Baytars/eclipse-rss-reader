/*
 * Created on Nov 5, 2003
 * Version $Id$
 */
package com.pnehrer.rss.core;

import java.beans.PropertyChangeListener;
import java.net.URL;
import java.util.Date;

import org.eclipse.core.resources.IFile;

/**
 * @author <a href="mailto:pnehrer@freeshell.org">Peter Nehrer</a>
 */
public interface IChannel {

    public URL getURL();
    
    public IFile getFile();
    
    public Integer getUpdateInterval();
    
    public void setUpdateInterval(Integer updateInterval);
    
    public Date getLastUpdated();

    public String getTitle();
    
    public String getLink();
    
    public String getDescription();
    
    public Date getDate();
    
    public IImage getImage();
    
    public IItem[] getItems();
    
    public ITextInput getTextInput();
    
    public void addPropertyChangeListener(PropertyChangeListener listener);
    
    public void removePropertyChangeListener(PropertyChangeListener listener);
}
