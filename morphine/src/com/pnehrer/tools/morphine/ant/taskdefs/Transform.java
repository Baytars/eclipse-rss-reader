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

package com.pnehrer.tools.morphine.ant.taskdefs;

import javax.xml.transform.sax.TransformerHandler;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.xml.sax.XMLReader;

import com.pnehrer.tools.morphine.EventMultiplexor;
import com.pnehrer.tools.morphine.ant.types.MultiplexorElement;
import com.pnehrer.tools.morphine.ant.types.ProcessorElement;
import com.pnehrer.tools.morphine.ant.types.ReaderElement;
import com.pnehrer.tools.morphine.ant.types.TransformerElement;

public class Transform extends Task {

    protected String src;
    protected ReaderElement readerElement;
    protected ProcessorElement processorElement;
    protected MultiplexorElement multiplexorElement;
    protected TransformerElement transformerElement;

    public void setSrc(String value) {
        src = value;
    }

    public Object createReader() {
        return readerElement = new ReaderElement();
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

    public void execute() throws BuildException {
    	if(readerElement == null) {
    		throw new BuildException("missing reader element");
    	}
    	
        try {
            XMLReader reader = readerElement.makeXMLReader();

			if(processorElement != null) {
				reader.setContentHandler(processorElement.makeContentHandler());
			}
			else if(multiplexorElement != null) {
				EventMultiplexor multiplexor = 
					multiplexorElement.makeEventMultiplexor();
				reader.setContentHandler(multiplexor);
				reader.setDTDHandler(multiplexor);
//				reader.setProperty(
//					"http://xml.org/sax/properties/lexical-handler",
//					multiplexor);
			}
			else if(transformerElement != null) {
				TransformerHandler th = transformerElement.makeTransformerHandler();
				reader.setContentHandler(th);
				reader.setDTDHandler(th);
//				reader.setProperty(
//					"http://xml.org/sax/properties/lexical-handler", th);
			}

            reader.parse(src);
        }
        catch(BuildException ex) {
            throw ex;
        }
        catch(Exception ex) {
            throw new BuildException(ex, location);
        }
    }
}
