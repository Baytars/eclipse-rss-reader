package com.pnehrer.tools.morphine;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;

import org.xml.sax.*;
import org.xml.sax.helpers.*;

import com.pnehrer.tools.morphine.ant.*;

/**
 * #@# #$#
 */
public class TypeLibReader implements XMLReader {

    public static final Set FILE_EXTENSIONS = Collections.unmodifiableSet(
        new HashSet(Arrays.asList(new String[] {".xml"})));

    public static final String PROP_XML_READER = "urn:com.pnehrer.tools.morphine.TypeLibReader.XMLReader";
    public static final String PROP_CLASSPATH = "urn:com.pnehrer.tools.morphine.TypeLibReader.classpath";

    public static final String IN_URI = "urn:com.s1";
    public static final String IN_ELEM_ANNOTATED_OBJECT = "AnnotatedObject";
    public static final String IN_ATTR_ANNOTATED_OBJECT_NAME = "name";
    public static final String IN_ATTR_ANNOTATED_OBJECT_PACKAGE = "packageName";
    public static final String IN_ELEM_PERSISTENCE_INFO = "PersistenceInfo";
    public static final String IN_ATTR_PERSISTENCE_INFO_TABLE = "tableName";
    public static final String IN_ATTR_PERSISTENCE_INFO_PKEY = "primaryKeyColumnName";

    public static final String DEFAULT_ATTR_TYPE = "string";
    public static final String OUT_URI = "";
    public static final String OUT_ELEM_TYPELIB = "typeLib";
    public static final String OUT_ATTR_TYPELIB_DIR = "dir";
    public static final String OUT_ELEM_ANNOTATED_OBJECT = "AnnotatedObject";
    public static final String OUT_ATTR_ANNOTATED_OBJECT_FILE = "file";
    public static final String OUT_ATTR_ANNOTATED_OBJECT_NAME = "name";
    public static final String OUT_ATTR_ANNOTATED_OBJECT_PACKAGE = "package";
    public static final String OUT_ELEM_PERSISTENCE_INFO = "PersistenceInfo";
    public static final String OUT_ATTR_PERSISTENCE_INFO_TABLE = "table";
    public static final String OUT_ATTR_PERSISTENCE_INFO_PKEY = "p-key";

    private class TypeLibObjectHandler extends DefaultHandler {
        private AnnotatedObjectInfo annotatedObjectInfo;

        public void startDocument() throws SAXException {
            annotatedObjectInfo = null;
        }

        public void startElement(String uri, String localName, String qName,
            Attributes attrs) throws SAXException {

            if(IN_URI.equals(uri) && IN_ELEM_ANNOTATED_OBJECT.equals(localName)) {
                annotatedObjectInfo = new AnnotatedObjectInfo();
                annotatedObjectInfo.name = attrs.getValue("", IN_ATTR_ANNOTATED_OBJECT_NAME);
                annotatedObjectInfo.packageName =
                    attrs.getValue("", IN_ATTR_ANNOTATED_OBJECT_PACKAGE);
            }
            else if(annotatedObjectInfo != null && IN_URI.equals(uri) &&
                IN_ELEM_PERSISTENCE_INFO.equals(localName)) {

                annotatedObjectInfo.persistenceInfo = new PersistenceInfo();
                annotatedObjectInfo.persistenceInfo.table =
                    attrs.getValue("", IN_ATTR_PERSISTENCE_INFO_TABLE);
                annotatedObjectInfo.persistenceInfo.pKey =
                    attrs.getValue("", IN_ATTR_PERSISTENCE_INFO_PKEY);
            }
        }

        public AnnotatedObjectInfo asAnnotatedObjectInfo() {
            return annotatedObjectInfo;
        }
    }

    private class AnnotatedObjectInfo {
        String name;
        String packageName;
        PersistenceInfo persistenceInfo;
    }

    private class PersistenceInfo {
        String table;
        String pKey;
    }

