/*
 * Created on Dec 4, 2003
 * Version $Id$
 */
package com.pnehrer.rss.core;

import org.eclipse.core.runtime.IAdaptable;

/**
 * @author <a href="mailto:pnehrer@freeshell.org">Peter Nehrer</a>
 */
public interface IRSSElement extends IAdaptable {

    public IChannel getChannel();

    public String getTitle();
    
    public String getLink();
}
