package com.pnehrer.tools.morphine.ant.taskdefs;

import java.net.*;
import java.io.*;
import java.util.*;
import javax.xml.transform.*;

import org.apache.tools.ant.*;
import org.apache.tools.ant.types.*;
import org.apache.xalan.serialize.*;
import org.apache.xalan.templates.*;

import com.pnehrer.tools.morphine.ant.*;
import com.pnehrer.tools.morphine.ant.types.*;

public class SerializerSpec extends DataTypeWithProperties {

    protected Transform task;
    protected String classname;
    protected Path classpath;
    protected File outputFile;

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

    public void setOutputFile(File outputFile) {
        this.outputFile = outputFile;
    }

    public Serializer createSerializer() throws BuildException {
        String method = getProperty(OutputKeys.METHOD);
        Properties serializerProps = new Properties(OutputProperties.getDefaultMethodProperties(
            method == null ? Method.XML : method));

        for(Iterator i = props.iterator(); i.hasNext();) {
            NameValuePair item = (NameValuePair)i.next();
            serializerProps.setProperty(item.getName(), item.getValue());
        }

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

        Serializer serializer = null;
        try {
            if(classname == null) serializer = SerializerFactory.getSerializer(serializerProps);
            else {
                try {
                    Class clazz = Class.forName(classname, true, loader == null ?
                        Thread.currentThread().getContextClassLoader() : loader);
                    serializer = (Serializer)clazz.newInstance();
                }
                catch(Exception ex) {
                    throw new BuildException(ex);
                }

                serializer.setOutputFormat(serializerProps);
            }


            try {
                if(outputFile == null) serializer.setOutputStream(System.out);
                else serializer.setWriter(new FileWriter(outputFile));
            }
            catch(IOException ex) {
                throw new BuildException(ex);
            }
        }
        finally {
            if(loader != null) loader.resetContextClassLoader();
        }

        return serializer;
    }
}
