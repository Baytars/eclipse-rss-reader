package com.pnehrer.tools.morphine.ant.types;

import java.util.Iterator;

import org.apache.tools.ant.AntClassLoader;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.Path;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;


public class ReaderElement extends DataTypeWithProperties {

    protected String factoryClassName;
    protected Path classPath;

    public void setClassname(String value) {
        factoryClassName = value;
    }

    public void setClasspath(Path classpath) {
        this.classPath = classpath;
    }

    public Path createClasspath(Project project) {
        return classPath = new Path(project);
    }

    public XMLReader makeXMLReader() throws BuildException {
        AntClassLoader loader = null; 
        try {
            Class factoryClass = null;
            if(classPath != null) {
                loader = new AntClassLoader(project.getCoreLoader(),
                	project, classPath, false);
                loader.setIsolated(true);
                loader.setThreadContextLoader();
            }
            
            XMLReader reader = factoryClassName == null ?
            	XMLReaderFactory.createXMLReader() :
            	XMLReaderFactory.createXMLReader(factoryClassName);
            	
            for(Iterator i = properties.values().iterator(); i.hasNext();) {
            	NameValuePair item = (NameValuePair)i.next();
            	reader.setProperty(item.getName(), item.getValue());
            }

			return reader;
        }
        catch(SecurityException e) {
            throw e;
        }
        catch(Throwable e) {
            throw new BuildException(e);
        }
        finally {
            if(loader != null) {
                loader.resetThreadContextLoader();
                loader.cleanup();
            }	            
        }
    }
}
