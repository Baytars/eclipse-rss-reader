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

package com.pnehrer.tools.morphine;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.AttributesImpl;

/**
 * @author Peter Nehrer
 */
public class FileTransformer extends DefaultHandler {

	private Transformer transformer;
	private SourceFactory sourceFactory;
	private ResultFactory resultFactory;
	private StringBuffer buffer;
	private Attributes attrs;
	
	public FileTransformer(Transformer transformer,
		SourceFactory sourceFactory,
		ResultFactory resultFactory) {
		
		this.transformer = transformer;
		this.sourceFactory = sourceFactory;
		this.resultFactory = resultFactory;
	}

	/**
	 * @see org.xml.sax.ContentHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
	 */
	public void startElement(
		String uri,
		String localName,
		String qName,
		Attributes attrs)
		throws SAXException {

		if(((uri == null || "".equals(uri)) && "file".equals(localName))
			|| "file".equals(qName)) {
				
			buffer = new StringBuffer();
			this.attrs = new AttributesImpl(attrs);
		}
	}

	/**
	 * @see org.xml.sax.ContentHandler#characters(char, int, int)
	 */
	public void characters(char[] ch, int offset, int len)
		throws SAXException {

		if(buffer != null) buffer.append(ch, offset, len);
	}

	/**
	 * @see org.xml.sax.ContentHandler#endElement(java.lang.String, java.lang.String, java.lang.String)
	 */
	public void endElement(String uri, String localName, String qName)
		throws SAXException {

		if(((uri == null || "".equals(uri)) && "file".equals(localName))
			|| "file".equals(qName)) {

			String contents = buffer.toString();
			
			try {
				transformer.transform(
					sourceFactory.createSource(attrs, contents),
					resultFactory.createResult(attrs, contents));
			}
			catch(TransformerException ex) {
				ex.printStackTrace();
				throw new SAXException(ex);
			}				
		}		
	}
}
