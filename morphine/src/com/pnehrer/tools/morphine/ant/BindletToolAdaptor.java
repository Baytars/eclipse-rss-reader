package com.pnehrer.tools.morphine.ant;

import org.apache.tools.ant.*;
import org.xml.sax.*;

import com.pnehrer.tools.morphine.ant.taskdefs.*;
import com.pnehrer.tools.morphine.*;

/**
 * #@# #$#
 */
public class BindletToolAdaptor implements TransformationStep {

    protected BindletTool tool;
    protected ContentHandler nextHandler;
    protected Project project;

    public void setNext(ContentHandler next) {
        nextHandler = next;
    }

    public void setParameter(String name, Object value) {
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public void characters(char[] parm1, int parm2, int parm3) throws SAXException {
        if(nextHandler != null) nextHandler.characters(parm1, parm2, parm3);
        if(tool != null) tool.characters(parm1, parm2, parm3);
    }

    public void endDocument() throws SAXException {
        if(nextHandler != null) nextHandler.endDocument();
        if(tool != null) tool.endDocument();
    }

    public void endElement(String parm1, String parm2, String parm3) throws SAXException {
        if(nextHandler != null) nextHandler.endElement(parm1, parm2, parm3);
        if(tool != null) tool.endElement(parm1, parm2, parm3);
    }

    public void endPrefixMapping(String parm1) throws SAXException {
        if(nextHandler != null) nextHandler.endPrefixMapping(parm1);
        if(tool != null) tool.endPrefixMapping(parm1);
    }

    public void ignorableWhitespace(char[] parm1, int parm2, int parm3) throws SAXException {
        if(nextHandler != null) nextHandler.ignorableWhitespace(parm1, parm2, parm3);
        if(tool != null) tool.ignorableWhitespace(parm1, parm2, parm3);
    }

    public void processingInstruction(String parm1, String parm2) throws SAXException {
        if(nextHandler != null) nextHandler.processingInstruction(parm1, parm2);
        if(tool != null) tool.processingInstruction(parm1, parm2);
    }

    public void setDocumentLocator(Locator parm1) {
        if(nextHandler != null) nextHandler.setDocumentLocator(parm1);
        if(tool != null) tool.setDocumentLocator(parm1);
    }

    public void skippedEntity(String parm1) throws SAXException {
        if(nextHandler != null) nextHandler.skippedEntity(parm1);
        if(tool != null) tool.skippedEntity(parm1);
    }

    public void startDocument() throws SAXException {
        if(nextHandler != null) nextHandler.startDocument();
        if(tool != null) tool.startDocument();
    }

    public void startElement(String parm1, String parm2, String parm3, Attributes parm4)
        throws SAXException {

        if(nextHandler != null) nextHandler.startElement(parm1, parm2, parm3, parm4);
        if(tool != null) tool.startElement(parm1, parm2, parm3, parm4);
    }

    public void startPrefixMapping(String parm1, String parm2) throws SAXException {
        if(nextHandler != null) nextHandler.startPrefixMapping(parm1, parm2);
        if(tool != null) tool.startPrefixMapping(parm1, parm2);
    }
}
