/*
 * Created on Dec 4, 2003
 * Version $Id$
 */
package com.pnehrer.rss.core;

/**
 * @author <a href="mailto:pnehrer@freeshell.org">Peter Nehrer</a>
 */
public interface IRSSElement {

    public IChannel getChannel();

    public String getTitle();
    
    public String getLink();
}
