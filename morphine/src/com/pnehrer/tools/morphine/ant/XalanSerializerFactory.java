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

package com.pnehrer.tools.morphine.ant;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import javax.xml.transform.OutputKeys;

import org.apache.xalan.serialize.Method;
import org.apache.xalan.serialize.Serializer;
import org.apache.xalan.serialize.SerializerFactory;
import org.apache.xalan.templates.OutputProperties;
import org.apache.xml.serialize.OutputFormat;

import org.xml.sax.ContentHandler;

/**
 * @author pnehrer
 */
public class XalanSerializerFactory implements ContentHandlerFactory {
	
	/**
	 * @see com.pnehrer.tools.morphine.ant.ContentHandlerFactory#create(java.util.Map)
	 */
	public ContentHandler create(Map properties)
		throws ContentHandlerFactoryException {
			
		String method = (String)properties.remove("method");
		String output = (String)properties.remove("output");
        
		Properties props = OutputProperties.getDefaultMethodProperties(method == null ? Method.XML : method);
		for(Iterator i = properties.entrySet().iterator(); i.hasNext();) {
			Map.Entry item = (Map.Entry)i.next();
		    props.setProperty(String.valueOf(item.getKey()), String.valueOf(item.getValue()));
		}

        Serializer serializer = SerializerFactory.getSerializer(props);
		try {
			if(output == null) serializer.setOutputStream(System.out);
			else serializer.setWriter(new FileWriter(output));

		    return serializer.asContentHandler();
		}
		catch(IOException ex) {
			throw new ContentHandlerFactoryException(ex);
		}
	}
}