    private static final FileFilter XML_FILE_FILTER = new FileFilter() {
        public boolean accept(File file) {
            if(file.isFile()) {
                for(Iterator i = FILE_EXTENSIONS.iterator(); i.hasNext();) {
                    if(file.getName().endsWith(i.next().toString())) return true;
                }
            }

            return false;
        }
    };

    private static final FileFilter DIR_FILTER = new FileFilter() {
        public boolean accept(File file) {
            return file.isDirectory();
        }
    };

    private Map features;
    private Map properties;
    private String basepath;

    private ContentHandler contentHandler;
    private EntityResolver entityResolver;
    private DTDHandler dtdHandler;
    private ErrorHandler errorHandler;

    public TypeLibReader() {
        features = new HashMap();
        features.put("http://xml.org/sax/features/namespaces", Boolean.TRUE);
        features.put("http://xml.org/sax/features/namespace-prefixes", Boolean.FALSE);
        features.put("http://xml.org/sax/features/resolve-dtd-uris", Boolean.TRUE);

        properties = new HashMap();
    }

    public boolean getFeature(String name) throws SAXNotRecognizedException,
        SAXNotSupportedException {

        Boolean value = (Boolean)features.get(name);
        if(value == null) throw new SAXNotRecognizedException(name);
        else return value.booleanValue();
    }

    public void setFeature(String name, boolean value) throws SAXNotRecognizedException,
        SAXNotSupportedException {

        if(features.containsKey(name)) features.put(name, new Boolean(value));
        else throw new SAXNotRecognizedException(name);
    }

    public Object getProperty(String name) throws SAXNotRecognizedException,
        SAXNotSupportedException {

        return properties.get(name);
    }

    public void setProperty(String name, Object value) throws SAXNotRecognizedException,
        SAXNotSupportedException {

        properties.put(name, value);
    }

    public void setEntityResolver(EntityResolver resolver) {
        entityResolver = resolver;
    }

    public EntityResolver getEntityResolver() {
        return entityResolver;
    }

    public void setDTDHandler(DTDHandler handler) {
        dtdHandler = handler;
    }

    public DTDHandler getDTDHandler() {
        return dtdHandler;
    }

    public void setContentHandler(ContentHandler handler) {
        contentHandler = handler;
    }

    public ContentHandler getContentHandler() {
        return contentHandler;
    }

    public void setErrorHandler(ErrorHandler handler) {
        errorHandler = handler;
    }

    public ErrorHandler getErrorHandler() {
        return errorHandler;
    }

    public void parse(InputSource input) throws IOException, SAXException {
        parse(input.getSystemId());
    }

    public void parse(String systemId) throws IOException, SAXException {
        if(systemId != null && contentHandler != null) {
            File baseDir = new File(systemId);
            basepath = baseDir.getAbsolutePath();

            List files = new ArrayList();
            collectFiles(files, systemId, XML_FILE_FILTER);

            contentHandler.startDocument();
            AttributesImpl attrs = new AttributesImpl();
            attrs.addAttribute(OUT_URI, OUT_ATTR_TYPELIB_DIR, OUT_ATTR_TYPELIB_DIR,
                DEFAULT_ATTR_TYPE, basepath);
            contentHandler.startElement(OUT_URI, OUT_ELEM_TYPELIB, OUT_ELEM_TYPELIB, attrs);

            String classpathProp = (String)properties.get(PROP_CLASSPATH);
            ScopedClassLoader loader = null;
            if(classpathProp != null) {
                try {
                    loader = ScopedClassLoader.newInstance(classpathProp,
                        Thread.currentThread().getContextClassLoader());
                }
                catch(MalformedURLException ex) {
                    throw new SAXException(ex);
                }
            }

            if(loader != null) loader.setAsContextClassLoader();

            try {
                TypeLibObjectHandler typeLibHandler = new TypeLibObjectHandler();
                String xmlReaderProp = (String)properties.get(PROP_XML_READER);
                XMLReader reader = xmlReaderProp == null ?
                    XMLReaderFactory.createXMLReader() :
                    (XMLReader)Class.forName(xmlReaderProp, true, loader == null ?
                        Thread.currentThread().getContextClassLoader() : loader).newInstance();
                reader.setContentHandler(typeLibHandler);
                for(Iterator i = files.iterator(); i.hasNext();) {
                    processFile((File)i.next(), reader, typeLibHandler);
                }
            }
            catch(Exception ex) {
                throw new SAXException(ex);
            }
            finally {
                if(loader != null) loader.resetContextClassLoader();
            }

            contentHandler.endElement(OUT_URI, OUT_ELEM_TYPELIB, OUT_ELEM_TYPELIB);
            contentHandler.endDocument();
        }
    }

