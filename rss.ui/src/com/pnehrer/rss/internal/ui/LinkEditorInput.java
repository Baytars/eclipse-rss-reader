/*
 * Created on Dec 17, 2003
 * Version $Id$
 */
package com.pnehrer.rss.internal.ui;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IStorage;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IPersistableElement;
import org.eclipse.ui.IStorageEditorInput;

import com.pnehrer.rss.core.IRSSElement;
import com.pnehrer.rss.ui.RSSUI;

/**
 * @author <a href="mailto:pnehrer@freeshell.org">Peter Nehrer</a>
 */
public class LinkEditorInput 
    extends PlatformObject 
    implements IStorageEditorInput, IPersistableElement {

    private final IRSSElement rssElement;

    public LinkEditorInput(IRSSElement rssElement) {
        this.rssElement = rssElement;
    }
    
    public IRSSElement getRSSElement() {
        return rssElement;
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.IStorageEditorInput#getStorage()
     */
    public IStorage getStorage() throws CoreException {
        URL url;
        try {
            url = new URL(rssElement.getLink());
        }
        catch(MalformedURLException e) {
            throw new CoreException(
                new Status(
                    IStatus.ERROR,
                    RSSUI.PLUGIN_ID,
                    0,
                    "could not create url from link " + rssElement.getLink(),
                    e));
        }
        
        return new LinkStorage(url);
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.IEditorInput#exists()
     */
    public boolean exists() {
        return rssElement.getLink() != null;
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.IEditorInput#getImageDescriptor()
     */
    public ImageDescriptor getImageDescriptor() {
        return RSSUI.getDefault().getImageDescriptor16x16(
            rssElement.getChannel());
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.IEditorInput#getName()
     */
    public String getName() {
        return rssElement.getTitle();
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.IEditorInput#getPersistable()
     */
    public IPersistableElement getPersistable() {
        return this;
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.IEditorInput#getToolTipText()
     */
    public String getToolTipText() {
        return rssElement.getLink();
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.IPersistableElement#getFactoryId()
     */
    public String getFactoryId() {
        return LinkEditorInputFactory.FACTORY_ID;
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.IPersistableElement#saveState(org.eclipse.ui.IMemento)
     */
    public void saveState(IMemento memento) {
        LinkEditorInputFactory.saveState(memento, this);
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
     */
    public Object getAdapter(Class adapter) {
        if(IRSSElement.class.equals(adapter))
            return rssElement;
        else if(IFile.class.equals(adapter))
            return rssElement.getChannel().getFile();
        else
            return super.getAdapter(adapter);
    }
}
