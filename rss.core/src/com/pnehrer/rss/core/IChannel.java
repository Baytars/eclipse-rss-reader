/*
 * Created on Nov 5, 2003
 * Version $Id$
 */
package com.pnehrer.rss.core;

import java.net.URL;
import java.util.Date;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;

/**
 * @author <a href="mailto:pnehrer@freeshell.org">Peter Nehrer</a>
 */
public interface IChannel {

    public IFile getFile();
    
    public URL getURL();
    
    public void setURL(URL url);
    
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
    
    public void update() throws CoreException;
    
    public void addChannelChangeListener(IChannelChangeListener listener);
    
    public void removeChannelChangeListener(IChannelChangeListener listener);
}
