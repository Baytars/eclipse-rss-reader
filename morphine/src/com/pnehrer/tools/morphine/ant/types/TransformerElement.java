/**
 * Copyright (c) 2002 Peter Nehrer
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR AND CONTRIBUTORS ``AS IS'' AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED.  IN NO EVENT SHALL THE AUTHOR OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS
 * OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
 * OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 *
 * $Id$
 */

package com.pnehrer.tools.morphine.ant.types;

import java.util.Iterator;
import java.util.Map;

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
	
	public void setClasspath(Path path) {
		classPath = path;
	}
	
	public Path createClasspath() {
		return classPath = new Path(project);
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
                loader = new AntClassLoader(TransformerFactory.class.getClassLoader(),
                	project, classPath, true);
                loader.setIsolated(false);
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
				for(Iterator i = properties.entrySet().iterator(); i.hasNext();) {
					Map.Entry item = (Map.Entry)i.next();
					transformer.setOutputProperty(String.valueOf(item.getKey()),
						String.valueOf(item.getValue()));
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
        	e.printStackTrace();
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
