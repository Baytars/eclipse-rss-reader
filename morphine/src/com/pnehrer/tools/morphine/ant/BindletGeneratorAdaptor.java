package com.pnehrer.tools.morphine.ant;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

import javax.xml.transform.*;
import javax.xml.transform.stream.*;

import org.apache.tools.ant.*;
import org.apache.tools.ant.types.*;
import org.xml.sax.*;

import com.pnehrer.tools.morphine.ant.*;
import com.pnehrer.tools.morphine.*;

/**
 * #@# #$#
 */
public class BindletGeneratorAdaptor extends BindletToolAdaptor {

    public static final String PARAM_TRANSFORMER_FACTORY_CLASSPATH = "transformerFactoryClasspath";
    public static final String PARAM_TRANSFORMER_FACTORY = "transformerFactory";
    public static final String PARAM_GENERATOR_XSL = "generatorXsl";

    protected Path classpath;
    protected String factory;
    protected String xsl;

    public void setParameter(String name, Object value) {
        if(PARAM_TRANSFORMER_FACTORY_CLASSPATH.equals(name) && value != null) {
            classpath = new Path(project, value.toString());
        }
        if(PARAM_TRANSFORMER_FACTORY.equals(name) && value != null) {
            factory = value.toString();
        }
        else if(PARAM_GENERATOR_XSL.equals(name) && value != null) {
            xsl = value.toString();
        }
    }

    public void startDocument() throws SAXException {
        ScopedClassLoader loader = null;
        if(classpath != null) {
            try {
                loader = ScopedClassLoader.newInstance(classpath,
                    Thread.currentThread().getContextClassLoader());
            }
            catch(MalformedURLException ex) {
                throw new SAXException(ex);
            }
        }

        if(loader != null) loader.setAsContextClassLoader();

        try {
            Class clazz = Class.forName(factory, true, loader == null ?
                Thread.currentThread().getContextClassLoader() : loader);
            tool = BindletGenerator.create(new StreamSource(xsl),
                (TransformerFactory)clazz.newInstance());
        }
        catch(Exception ex) {
            throw new RuntimeException("could not create BindletGenerator: " + ex);
        }
        finally {
            if(loader != null) loader.resetContextClassLoader();
        }

        super.startDocument();
    }
}
