package com.pnehrer.tools.morphine;

import java.io.*;
import org.xml.sax.*;

/**
 * <bindlets srcdir="..." destdir="...">
 *   <bindlet src="..." dest="..."/>
 * </bindlets>
 *
 * #@# #$#
 */

public abstract class BindletTool implements ContentHandler {

    public static final String URI = "";
    public static final String ELEM_BINDLETS = "bindlets";
    public static final String ATTR_BINDLETS_SRCDIR = "srcdir";
    public static final String ATTR_BINDLETS_DESTDIR = "destdir";
    public static final String ELEM_BINDLET = "bindlet";
    public static final String ATTR_BINDLET_SRC = "src";
    public static final String ATTR_BINDLET_DEST = "dest";

    protected String srcDir;
    protected String destDir;

    protected BindletTool() {
    }

    public void setDocumentLocator(Locator locator) {
    }

    public void startDocument() throws SAXException {
    }

    public void endDocument() throws SAXException {
    }

    public void startPrefixMapping(String prefix, String uri) throws SAXException {
    }

    public void endPrefixMapping(String prefix) throws SAXException {
    }

    public void startElement(String uri, String localName, String qName,
        Attributes atts) throws SAXException {

        if(URI.equals(uri) && ELEM_BINDLET.equals(localName)) {
            onBindlet(atts.getValue("", ATTR_BINDLET_SRC), atts.getValue("", ATTR_BINDLET_DEST));
        }
        else if(URI.equals(uri) && ELEM_BINDLETS.equals(localName)) {
            onBindlets(atts.getValue("", ATTR_BINDLETS_SRCDIR), atts.getValue("", ATTR_BINDLETS_DESTDIR));
        }
    }

    public void endElement(String namespaceURI, String localName, String qName)
        throws SAXException {
    }

    public void characters(char[] ch, int start, int length) throws SAXException {
    }

    public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
    }

    public void processingInstruction(String target, String data) throws SAXException {
    }

    public void skippedEntity(String name) throws SAXException {
    }

    protected void onBindlets(String srcDir, String destDir) throws SAXException {
        this.srcDir = srcDir;
        this.destDir = destDir;
    }

    protected void onBindlet(String src, String dest) throws SAXException {
    }
}
