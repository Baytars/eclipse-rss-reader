/*
 * Created on Nov 5, 2003
 * Version $Id$
 */
package com.pnehrer.rss.core;

import java.util.Date;

/**
 * @author <a href="mailto:pnehrer@freeshell.org">Peter Nehrer</a>
 */
public interface IItem extends IRSSElement {
    
    public String getDescription();    

    public Date getDate();
    
    public boolean isUpdated();
    
    public void resetUpdateFlag();
}
