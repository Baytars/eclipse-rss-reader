/*
 * Created on Nov 13, 2003
 * Version $Id$
 */
package com.pnehrer.rss.core.internal;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;

/**
 * @author <a href="mailto:pnehrer@freeshell.org">Peter Nehrer</a>
 */
public class ChannelManager {

    private final Map fileChannelMap =  new HashMap();
    private final Map projectFileMap = new HashMap();
    
    public synchronized void add(Channel channel) {
        IFile file = channel.getFile();
        fileChannelMap.put(channel.getFile(), channel);        
        IProject project = file.getProject();
        Collection files = (Collection)projectFileMap.get(project);
        if(files == null) {
            files = new HashSet();
            projectFileMap.put(project, files);
        }
        
        files.add(file);
    }
    
    public synchronized void remove(IFile file) {
        fileChannelMap.remove(file);
        IProject project = file.getProject();
        Collection files = (Collection)projectFileMap.get(project);
        if(files != null) {
            files.remove(file);                
            if(files.isEmpty())
                projectFileMap.remove(project);
        }
    }
    
    public synchronized void remove(IProject project) {
        Collection files = (Collection)projectFileMap.remove(project);
        if(files != null)
            for(Iterator i = files.iterator(); i.hasNext();) {
                fileChannelMap.remove(i.next());
            }
    }
    
    public synchronized Channel get(IFile file) {
        return (Channel)fileChannelMap.get(file);
    }
}
