/*
 * Created on Dec 17, 2003
 * Version $Id$
 */
package com.pnehrer.rss.ui;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.IWorkbenchPage;

import com.pnehrer.rss.core.IRSSElement;
import com.pnehrer.rss.core.ITextInput;

/**
 * @author <a href="mailto:pnehrer@freeshell.org">Peter Nehrer</a>
 */
public interface ILinkBrowser {

    public void open(IRSSElement rssElement, IWorkbenchPage page) 
        throws CoreException;

    public void open(ITextInput textInput, String data, IWorkbenchPage page) 
        throws CoreException;
}
