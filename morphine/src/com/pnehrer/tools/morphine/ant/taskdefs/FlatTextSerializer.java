package com.pnehrer.tools.morphine.ant.taskdefs;

import java.io.*;
import java.util.*;

import org.apache.xalan.serialize.*;

import org.xml.sax.*;

public class FlatTextSerializer implements Serializer {

    private OutputStream outputStream;
    private WriterToUTF8Buffered utfWriter;
    private Writer customWriter;
    private Writer writer;
    private Properties format;
    private Stack hasCharacters;

    public FlatTextSerializer() {
        hasCharacters = new Stack();
    }

    public void setOutputStream(OutputStream parm1) {
        outputStream = parm1;

        try {
            writer = utfWriter = new WriterToUTF8Buffered(parm1);
            customWriter = null;
        }
        catch(UnsupportedEncodingException ex) {
            writer = customWriter = new OutputStreamWriter(parm1);
            utfWriter = null;
        }
    }

    public OutputStream getOutputStream() {
        return outputStream;
    }

    public void setWriter(Writer parm1) {
        writer = customWriter = parm1;
        utfWriter = null;
    }

    public Writer getWriter() {
        return customWriter;
    }

    public void setOutputFormat(Properties parm1) {
        format = parm1;
    }

    public Properties getOutputFormat() {
        return format;
    }

    public ContentHandler asContentHandler() throws IOException {
        return new ContentHandlerImpl();
    }

    public DOMSerializer asDOMSerializer() throws IOException {
        return null;
    }

    public boolean reset() {
        outputStream = null;
        utfWriter = null;
        customWriter = null;
        writer = null;
        format = null;
        hasCharacters.clear();

        return true;
    }

    private class ContentHandlerImpl implements ContentHandler {
        public void characters(char[] parm1, int parm2, int parm3) throws SAXException {
            if(writer != null) {
                try {
                    writer.write(parm1, parm2, parm3);
                    if(!Boolean.TRUE.equals(hasCharacters.peek())) {
                        hasCharacters.pop();
                        hasCharacters.push(Boolean.TRUE);
                    }
                }
                catch(IOException ex) {
                    throw new SAXException(ex);
                }
            }
        }

        public void endDocument() throws SAXException {
            if(writer != null) {
                try {
                    writer.close();
                }
                catch(IOException ex) {
                    throw new SAXException(ex);
                }
            }
        }

        public void endElement(String parm1, String parm2, String parm3) throws SAXException {
            if(writer != null) {
                try {
                    if(Boolean.TRUE.equals(hasCharacters.pop())) writer.write('\n');
                }
                catch(IOException ex) {
                    throw new SAXException(ex);
                }
            }
        }

        public void endPrefixMapping(String parm1) throws SAXException {
        }

        public void ignorableWhitespace(char[] parm1, int parm2, int parm3) throws SAXException {
        }

        public void processingInstruction(String parm1, String parm2) throws SAXException {
        }

        public void setDocumentLocator(Locator parm1) {
        }

        public void skippedEntity(String parm1) throws SAXException {
        }

        public void startDocument() throws SAXException {
        }

        public void startElement(String uri, String localName, String qName, Attributes attrs)
            throws SAXException {

            if(writer != null) {
                try {
                    boolean writeNS = localName != null && localName.length() > 0;

                    if(writeNS && uri != null && uri.length() > 0) {
                        writer.write(uri);
                        writer.write(':');
                    }

                    if(writeNS) writer.write(localName);
                    else writer.write(qName);

                    for(int i = 0, n = attrs.getLength(); i < n; ++i) {
                        writer.write(' ');
                        String attrUri = attrs.getURI(i);
                        if(writeNS && attrUri != null && attrUri.length() > 0
                             && !attrUri.equals(uri)) {

                            writer.write(attrUri);
                            writer.write(':');
                        }

                        if(writeNS) writer.write(attrs.getLocalName(i));
                        else writer.write(attrs.getQName(i));

                        writer.write("=\"");
                        writer.write(attrs.getValue(i));
                        writer.write('"');
                    }

                    writer.write('\n');
                    writer.flush();

                    hasCharacters.push(Boolean.FALSE);
                }
                catch(IOException ex) {
                    throw new SAXException(ex);
                }
            }
        }

        public void startPrefixMapping(String parm1, String parm2) throws SAXException {
        }
    }
}
