package com.pnehrer.tools.morphine.ant.taskdefs;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

import org.apache.tools.ant.*;
import org.apache.tools.ant.types.*;
import org.xml.sax.*;

import com.pnehrer.tools.morphine.ant.*;
import com.pnehrer.tools.morphine.ant.types.*;

public class CustomStepSpec extends TransformationStepSpec {

    private String classname;
    private Path classpath;

    public String getClassname() {
        return classname;
    }

    public void setClassname(String classname) {
        this.classname = classname;
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
//            Class clazz = Class.forName(classname, true, loader == null ?
//                Thread.currentThread().getContextClassLoader() : loader);
            AntClassLoader antLoader = new AntClassLoader(TransformationStep.class.getClassLoader(), true);
            if(classpath != null) {
                String[] pathElements = classpath.list();
                for(int i = 0, n = pathElements.length; i < n; ++i) antLoader.addPathElement(pathElements[i]);
            }

            if(loader != null) {
                URL[] urls = loader.getURLs();
                for(int i = 0, n = urls.length; i < n; ++i) antLoader.addPathElement(urls.toString());
            }

            Class clazz = Class.forName(classname, true, antLoader);

            TransformationStep step = (TransformationStep)clazz.newInstance();
            step.setProject(project);

            for(Iterator i = params.iterator(); i.hasNext();) {
                NameValuePair item = (NameValuePair)i.next();
                step.setParameter(item.getName(), item.getValue());
            }

            step.setNext(next);

            return step;
        }
        catch(Exception ex) {
            throw new BuildException("could not chain transformation steps", ex);
        }
        finally {
            if(loader != null) loader.resetContextClassLoader();
        }
    }
}
