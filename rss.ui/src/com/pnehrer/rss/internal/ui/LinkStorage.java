/*
 * Created on Dec 17, 2003
 * Version $Id$
 */
package com.pnehrer.rss.internal.ui;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.eclipse.core.resources.IStorage;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.core.runtime.Status;

import com.pnehrer.rss.ui.RSSUI;

/**
 * @author <a href="mailto:pnehrer@freeshell.org">Peter Nehrer</a>
 */
public class LinkStorage extends PlatformObject implements IStorage {

    private final URL url;

    public LinkStorage(URL url) {
        this.url = url;
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IStorage#getContents()
     */
    public InputStream getContents() throws CoreException {
        try {
            return url.openStream();
        }
        catch(IOException e) {
            throw new CoreException(
                new Status(
                    IStatus.ERROR,
                    RSSUI.PLUGIN_ID,
                    0,
                    "could not open URL",
                    e));
        }
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IStorage#getFullPath()
     */
    public IPath getFullPath() {
        return null;
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IStorage#getName()
     */
    public String getName() {
        return url.toExternalForm();
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IStorage#isReadOnly()
     */
    public boolean isReadOnly() {
        return true;
    }
}
