package com.pnehrer.tools.morphine.ant.taskdefs;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;

import org.apache.tools.ant.*;
import org.apache.tools.ant.types.*;
import org.apache.xalan.serialize.*;
import org.apache.xalan.templates.*;

import org.xml.sax.*;
import org.xml.sax.helpers.*;

import com.pnehrer.tools.morphine.ant.*;

public class Transform extends Task {

    class DummyHandler implements ContentHandler {
        public void characters(char[] parm1, int parm2, int parm3) throws SAXException {
        }

        public void endDocument() throws SAXException {
        }

        public void endElement(String parm1, String parm2, String parm3) throws SAXException {
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

        public void startElement(String parm1, String parm2, String parm3, Attributes parm4)
            throws SAXException {
        }

        public void startPrefixMapping(String parm1, String parm2) throws SAXException {
        }
    }

    protected List steps;
    protected ReaderSpec readerSpec;
    protected SerializerSpec serializerSpec;
    protected String src;
    protected Path classpath;

    public Object createXsltStep() {
        XsltStepSpec step = new XsltStepSpec();
        step.setTask(this);
        steps.add(step);
        return step;
    }

    public Object createCustomStep() {
        CustomStepSpec step = new CustomStepSpec();
        step.setTask(this);
        steps.add(step);
        return step;
    }

    public Object createReader() {
        readerSpec = new ReaderSpec();
        readerSpec.setTask(this);
        return readerSpec;
    }

    public Object createSerializer() {
        serializerSpec = new SerializerSpec();
        serializerSpec.setTask(this);
        return serializerSpec;
    }

    public void setSrc(String value) {
        src = value;
    }

    public void setClasspath(Path classpath) {
        this.classpath = classpath;
    }

    public Object createClasspath() {
        return classpath = new Path(project);
    }

    public Path getClasspath() {
        return classpath;
    }

    public void init() throws BuildException {
        steps = new ArrayList();
    }

    public void execute() throws BuildException {
        ScopedClassLoader loader = null;
        if(classpath != null) {
            try {
                loader = ScopedClassLoader.newInstance(classpath,
                    Thread.currentThread().getContextClassLoader());
            }
            catch(MalformedURLException ex) {
                throw new BuildException(ex, location);
            }
        }

        if(loader != null) loader.setAsContextClassLoader();

        try {
            XMLReader reader = null;
            if(readerSpec == null) reader = XMLReaderFactory.createXMLReader();
            else reader = readerSpec.createXMLReader();

            ContentHandler nextContentHandler = null;

            if(serializerSpec == null) nextContentHandler = new DummyHandler();
            else {
                Serializer serializer = serializerSpec.createSerializer();
                nextContentHandler = serializer.asContentHandler();
            }

            for(ListIterator itor = steps.listIterator(steps.size()); itor.hasPrevious();) {
                TransformationStepSpec item = (TransformationStepSpec)itor.previous();
                nextContentHandler = item.chain(nextContentHandler);
            }

            reader.setContentHandler(nextContentHandler);
            reader.parse(src);
        }
        catch(BuildException ex) {
            throw ex;
        }
        catch(Exception ex) {
            throw new BuildException(ex, location);
        }
        finally {
            if(loader != null) loader.resetContextClassLoader();
        }
    }
}
