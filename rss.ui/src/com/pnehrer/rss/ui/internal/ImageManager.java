/*
 * Created on Nov 30, 2003
 * Version $Id$
 */
package com.pnehrer.rss.ui.internal;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;

import com.pnehrer.rss.core.IChannel;
import com.pnehrer.rss.core.IImage;
import com.pnehrer.rss.ui.RSSUI;

/**
 * @author <a href="mailto:pnehrer@freeshell.org">Peter Nehrer</a>
 */
public class ImageManager {

    private final Map imageMap = new HashMap();
    private final Map image16Map = new HashMap();
    private final Map image16x16Map = new HashMap();

    public synchronized ImageDescriptor getImageDescriptor(IChannel channel) {
        IImage image = channel.getImage();
        if(image == null) return null; // TODO Return default channel image.
        else {
            ImageDescriptor result = (ImageDescriptor)imageMap.get(channel);
            if(result == null) {
                IPath cachePath = 
                    RSSUI.getDefault().getStateLocation().append(
                        channel
                            .getFile()
                            .getFullPath()
                            .removeFileExtension());

                File cache = cachePath.addFileExtension("gif").toFile();
                if(cache.exists()) {
                    result = ImageDescriptor.createFromFile(
                        null, 
                        cache.toString());
                }
                else {
                    createImageDescriptors(image, cachePath);
                    return (ImageDescriptor)imageMap.get(channel);
                }
            }
            
            return result;
        }
    }
    
    public synchronized ImageDescriptor getImageDescriptor16(IChannel channel) {
        IImage image = channel.getImage();
        if(image == null) return null; // TODO Return default channel image.
        else {
            ImageDescriptor result = 
                (ImageDescriptor)image16Map.get(channel);

            if(result == null) {
                IPath cachePath = 
                    RSSUI.getDefault().getStateLocation().append(
                        channel
                            .getFile()
                            .getFullPath()
                            .removeFileExtension());

                File cache = cachePath
                    .removeLastSegments(1)
                    .append(cachePath.lastSegment() + "_16")
                    .addFileExtension("gif")
                    .toFile();

                if(cache.exists()) {
                    result = ImageDescriptor.createFromFile(
                        null, 
                        cache.toString());
                }
                else {
                    createImageDescriptors(image, cachePath);
                    return (ImageDescriptor)image16Map.get(channel);
                }
            }
            
            return result;
        }
    }
    
    public synchronized ImageDescriptor getImageDescriptor16x16(IChannel channel) {
        IImage image = channel.getImage();
        if(image == null) return null; // TODO Return default channel image.
        else {
            ImageDescriptor result = 
                (ImageDescriptor)image16x16Map.get(channel);

            if(result == null) {
                IPath cachePath = 
                    RSSUI.getDefault().getStateLocation().append(
                        channel
                            .getFile()
                            .getFullPath()
                            .removeFileExtension());

                File cache = cachePath
                    .removeLastSegments(1)
                    .append(cachePath.lastSegment() + "_16x16")
                    .addFileExtension("gif")
                    .toFile();

                if(cache.exists()) {
                    result = ImageDescriptor.createFromFile(
                        null, 
                        cache.toString());
                }
                else {
                    createImageDescriptors(image, cachePath);
                    return (ImageDescriptor)image16x16Map.get(channel);
                }
            }
            
            return result;
        }
    }
    
    private void createImageDescriptors(IImage image, IPath cachePath) {
        ImageDescriptor imageDescriptor = 
            ImageDescriptor.createFromURL(image.getURL());

        imageMap.put(image.getChannel(), imageDescriptor);

        ImageData imageData = imageDescriptor.getImageData();
        ImageLoader loader = new ImageLoader();
        loader.data = new ImageData[] { imageData };
        File cache = cachePath.addFileExtension("gif").toFile();
        if(!cache.getParentFile().exists())
            cache.getParentFile().mkdirs();
                            
        loader.save(cache.toString(), SWT.IMAGE_GIF);

        float w = imageData.width;
        float h = imageData.height;
        float r = h / 16;
        int newWidth = (int)(w / r);
        imageData = imageData.scaledTo(newWidth, 16);
        loader.data = new ImageData[] { imageData };
        IPath cache16Path = cachePath
            .removeLastSegments(1)
            .append(cachePath.lastSegment() + "_16")
            .addFileExtension("gif");

        loader.save(cache16Path.toString(), SWT.IMAGE_GIF);
        image16Map.put(
            image.getChannel(), 
            ImageDescriptor.createFromFile(null, cache16Path.toString()));
            
        ImageData newImageData = new ImageData(
            newWidth > 16 ? 16 : newWidth,
            16,
            imageData.depth,
            imageData.palette);
            
        newImageData.transparentPixel = imageData.transparentPixel;
        newImageData.alpha = imageData.alpha;
        if(imageData.alphaData != null) {
            for(int i = 0; i < 16; ++i) {
                byte[] line = new byte[newImageData.width];
                imageData.getAlphas(0, i, newImageData.width, line, 0);
                newImageData.setAlphas(0, i, newImageData.width, line, 0);
            }
        }

        for(int i = 0; i < 16; ++i) {
            int[] line = new int[newImageData.width];
            imageData.getPixels(0, i, newImageData.width, line, 0);
            newImageData.setPixels(0, i, newImageData.width, line, 0);
        }
        
        loader.data = new ImageData[] { newImageData };
        IPath cache16x16Path = cachePath
            .removeLastSegments(1)
            .append(cachePath.lastSegment() + "_16x16")
            .addFileExtension("gif");

        loader.save(cache16x16Path.toString(), SWT.IMAGE_GIF);
        image16x16Map.put(
            image.getChannel(), 
            ImageDescriptor.createFromFile(null, cache16x16Path.toString()));
    }
}
