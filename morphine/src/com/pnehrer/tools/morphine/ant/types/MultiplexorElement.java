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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.transform.sax.TransformerHandler;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.DataType;

import com.pnehrer.tools.morphine.EventMultiplexor;

/**
 * @author Peter Nehrer
 */
public class MultiplexorElement extends DataType {

	protected List processors = new ArrayList();
	protected List multiplexors = new ArrayList();
	protected List transformers = new ArrayList();

	public Object createProcessor() {
		ProcessorElement processor = new ProcessorElement();
		processors.add(processor);
		return processor;
	}
	
	public Object createMultiplexor() {
		MultiplexorElement multiplexor = new MultiplexorElement();
		multiplexors.add(multiplexor);
		return multiplexor;
	}
	
	public Object createTransformer() {
		TransformerElement transformerElement = new TransformerElement();
		transformers.add(transformerElement);
		return transformerElement;
	}
	
	public EventMultiplexor makeEventMultiplexor() throws BuildException {
		EventMultiplexor multiplexor = new EventMultiplexor();
		
		for(Iterator i = processors.iterator(); i.hasNext();) {
			ProcessorElement item = (ProcessorElement)i.next();
			multiplexor.addContentHandler(item.makeContentHandler());
		}
		
		for(Iterator i = multiplexors.iterator(); i.hasNext();) {
			MultiplexorElement item = (MultiplexorElement)i.next();
			EventMultiplexor childMultiplexor = item.makeEventMultiplexor(); 
			multiplexor.addContentHandler(childMultiplexor);
			multiplexor.addLexicalHandler(childMultiplexor);
			multiplexor.addDTDHandler(childMultiplexor);
		}
		
		for(Iterator i = transformers.iterator(); i.hasNext();) {
			TransformerElement item = (TransformerElement)i.next();
			TransformerHandler transformerHandler = 
				item.makeTransformerHandler();
			multiplexor.addContentHandler(transformerHandler);
			multiplexor.addLexicalHandler(transformerHandler);
			multiplexor.addDTDHandler(transformerHandler);
		}
		
		return multiplexor;
	}
}
