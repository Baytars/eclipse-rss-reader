/*
 * Created on Nov 5, 2003
 * Version $Id$
 */
package com.pnehrer.rss.core;

/**
 * @author <a href="mailto:pnehrer@freeshell.org">Peter Nehrer</a>
 */
public interface ITextInput {

    public IChannel getChannel();

    public String getTitle();
    
    public String getDescription();
    
    public String getName();
    
    public String getLink();
}
