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

    /**
     * @param source
     */
    public ChannelChangeEvent(IChannel channel) {
        super(channel);
    }

    public IChannel getChannel() {
        return (IChannel)source;
    }
}
