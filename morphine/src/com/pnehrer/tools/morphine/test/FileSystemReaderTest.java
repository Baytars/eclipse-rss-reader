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

package com.pnehrer.tools.morphine.test;

import java.io.IOException;
import java.util.Properties;

import javax.xml.transform.OutputKeys;

import junit.framework.TestCase;

import org.apache.xalan.serialize.Method;
import org.apache.xalan.serialize.Serializer;
import org.apache.xalan.serialize.SerializerFactory;
import org.apache.xalan.templates.OutputProperties;
import org.apache.xml.serialize.OutputFormat;

import org.xml.sax.SAXException;

import com.pnehrer.tools.morphine.FileSystemReader;

/**
 * @author Peter Nehrer
 */
public class FileSystemReaderTest extends TestCase {

	/**
	 * Constructor for FileSystemReaderTest.
	 * @param arg0
	 */
	public FileSystemReaderTest(String arg0) {
		super(arg0);
	}

	/*
	 * Test for void parse(String)
	 */
	public void testParseString() throws IOException, SAXException {
		Properties props = OutputProperties.getDefaultMethodProperties(Method.XML);
		props.setProperty(OutputKeys.INDENT, "yes");
		props.setProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
		props.setProperty(OutputKeys.STANDALONE, "yes");
		
		Serializer serializer = SerializerFactory.getSerializer(props);
		serializer.setOutputStream(System.out);
		
		FileSystemReader fsReader = new FileSystemReader();
		fsReader.setContentHandler(serializer.asContentHandler());
		fsReader.parse(".");
	}

}
