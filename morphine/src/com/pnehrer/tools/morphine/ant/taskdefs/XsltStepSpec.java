package com.pnehrer.tools.morphine.ant.taskdefs;

import java.net.MalformedURLException;
import java.util.*;

import javax.xml.transform.*;
import javax.xml.transform.sax.*;
import javax.xml.transform.stream.*;

import org.xml.sax.*;
import org.apache.tools.ant.*;
import org.apache.tools.ant.types.*;

import com.pnehrer.tools.morphine.ant.*;
import com.pnehrer.tools.morphine.ant.types.*;

/**
 * #@# #$#
 */
public class XsltStepSpec extends TransformationStepSpec {

    protected String src;
    protected String factory;
    protected Path classpath;

    public void setSrc(String src) {
        this.src = src;
    }

    public void setFactory(String factory) {
        this.factory = factory;
    }

    public void setClasspath(Path classpath) {
        this.classpath = classpath;
    }

    public Object createClasspath() {
        return classpath = new Path(project);
    }

    public ContentHandler chain(ContentHandler next) throws BuildException {
        ScopedClassLoader loader = null;
        if(classpath != null) {
            try {
                loader = ScopedClassLoader.newInstance(classpath,
                    Thread.currentThread().getContextClassLoader());
            }
            catch(MalformedURLException ex) {
                throw new BuildException(ex);
            }
        }

        if(loader != null) loader.setAsContextClassLoader();

        try {
            SAXTransformerFactory factoryInstance = null;
            if(factory == null) {
                factoryInstance = (SAXTransformerFactory)SAXTransformerFactory.newInstance();
            }
            else {
                Class clazz = Class.forName(factory, true, loader == null ?
                    Thread.currentThread().getContextClassLoader() : loader);
                factoryInstance = (SAXTransformerFactory)clazz.newInstance();
            }

            TransformerHandler transformerHandler =
                factoryInstance.newTransformerHandler(new StreamSource(src));
            Transformer transformer = transformerHandler.getTransformer();

            for(Iterator i = params.iterator(); i.hasNext();) {
                NameValuePair item = (NameValuePair)i.next();
                transformer.setParameter(item.getName(), item.getValue());
            }

            transformerHandler.setResult(new SAXResult(next));

            return transformerHandler;
        }
        catch(Exception ex) {
            throw new BuildException(ex);
        }
        finally {
            if(loader != null) loader.resetContextClassLoader();
        }
    }
}
