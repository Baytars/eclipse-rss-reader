/*
 * Created on Nov 5, 2003
 * Version $Id$
 */
package com.pnehrer.rss.core;

import java.net.URL;

/**
 * @author <a href="mailto:pnehrer@freeshell.org">Peter Nehrer</a>
 */
public interface IImage {
    
    public IChannel getChannel();

    public String getTitle();
    
    public URL getURL();
    
    public String getLink();
}
