/*
 * Created on Nov 24, 2003
 * Version $Id$
 */
package com.pnehrer.rss.internal.ui;

/**
 * @author <a href="mailto:pnehrer@freeshell.org">Peter Nehrer</a>
 */
public interface IPageContainer {

    public void setMessage(String message);
    
    public void setErrorMessage(String message);
    
    public void setComplete(boolean complete);
}
