/*
 * Created on Nov 13, 2003
 * Version $Id$
 */
package com.pnehrer.rss.internal.core;

import java.net.URL;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.QualifiedName;
import org.w3c.dom.Document;

import com.pnehrer.rss.core.ChannelChangeEvent;
import com.pnehrer.rss.core.IChannelChangeListener;
import com.pnehrer.rss.core.IRegisteredTranslator;
import com.pnehrer.rss.core.RSSCore;

/**
 * @author <a href="mailto:pnehrer@freeshell.org">Peter Nehrer</a>
 */
public class ChannelManager {
    
    private static final QualifiedName CHANNEL_KEY = 
        new QualifiedName(RSSCore.PLUGIN_ID, "channel");
    
    private static ChannelManager instance;
    private final Timer timer = new Timer();
    private volatile boolean timerCancelled;
    private final Collection listeners = 
        Collections.synchronizedCollection(new HashSet());
    
    public ChannelManager() {
        instance = this;
    }
    
    static ChannelManager getInstance() {
        return instance;
    }

    void scheduleTask(TimerTask task, Date lastUpdated, int updateInterval) {
        if(timerCancelled)
            return;
            
        Calendar cal = Calendar.getInstance();
        if(lastUpdated != null)
            cal.setTime(lastUpdated);

        cal.add(Calendar.MINUTE, updateInterval);
        timer.schedule(task, cal.getTime());
    }
    
    public synchronized void cancelPendingTasks() {
        timer.cancel();
        timerCancelled = true;
    }
    
    public synchronized Channel getChannel(IFile file) throws CoreException {
        Channel channel = (Channel)file.getSessionProperty(CHANNEL_KEY);
        if(channel == null && "rss".equals(file.getFileExtension())) {
            channel = Channel.load(file);
            file.setSessionProperty(CHANNEL_KEY, channel);
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
    
        Channel channel = Channel.create(
            file, 
            translator, 
            document, 
            url, 
            updateInterval,
            monitor);

        file.setSessionProperty(CHANNEL_KEY, channel);
        return channel;
    }
    
    synchronized void removeChannel(Channel channel) {
        try {
            channel.getFile().setSessionProperty(CHANNEL_KEY, null);
        }
        catch(CoreException ex) {
            RSSCore.getPlugin().getLog().log(ex.getStatus());
        }
    }

    public void addChannelChangeListener(IChannelChangeListener listener) {
        listeners.add(listener);
    }

    public void removeChannelChangeListener(IChannelChangeListener listener) {
        listeners.remove(listener);
    }
    
    void firePropertyChange(Channel channel, int flags) {
        ChannelChangeEvent event = new ChannelChangeEvent(channel, flags);
        synchronized(listeners) {
            for(Iterator i = listeners.iterator(); i.hasNext();) {
                IChannelChangeListener listener = 
                    (IChannelChangeListener)i.next();
                listener.channelChanged(event);
            }
        }
    }
}
