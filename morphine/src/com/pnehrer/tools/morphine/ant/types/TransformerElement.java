package com.pnehrer.tools.morphine.ant.types;

import java.util.Iterator;

import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamSource;

import org.apache.tools.ant.AntClassLoader;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.Path;

/**
 * @author Peter Nehrer
 */
public class TransformerElement extends DataTypeWithProperties {

	protected String factoryClassName;
	protected Path classPath;
	protected String src;
	protected ProcessorElement processorElement;
	protected MultiplexorElement multiplexorElement;
	protected TransformerElement transformerElement;

	public void setClassname(String className) {
		factoryClassName = className;
	}
	
	public Path createClasspath(Project p) {
		return classPath = new Path(p);
	}
	
	public void setSrc(String src) {
		this.src = src;
	}
	
	public Object createProcessor() {
		return processorElement = new ProcessorElement();
	}
	
	public Object createMultiplexor() {
		return multiplexorElement = new MultiplexorElement();
	}
	
	public Object createTransformer() {
		return transformerElement = new TransformerElement();
	}
	
	public TransformerHandler makeTransformerHandler() throws BuildException {
			
		String oldFactoryClassName = null;
        AntClassLoader loader = null; 
        try {
			if(factoryClassName != null) {
				oldFactoryClassName = 
					System.setProperty("javax.xml.transform.TransformerFactory",
						factoryClassName);
			}

            if(classPath != null) {
                loader = new AntClassLoader(project.getCoreLoader(),
                	project, classPath, false);
                loader.setIsolated(true);
                loader.setThreadContextLoader();
            }

            TransformerFactory factory = TransformerFactory.newInstance();
            if(factory.getFeature(SAXTransformerFactory.FEATURE)) {
            	SAXTransformerFactory saxFactory = 
            		(SAXTransformerFactory)factory;
            	TransformerHandler th = src == null ? 
            		saxFactory.newTransformerHandler() :
            		saxFactory.newTransformerHandler(new StreamSource(src));

				Transformer transformer = th.getTransformer();
				for(Iterator i = properties.values().iterator(); i.hasNext();) {
					NameValuePair item = (NameValuePair)i.next();
					transformer.setOutputProperty(item.getName(),
						item.getValue());
				}
				
				/**
				 * @todo Add parameter initialization, error listener,
				 * URI resolver, etc.				 */

				Result result = null;            	
            	if(processorElement != null) {
            		result = 
            			new SAXResult(processorElement.makeContentHandler());
            	}
            	else if(multiplexorElement != null) {
            		result = new SAXResult(
            			multiplexorElement.makeEventMultiplexor());
            	}
            	else if(transformerElement != null) {
            		result = new SAXResult(
            			transformerElement.makeTransformerHandler());
            	}
            	
            	th.setResult(result);
            	
            	return th;
            }
            else return null;
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
            
            if(oldFactoryClassName != null) { 
					System.setProperty("javax.xml.transform.TransformerFactory",
						oldFactoryClassName);
            }
        }
	}
}
