/*
 * Created on Nov 24, 2003
 * Version $Id$
 */
package com.pnehrer.rss.core;

import java.util.EventObject;

/**
 * @author <a href="mailto:pnehrer@freeshell.org">Peter Nehrer</a>
 */
public class ChannelChangeEvent extends EventObject {

    public static final int CHANGED = 1;
    public static final int ADDED = 2;
    public static final int REMOVED = 4;
    
    private final int flags;

    /**
     * @param source
     */
    public ChannelChangeEvent(IChannel channel, int flags) {
        super(channel);
        this.flags = flags;
    }

    public IChannel getChannel() {
        return (IChannel)source;
    }
    
    public int getFlags() {
        return flags;
    }
}
