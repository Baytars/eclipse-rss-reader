/*
 * Created on Nov 18, 2003
 * Version $Id$
 */
package com.pnehrer.rss.core;

import org.eclipse.core.runtime.CoreException;
import org.w3c.dom.Document;

/**
 * @author <a href="mailto:pnehrer@freeshell.org">Peter Nehrer</a>
 */
public interface ITranslator {

    public boolean canTranslate(Document document);
    
    public Document translate(Document document) throws CoreException;
}
