/*
 * Created on Nov 30, 2003
 * Version $Id$
 */
package com.pnehrer.rss.ui.internal;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.core.runtime.Status;
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
                File cache = getCacheFile(channel.getFile(), "image");
                if(cache != null && cache.exists()) {
                    result = ImageDescriptor.createFromFile(
                        null, 
                        cache.toString());
                }
                else {
                    createImageDescriptors(image);
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
                File cache = getCacheFile(channel.getFile(), "image16");
                if(cache != null && cache.exists()) {
                    result = ImageDescriptor.createFromFile(
                        null, 
                        cache.toString());
                }
                else {
                    createImageDescriptors(image);
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
                File cache = getCacheFile(channel.getFile(), "image16x16");
                if(cache != null && cache.exists()) {
                    result = ImageDescriptor.createFromFile(
                        null, 
                        cache.toString());
                }
                else {
                    createImageDescriptors(image);
                    return (ImageDescriptor)image16x16Map.get(channel);
                }
            }
            
            return result;
        }
    }
    
    private void createImageDescriptors(IImage image) {
        ImageDescriptor imageDescriptor = 
            ImageDescriptor.createFromURL(image.getURL());

        imageMap.put(image.getChannel(), imageDescriptor);

        ImageData imageData = imageDescriptor.getImageData();
        ImageLoader loader = new ImageLoader();
        loader.data = new ImageData[] { imageData };
        IFile file = image.getChannel().getFile();
        IPath cacheDir = RSSUI
            .getDefault()
            .getStateLocation();

        String ext;
        switch(imageData.type) {
            case SWT.IMAGE_BMP:
            case SWT.IMAGE_BMP_RLE:
                ext = "bmp";
                break;

            case SWT.IMAGE_ICO:
                ext = "ico";
                break;

            case SWT.IMAGE_PNG:
                // !@#$, png is not implemented!

            case SWT.IMAGE_JPEG:
                ext = "jpg";
                break;

            default:
                ext = "gif";
        }

        int outputType;
        switch(imageData.type) {
            case SWT.IMAGE_PNG:
                outputType = SWT.IMAGE_JPEG;
                break;
                
            case SWT.IMAGE_UNDEFINED:
                outputType = SWT.IMAGE_GIF;
                break;
                
            default:
                outputType = imageData.type;        
        }

        IPath basePath = file.getFullPath().removeFileExtension();
        IPath imagePath = basePath.addFileExtension(ext);

        File parentDir = new File(
            cacheDir.toFile(), 
            imagePath.removeLastSegments(1).toString());

        if(!parentDir.exists())
            parentDir.mkdirs();
                            
        loader.save(cacheDir.append(imagePath).toString(), outputType);
        try {
            file.setPersistentProperty(
                new QualifiedName(RSSUI.PLUGIN_ID, "image"),
                imagePath.toString());
        }
        catch(CoreException ex) {
            RSSUI.getDefault().getLog().log(
                new Status(
                    IStatus.ERROR,
                    RSSUI.PLUGIN_ID,
                    0,
                    "could not set persistent property on file " + file,
                    ex));
        }

        float w = imageData.width;
        float h = imageData.height;
        float r = h / 16;
        int newWidth = (int)(w / r);
        imageData = imageData.scaledTo(newWidth, 16);
        loader.data = new ImageData[] { imageData };
        imagePath = basePath
            .removeLastSegments(1)
            .append(basePath.lastSegment() + "_16")
            .addFileExtension(ext);

        String fullPath = cacheDir.append(imagePath).toString();
        loader.save(fullPath, outputType);
        try {
            file.setPersistentProperty(
                new QualifiedName(RSSUI.PLUGIN_ID, "image16"),
                imagePath.toString());
        }
        catch(CoreException ex) {
            RSSUI.getDefault().getLog().log(
                new Status(
                    IStatus.ERROR,
                    RSSUI.PLUGIN_ID,
                    0,
                    "could not set persistent property on file " + file,
                    ex));
        }
        
        image16Map.put(
            image.getChannel(), 
            ImageDescriptor.createFromFile(null, fullPath));
            
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
        imagePath = basePath
            .removeLastSegments(1)
            .append(basePath.lastSegment() + "_16x16")
            .addFileExtension(ext);

        fullPath = cacheDir.append(imagePath).toString();
        loader.save(fullPath, outputType);
        try {
            file.setPersistentProperty(
                new QualifiedName(RSSUI.PLUGIN_ID, "image16x16"),
                imagePath.toString());
        }
        catch(CoreException ex) {
            RSSUI.getDefault().getLog().log(
                new Status(
                    IStatus.ERROR,
                    RSSUI.PLUGIN_ID,
                    0,
                    "could not set persistent property on file " + file,
                    ex));
        }

        image16x16Map.put(
            image.getChannel(), 
            ImageDescriptor.createFromFile(null, fullPath));
    }
    
    private File getCacheFile(IFile file, String type) {
        String cachePathStr;
        try {
            cachePathStr =
                file.getPersistentProperty(
                    new QualifiedName(RSSUI.PLUGIN_ID, type));
        }
        catch(CoreException ex) {
            cachePathStr = null;
        }

        if(cachePathStr == null || cachePathStr.trim().length() == 0)
            return null;
        else {
            IPath cachePath = 
                RSSUI.getDefault().getStateLocation().append(cachePathStr);
    
            return cachePath.toFile();
        }
    }
}
