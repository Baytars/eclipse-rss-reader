/*
 * Created on Nov 5, 2003
 * Version $Id$
 */
package com.pnehrer.rss.core.internal;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * @author <a href="mailto:pnehrer@freeshell.org">Peter Nehrer</a>
 */
public class ChannelBuilder extends DefaultHandler {
    
    private static final String CHANNEL = "channel";
    private static final String IMAGE = "image";
    private static final String ITEM = "item";
    private static final String TEXT_INPUT = "textInput";
    
    private static final String TITLE = "title";
    private static final String DESCRIPTION = "description";
    private static final String LINK = "link";
    private static final String DATE = "date";
    private static final String URL = "url";
    private static final String NAME = "name";

    private Channel channel;
    private final Collection items = new ArrayList();
    
    public ChannelBuilder() {
    }
    
    public ChannelBuilder(Channel channel) {
        this.channel = channel;
    }
    
    /* (non-Javadoc)
     * @see org.xml.sax.ContentHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
     */
    public void startElement(
        String uri,
        String localName,
        String qName,
        Attributes attributes)
        throws SAXException {

        if("".equals(uri) && CHANNEL.equals(localName)) {
            if(channel == null) 
                channel = new Channel(attributes.getValue("", URL));
                
            channel.setTitle(attributes.getValue("", TITLE));
            channel.setLink(attributes.getValue("", LINK));
            channel.setDescription(attributes.getValue("", DESCRIPTION));
            String date = attributes.getValue("", DATE);
            channel.setDate(date == null ? null : parseDate(date));
        }
        else if("".equals(uri) && IMAGE.equals(localName)) {
            Image image = new Image(
                channel,
                attributes.getValue("", TITLE),
                attributes.getValue("", URL),
                attributes.getValue("", LINK));
            channel.setImage(image);
        }
        else if("".equals(uri) && ITEM.equals(localName)) {
            String date = attributes.getValue("", DATE);
            Item item = new Item(
                channel,
                attributes.getValue("", TITLE),
                attributes.getValue("", DESCRIPTION),
                attributes.getValue("", LINK),
                date == null ? null : parseDate(date));
            items.add(item);
        }
        else if("".equals(uri) && TEXT_INPUT.equals(localName)) {
            TextInput textInput = new TextInput(
                channel,
                attributes.getValue("", TITLE),
                attributes.getValue("", DESCRIPTION),
                attributes.getValue("", NAME),
                attributes.getValue("", LINK));
            channel.setTextInput(textInput);
        }
    }
    
    /* (non-Javadoc)
     * @see org.xml.sax.ContentHandler#endElement(java.lang.String, java.lang.String, java.lang.String)
     */
    public void endElement(String uri, String localName, String qName)
        throws SAXException {

        if("".equals(uri) && CHANNEL.equals(localName)) {
            channel.setItems((Item[])items.toArray(new Item[items.size()]));
        }
    }

    public Channel getResult() {
        return channel;
    }

    private Date parseDate(String str) {
        try {
            return DateFormat.getInstance().parse(str);
        }
        catch(ParseException ex) {
            return null;
        }
    }
}
