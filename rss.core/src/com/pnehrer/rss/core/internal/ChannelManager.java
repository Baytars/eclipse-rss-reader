/*
 * Created on Nov 13, 2003
 * Version $Id$
 */
package com.pnehrer.rss.core.internal;

import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.w3c.dom.Document;

import com.pnehrer.rss.core.ChannelChangeEvent;
import com.pnehrer.rss.core.IChannelChangeListener;
import com.pnehrer.rss.core.IRegisteredTranslator;
import com.pnehrer.rss.core.RSSCore;

/**
 * @author <a href="mailto:pnehrer@freeshell.org">Peter Nehrer</a>
 */
public class ChannelManager {
    
    private static final long MILISEC_PER_MIN = 60000;

    private static ChannelManager instance;
    private final Map fileChannelMap =  new HashMap();
    private final Timer timer = new Timer();
    private final Collection listeners = 
        Collections.synchronizedCollection(new HashSet());
    
    public ChannelManager() {
        instance = this;
    }
    
    static ChannelManager getInstance() {
        return instance;
    }

    void scheduleTask(TimerTask task, int updateInterval) {
        timer.schedule(
            task, 
            MILISEC_PER_MIN * updateInterval, 
            MILISEC_PER_MIN * updateInterval);
    }
    
    public synchronized void cancelPendingTasks() {
        timer.cancel();
    }
    
    public synchronized Channel getChannel(IFile file) throws CoreException {
        Channel channel = (Channel)fileChannelMap.get(file);
        if(channel == null) {
            channel = Channel.load(file);
            fileChannelMap.put(file, channel);
        }
        
        return channel;
    }
    
    public synchronized Channel createChannel(
        IFile file, 
        IRegisteredTranslator translator,
        Document document,
        URL url,
        Integer updateInterval,
        IProgressMonitor monitor)
        throws CoreException {
    
        if(fileChannelMap.containsKey(file)) {
            throw new CoreException(
                new Status(
                    IStatus.ERROR,
                    RSSCore.PLUGIN_ID,
                    0,
                    "channel already exists",
                    null));
        }
        else {
            Channel channel = Channel.create(
                file, 
                translator, 
                document, 
                url, 
                updateInterval,
                monitor);

            fileChannelMap.put(file, channel);
            return channel;
        }        
    }

    public void addChannelChangeListener(IChannelChangeListener listener) {
        listeners.add(listener);
    }

    public void removeChannelChangeListener(IChannelChangeListener listener) {
        listeners.remove(listener);
    }
    
    void firePropertyChange(Channel channel) {
        ChannelChangeEvent event = new ChannelChangeEvent(channel);
        synchronized(listeners) {
            for(Iterator i = listeners.iterator(); i.hasNext();) {
                IChannelChangeListener listener = 
                    (IChannelChangeListener)i.next();
                listener.channelChanged(event);
            }
        }
    }
}
