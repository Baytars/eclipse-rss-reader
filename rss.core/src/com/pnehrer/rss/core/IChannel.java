/*
 * Created on Nov 5, 2003
 * Version $Id$
 */
package com.pnehrer.rss.core;

import java.net.URL;
import java.util.Date;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * @author <a href="mailto:pnehrer@freeshell.org">Peter Nehrer</a>
 */
public interface IChannel extends IRSSElement {
	
	public static final String UPDATE_JOB_FAMILY = "com.pnehrer.rss.channel.update";
	
    public IFile getFile();
    
    public IRegisteredTranslator getTranslator();
    
    public void setTranslator(IRegisteredTranslator translator);
    
    public URL getURL();
    
    public void setURL(URL url);
    
    public Integer getUpdateInterval();
    
    public void setUpdateInterval(Integer updateInterval);
    
    public Date getLastUpdated();
    
    public String getDescription();    

    public Date getDate();
    
    public IImage getImage();
    
    public IItem[] getItems();
    
    public ITextInput getTextInput();
    
    public boolean hasUpdates();
    
    public void resetUpdateFlags();
    
    public void update(IProgressMonitor monitor) throws CoreException;
    
    public void save(IProgressMonitor monitor) throws CoreException;
}
