/*
 * Created on Mar 2, 2004
 * Version $Id$
 */
package com.pnehrer.rss.internal.ui;

import org.eclipse.jface.resource.CompositeImageDescriptor;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;

/**
 * @author <a href="mailto:pnehrer@freeshell.org">Peter Nehrer</a>
 */

public class NewChannelImageDescriptor extends CompositeImageDescriptor {

    private final ImageData imageData;
    private final ImageData decorationData;

    public NewChannelImageDescriptor(
        ImageData imageData,
        ImageData decorationData) {
                
        this.imageData = imageData;
        this.decorationData = decorationData; 
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.resource.CompositeImageDescriptor#drawCompositeImage(int, int)
     */
    protected void drawCompositeImage(int width, int height) {
        drawImage(imageData, 0, 0);
        drawImage(decorationData, width - 16, 0);
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.resource.CompositeImageDescriptor#getSize()
     */
    protected Point getSize() {
        return new Point(imageData.width, imageData.height);
    }
}
