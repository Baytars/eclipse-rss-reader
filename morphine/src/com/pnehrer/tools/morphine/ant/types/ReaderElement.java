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

    public Path createClasspath() {
        return classPath = new Path(project);
    }

    public XMLReader makeXMLReader() throws BuildException {
        AntClassLoader loader = null; 
        try {
            Class factoryClass = null;
            if(classPath != null) {
                loader = new AntClassLoader(XMLReader.class.getClassLoader(),
                	project, classPath, true);
                loader.setIsolated(false);
                loader.setThreadContextLoader();
            }
            
            XMLReader reader = factoryClassName == null ?
                XMLReaderFactory.createXMLReader() :
                XMLReaderFactory.createXMLReader(factoryClassName);
                
            for(Iterator i = properties.entrySet().iterator(); i.hasNext();) {
            	Map.Entry item = (Map.Entry)i.next();
            	reader.setProperty(String.valueOf(item.getKey()),
            		String.valueOf(item.getValue()));
            }

			return reader;
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
        }
    }
}
