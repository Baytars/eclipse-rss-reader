package com.pnehrer.tools.morphine.ant.types;

import org.apache.tools.ant.AntClassLoader;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.Path;

import org.xml.sax.ContentHandler;

import com.pnehrer.tools.morphine.ant.ContentHandlerFactory;
import com.pnehrer.tools.morphine.ant.ContentHandlerFactoryException;

/**
 * @author Peter Nehrer
 */
public class ProcessorElement extends DataTypeWithProperties {

	protected String factoryClassName;
	protected Path classPath;

	public void setClassname(String className) {
		factoryClassName = className;
	}
	
	public Path createClasspath(Project p) {
		return classPath = new Path(p);
	}
	
	public ContentHandler makeContentHandler() throws BuildException {
		if(factoryClassName == null) {
			return null;
		}
		else {
	        AntClassLoader loader = null; 
	        try {
	            Class factoryClass = null;
	            if(classPath == null) {
	                factoryClass = Class.forName(factoryClassName);
	            }
	            else {
	                loader = new AntClassLoader(project.getCoreLoader(),
	                	project, classPath, false);
	                loader.setIsolated(true);
	                loader.setThreadContextLoader();
	                factoryClass = loader.forceLoadClass(factoryClassName);
	                AntClassLoader.initializeClass(factoryClass);
	            }

				ContentHandlerFactory factory = (ContentHandlerFactory)
					factoryClass.newInstance();    
					
				return factory.create(properties);	
	        }
	        catch(ClassNotFoundException e) {
	            throw new BuildException("Could not find " + factoryClassName 
	            						 + "."
	                                     + " Make sure you have it in your"
	                                     + " classpath");
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
}