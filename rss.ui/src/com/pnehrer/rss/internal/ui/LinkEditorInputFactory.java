/*
 * Created on Dec 17, 2003
 * Version $Id$
 */
package com.pnehrer.rss.internal.ui;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.Path;
import org.eclipse.ui.IElementFactory;
import org.eclipse.ui.IMemento;

import com.pnehrer.rss.core.IChannel;
import com.pnehrer.rss.core.IItem;
import com.pnehrer.rss.core.IRSSElement;
import com.pnehrer.rss.ui.RSSUI;

/**
 * @author <a href="mailto:pnehrer@freeshell.org">Peter Nehrer</a>
 */
public class LinkEditorInputFactory implements IElementFactory {

    public static final String FACTORY_ID = 
        RSSUI.PLUGIN_ID + ".LinkEditorInputFactory";

    private static final String TAG_PATH = "path";
    private static final String TAG_LINK = "link";
    private static final String TAG_CHANNEL = "isChannel";

    /* (non-Javadoc)
     * @see org.eclipse.ui.IElementFactory#createElement(org.eclipse.ui.IMemento)
     */
    public IAdaptable createElement(IMemento memento) {
        String path = memento.getString(TAG_PATH);
        if(path == null)
            return null;

        // Create an IResource.
        IResource res = ResourcesPlugin.getWorkspace().getRoot().findMember(new Path(path));
        if(res instanceof IFile) {
            IRSSElement rssElement = (IRSSElement)
                res.getAdapter(IRSSElement.class);
            if(rssElement == null)
                return null;
            else {
                boolean isChannel = 
                    new Boolean(memento.getString(TAG_CHANNEL)).booleanValue();
                if(isChannel)
                    return new LinkEditorInput(rssElement);
                else {
                    String link = memento.getString(TAG_LINK);
                    if(link == null)
                        return new LinkEditorInput(rssElement);

                    IItem[] items = rssElement.getChannel().getItems();
                    for(int i = 0, n = items.length; i < n; ++i) {
                        if(link.equals(items[i].getLink()))
                            return new LinkEditorInput(items[i]);
                    }
                    
                    return new LinkEditorInput(rssElement);
                }
            }
        }
        else
            return null;
    }

    public static void saveState(IMemento memento, LinkEditorInput input) {
        IRSSElement rssElement = input.getRSSElement();
        memento.putString(
            TAG_PATH, 
            rssElement.getChannel().getFile().getFullPath().toString());

        memento.putString(
            TAG_LINK, 
            rssElement.getLink());

        memento.putString(
            TAG_CHANNEL, 
            String.valueOf(rssElement instanceof IChannel));
    }
}
