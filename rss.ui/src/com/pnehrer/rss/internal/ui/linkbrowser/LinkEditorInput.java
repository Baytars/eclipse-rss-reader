/*
 * Created on Dec 17, 2003
 * Version $Id$
 */
package com.pnehrer.rss.internal.ui.linkbrowser;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IStorage;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.PlatformObject;
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
    private final URL url;
    private final LinkStorage storage;

    public LinkEditorInput(IRSSElement rssElement) 
        throws MalformedURLException {
        
        this(rssElement, new URL(rssElement.getLink()));
    }
    
    public LinkEditorInput(IRSSElement rssElement, URL url) {
        this.rssElement = rssElement;
        this.url = url;
        storage = new LinkStorage(url);
    }
    
    public IRSSElement getRSSElement() {
        return rssElement;
    }
    
    public URL getUrl() {
    	return url;
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.IStorageEditorInput#getStorage()
     */
    public IStorage getStorage() throws CoreException {
        return storage;
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.IEditorInput#exists()
     */
    public boolean exists() {
        return url != null;
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
        return url.toExternalForm();
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