    protected void processFile(File file, XMLReader reader, TypeLibObjectHandler handler)
        throws IOException, SAXException {

        reader.parse(file.toString());

        AnnotatedObjectInfo aoInfo = handler.asAnnotatedObjectInfo();
        if(aoInfo != null) {
            String relativePath = file.getAbsolutePath();
            if(relativePath.startsWith(basepath)) {
                int offset = basepath.length();
                if(relativePath.charAt(offset) == '/' || relativePath.charAt(offset) == '\\') {
                    ++offset;
                }

                relativePath = relativePath.substring(offset);
            }

            AttributesImpl attrs = new AttributesImpl();
            attrs.addAttribute(OUT_URI, OUT_ATTR_ANNOTATED_OBJECT_FILE,
                OUT_ATTR_ANNOTATED_OBJECT_FILE, DEFAULT_ATTR_TYPE, relativePath);
            if(aoInfo.name != null) attrs.addAttribute(OUT_URI, OUT_ATTR_ANNOTATED_OBJECT_NAME,
                OUT_ATTR_ANNOTATED_OBJECT_NAME, DEFAULT_ATTR_TYPE, aoInfo.name);

            if(aoInfo.packageName != null) {
                attrs.addAttribute(OUT_URI, OUT_ATTR_ANNOTATED_OBJECT_PACKAGE,
                    OUT_ATTR_ANNOTATED_OBJECT_PACKAGE, DEFAULT_ATTR_TYPE, aoInfo.packageName);
            }

            contentHandler.startElement(OUT_URI, OUT_ELEM_ANNOTATED_OBJECT,
                OUT_ELEM_ANNOTATED_OBJECT, attrs);

            PersistenceInfo pi = aoInfo.persistenceInfo;
            if(pi != null) {
                AttributesImpl attrs2 = new AttributesImpl();
                if(pi.table != null) attrs2.addAttribute(OUT_URI, OUT_ATTR_PERSISTENCE_INFO_TABLE,
                    OUT_ATTR_PERSISTENCE_INFO_TABLE, DEFAULT_ATTR_TYPE, pi.table);

                if(pi.pKey != null) attrs2.addAttribute(OUT_URI, OUT_ATTR_PERSISTENCE_INFO_PKEY,
                    OUT_ATTR_PERSISTENCE_INFO_PKEY, DEFAULT_ATTR_TYPE, pi.pKey);

                contentHandler.startElement(OUT_URI, OUT_ELEM_PERSISTENCE_INFO,
                    OUT_ELEM_PERSISTENCE_INFO, attrs2);
                contentHandler.endElement(OUT_URI, OUT_ELEM_PERSISTENCE_INFO,
                    OUT_ELEM_PERSISTENCE_INFO);
            }

            contentHandler.endElement(OUT_URI, OUT_ELEM_ANNOTATED_OBJECT,
                OUT_ELEM_ANNOTATED_OBJECT);
        }
    }

    private static void collectFiles(List files, String dirname, FileFilter filter) {
        File dir = new File(dirname);
        File[] fileArray = dir.listFiles(filter);
        if(fileArray != null && fileArray.length > 0) files.addAll(Arrays.asList(fileArray));

        File[] dirArray = dir.listFiles(DIR_FILTER);
        for(int i = 0, n = dirArray == null ? 0 : dirArray.length; i < n; ++i) {
            collectFiles(files, dirArray[i].toString(), filter);
        }
    }
}
