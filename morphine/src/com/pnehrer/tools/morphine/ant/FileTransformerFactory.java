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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;

import com.pnehrer.tools.morphine.FileTransformer;
import com.pnehrer.tools.morphine.ResultFactory;
import com.pnehrer.tools.morphine.SourceFactory;

/**
 * @author Peter Nehrer
 */
public class FileTransformerFactory implements ContentHandlerFactory {
	
	public class DefaultResultFactory implements ResultFactory {
		public Result createResult(Attributes attrs, String content)
			throws TransformerException {

			return new StreamResult(System.out);
		}
	}

	/**
	 * @see com.pnehrer.tools.morphine.ant.ContentHandlerFactory#create(java.util.Map)
	 */
	public ContentHandler create(Map properties)
		throws ContentHandlerFactoryException {

		Map props = new HashMap(properties);

		ClassLoader loader = Thread.currentThread().getContextClassLoader();
	
		String sourceFactoryName = (String)props.remove("source-factory");
		if(sourceFactoryName == null) {
			throw new ContentHandlerFactoryException("source-factory not specified");
		}
	
		String resultFactoryName = (String)props.remove("result-factory");

		SourceFactory sourceFactory = null;
		ResultFactory resultFactory = null;
		Transformer transformer = null; 
		try {		
			Class sourceFactoryClass = loader.loadClass(sourceFactoryName);
			sourceFactory = (SourceFactory)sourceFactoryClass.newInstance();

			if(resultFactoryName == null) resultFactory = new DefaultResultFactory();
			else {		
				Class resultFactoryClass = loader.loadClass(resultFactoryName);
				resultFactory = (ResultFactory)resultFactoryClass.newInstance();
			}
			
			String src = (String)props.remove("xsl");
			TransformerFactory tf = TransformerFactory.newInstance();
			transformer = src == null ?
				tf.newTransformer() : tf.newTransformer(new StreamSource(src));
		}
		catch(Exception ex) {
			ex.printStackTrace();
			throw new ContentHandlerFactoryException(ex);
		}

		for(Iterator i = props.entrySet().iterator(); i.hasNext();) {
			Map.Entry item = (Map.Entry)i.next();
			String name = (String)item.getKey();
			if(name.startsWith("param:")) {
				transformer.setParameter(name.substring("param:".length()),
					item.getValue());
			}
			else {
				transformer.setOutputProperty(name, item.getValue().toString());
			}
		}
		
		return new FileTransformer(transformer, sourceFactory, resultFactory);
	}
}
