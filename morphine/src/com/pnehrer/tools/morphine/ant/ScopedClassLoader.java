package com.pnehrer.tools.morphine.ant;

import java.io.*;
import java.net.*;
import java.util.*;

import org.apache.tools.ant.types.Path;

public class ScopedClassLoader extends URLClassLoader {

    private ClassLoader oldClassLoader;

    public ScopedClassLoader() {
        this(new URL[0]);
    }

    public ScopedClassLoader(URL[] urls) {
        super(urls);
    }

    public ScopedClassLoader(URL[] urls, ClassLoader parent) {
        super(urls, parent);
    }

    public ScopedClassLoader(URL[] urls, ClassLoader parent, URLStreamHandlerFactory factory) {
        super(urls, parent, factory);
    }

    public void addURL(URL url) {
        super.addURL(url);
    }

    public void addPath(String path) throws MalformedURLException {
        URL[] urls = pathToURLArray(path);
        for(int i = 0, n = urls.length; i < n; ++i) addURL(urls[i]);
    }

    public void addPath(Path path) throws MalformedURLException {
        String[] pathElements = path.list();
        for(int i = 0, n = pathElements.length; i < n; ++i) addPath(pathElements[i]);
    }

    public void setAsContextClassLoader() {
        if(oldClassLoader != null) throw new IllegalStateException();
        else {
            oldClassLoader = Thread.currentThread().getContextClassLoader();
            Thread.currentThread().setContextClassLoader(this);
        }
    }

    public void resetContextClassLoader() {
        Thread.currentThread().setContextClassLoader(oldClassLoader);
    }

    public static URL[] pathToURLArray(Path path) throws MalformedURLException {
        List pathList = new ArrayList();
        String[] pathElements = path.list();
        for(int i = 0, n = pathElements.length; i < n; ++i) {
            pathList.addAll(Arrays.asList(pathToURLArray(pathElements[i])));
        }

        URL[] result = new URL[pathList.size()];
        return (URL[])pathList.toArray(result);
    }

    public static URL[] pathToURLArray(String path) throws MalformedURLException {
        StringTokenizer st = new StringTokenizer(path, File.pathSeparator);
        URL[] result = new URL[st.countTokens()];
        int i = 0;
        while(st.hasMoreTokens()) {
            String pathElement = st.nextToken();
            String lcPathElement = pathElement.toLowerCase();

            if(!lcPathElement.startsWith("http:")
                && !lcPathElement.startsWith("ftp:")
                && !lcPathElement.startsWith("jar:")
                && !lcPathElement.startsWith("file:")) {

                pathElement = "file:" + pathElement;
            }

            if(!(lcPathElement.endsWith(".jar") || lcPathElement.endsWith(".zip")
                || lcPathElement.endsWith(".war"))
                && !lcPathElement.endsWith(File.separator)) {

                pathElement += File.separator;
            }

            result[i++] = new URL(pathElement);
        }

        return result;
    }

    public static ScopedClassLoader newInstance(String path, ClassLoader parent)
        throws MalformedURLException {

        return new ScopedClassLoader(ScopedClassLoader.pathToURLArray(path), parent);
    }

    public static ScopedClassLoader newInstance(Path path, ClassLoader parent)
        throws MalformedURLException {

        return new ScopedClassLoader(ScopedClassLoader.pathToURLArray(path), parent);
    }
}
