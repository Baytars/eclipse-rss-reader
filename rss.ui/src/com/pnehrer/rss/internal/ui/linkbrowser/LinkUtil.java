/*
 * Created on Dec 18, 2003
 * Version $Id$
 */
package com.pnehrer.rss.internal.ui.linkbrowser;

import java.net.URLEncoder;

/**
 * @author <a href="mailto:pnehrer@freeshell.org">Peter Nehrer</a>
 */
public class LinkUtil {
    
    private LinkUtil() {
    }

    public static String createURL(String link, String name, String value) {

        StringBuffer buf = new StringBuffer(link);
        int i = link.lastIndexOf('/');
        String lastSegment = i >= 0 ? link.substring(i) : link;
        if((i = lastSegment.lastIndexOf('?')) >= 0) {
            if(i < link.length() - 1)
                buf.append('&'); 
        }
        else 
            buf.append('?');
            
        buf.append(name);
        buf.append('=');
        buf.append(URLEncoder.encode(value));
        return buf.toString();
    }
}
