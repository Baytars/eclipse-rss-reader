/*
 * Created on Nov 5, 2003
 * Version $Id$
 */
package com.pnehrer.rss.core;

import java.util.Date;

/**
 * @author <a href="mailto:pnehrer@freeshell.org">Peter Nehrer</a>
 */
public interface IItem {
    
    public IChannel getChannel();

    public String getTitle();
    
    public String getDescription();

    public String getLink();

    public Date getDate();    
}
