package com.pnehrer.tools.morphine.ant.taskdefs;

import java.net.*;
import java.util.*;

import org.apache.tools.ant.*;
import org.apache.tools.ant.types.*;
import org.xml.sax.*;
import org.xml.sax.helpers.*;

import com.pnehrer.tools.morphine.ant.*;
import com.pnehrer.tools.morphine.ant.types.*;

public class ReaderSpec extends DataTypeWithProperties {

    protected Transform task;
    protected String classname;
    protected Path classpath;

    public void setTask(Transform task) {
        this.task = task;
    }

    public void setClassname(String value) {
        classname = value;
    }

    public void setClasspath(Path classpath) {
        this.classpath = classpath;
    }

    public Object createClasspath() {
        return classpath = new Path(project);
    }

    public XMLReader createXMLReader() throws BuildException {
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

        XMLReader reader = null;
        try {
            if(classname == null) {
                reader = XMLReaderFactory.createXMLReader();
            }
            else {
                Class clazz = Class.forName(classname, true, loader == null ?
                    Thread.currentThread().getContextClassLoader() : loader);
                reader = (XMLReader)clazz.newInstance();
            }

            for(Iterator i = props.iterator(); i.hasNext();) {
                NameValuePair item = (NameValuePair)i.next();
                reader.setProperty(item.getName(), item.getValue());
            }
        }
        catch(Exception ex) {
            throw new BuildException("could not create XMLReader", ex);
        }
        finally {
            if(loader != null) loader.resetContextClassLoader();
        }

        return reader;
    }
}
