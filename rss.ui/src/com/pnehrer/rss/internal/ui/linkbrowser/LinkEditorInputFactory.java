/*
 * Created on Dec 17, 2003
 * Version $Id$
 */
package com.pnehrer.rss.internal.ui.linkbrowser;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.Path;
import org.eclipse.ui.IElementFactory;
import org.eclipse.ui.IMemento;

import com.pnehrer.rss.core.IImage;
import com.pnehrer.rss.core.IItem;
import com.pnehrer.rss.core.IRSSElement;
import com.pnehrer.rss.core.ITextInput;
import com.pnehrer.rss.ui.RSSUI;

/**
 * @author <a href="mailto:pnehrer@freeshell.org">Peter Nehrer</a>
 */
public class LinkEditorInputFactory implements IElementFactory {

    public static final String FACTORY_ID = 
        RSSUI.PLUGIN_ID + ".LinkEditorInputFactory";

    private static final String TAG_PATH = "path";
    private static final String TAG_LINK = "link";
    private static final String TAG_KIND = "kind";
    private static final String TAG_URL = "url";

    /* (non-Javadoc)
     * @see org.eclipse.ui.IElementFactory#createElement(org.eclipse.ui.IMemento)
     */
    public IAdaptable createElement(IMemento memento) {
        String path = memento.getString(TAG_PATH);
        if(path == null)
            return null;

        // Create an IResource.
        IResource res = 
            ResourcesPlugin.getWorkspace().getRoot().findMember(new Path(path));
        if(res instanceof IFile) {
            IRSSElement rssElement = (IRSSElement)
                res.getAdapter(IRSSElement.class);
            if(rssElement != null) {
                String kind = memento.getString(TAG_KIND);
                try {
                    if("textinput".equals(kind))
                        return new LinkEditorInput(
                            rssElement.getChannel().getTextInput(),
							new URL(memento.getString(TAG_URL)));
                    else if("image".equals(kind))
                        return new LinkEditorInput(
                            rssElement.getChannel().getImage());
                    else if("item".equals(kind)) {
                        String link = memento.getString(TAG_LINK);
                        IItem[] items = rssElement.getChannel().getItems();
                        for(int i = 0, n = items.length; i < n; ++i) {
                            if(link.equals(items[i].getLink()))
                                return new LinkEditorInput(items[i]);
                        }
                    }
                    else
                        return new LinkEditorInput(rssElement);
                }
                catch(MalformedURLException ex) {
                    return null;
                }
            }
        }

        return null;
    }

    public static void saveState(IMemento memento, LinkEditorInput input) {
        IRSSElement rssElement = input.getRSSElement();
        memento.putString(
            TAG_PATH, 
            rssElement.getChannel().getFile().getFullPath().toString());

        if(rssElement instanceof IItem) {
            memento.putString(
                TAG_KIND, 
                "item");
            memento.putString(
                TAG_LINK, 
                rssElement.getLink());
        }
        else if(rssElement instanceof IImage) {
            memento.putString(
                TAG_KIND, 
                "image");
        }
        else if(rssElement instanceof ITextInput) {
            memento.putString(
                TAG_KIND, 
                "textinput");
            memento.putString(
            	TAG_URL,
				input.getUrl().toExternalForm());
        }
    }
}
