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

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLReader;

import org.xml.sax.helpers.AttributesImpl;

/**
 * @author Peter Nehrer
 */
public class FileSystemReader implements XMLReader {
	
	private abstract class Property {
		String key;
		
		Property(String key) {
			this.key = key;
		}
		
		abstract void setValue(Object value);

		abstract Object getValue();
	}
	
	public static final String FILE_FILTER_PROP = 
		"http://pnehrer.com/morphine/filter";

	public static final String DEFAULT_URI = "";
	public static final String DEFAULT_PREFIX = "";
	public static final String DIR_ELEMENT = "directory";
	public static final String FILE_ELEMENT = "file";
	public static final String READ_ATTR = "read";
	public static final String WRITE_ATTR = "write";
	public static final String ABS_PATH_ATTR = "absolute-path";
	public static final String CAN_PATH_ATTR = "canonical-path";
	public static final String NAME_ATTR = "name";
	public static final String PATH_ATTR = "path";
	public static final String ABS_ATTR = "absolute";
	public static final String HID_ATTR = "hidden";
	public static final String LM_ATTR = "last-modified";
	public static final String LEN_ATTR = "length";
	
	private static final String CDATA = "CDATA";
		
	private ContentHandler contentHandler;
	private Map properties;
	private FileFilter fileFilter;

	public FileSystemReader() {
		properties = new HashMap();

		properties.put(FILE_FILTER_PROP,
			new Property(FILE_FILTER_PROP) {

				void setValue(Object value) {
					fileFilter = (FileFilter)value;
				}
				
				Object getValue() {
					return fileFilter;
				}
			});
	}

	public void setFileFilter(FileFilter fileFilter) {
		this.fileFilter = fileFilter;
	}
	
	public FileFilter getFileFilter() {
		return fileFilter;
	}
	
	/**
	 * @see org.xml.sax.XMLReader#getFeature(java.lang.String)
	 */
	public boolean getFeature(String feature)
		throws SAXNotRecognizedException, SAXNotSupportedException {
		return false;
	}

	/**
	 * @see org.xml.sax.XMLReader#setFeature(java.lang.String, boolean)
	 */
	public void setFeature(String feature, boolean value)
		throws SAXNotRecognizedException, SAXNotSupportedException {
	}

	/**
	 * @see org.xml.sax.XMLReader#getProperty(java.lang.String)
	 */
	public Object getProperty(String property)
		throws SAXNotRecognizedException, SAXNotSupportedException {
		
		Property propObj = (Property)properties.get(property);
		if(propObj == null) throw new SAXNotRecognizedException(property);
		else return propObj.getValue();
	}

	/**
	 * @see org.xml.sax.XMLReader#setProperty(java.lang.String, java.lang.Object)
	 */
	public void setProperty(String property, Object value)
		throws SAXNotRecognizedException, SAXNotSupportedException {
			
		Property propObj = (Property)properties.get(property);
		if(propObj == null) throw new SAXNotRecognizedException(property);
		else propObj.setValue(value);
	}

	/**
	 * @see org.xml.sax.XMLReader#setEntityResolver(org.xml.sax.EntityResolver)
	 */
	public void setEntityResolver(EntityResolver entityResolver) {
	}

	/**
	 * @see org.xml.sax.XMLReader#getEntityResolver()
	 */
	public EntityResolver getEntityResolver() {
		return null;
	}

	/**
	 * @see org.xml.sax.XMLReader#setDTDHandler(org.xml.sax.DTDHandler)
	 */
	public void setDTDHandler(DTDHandler dtdHandler) {
	}

	/**
	 * @see org.xml.sax.XMLReader#getDTDHandler()
	 */
	public DTDHandler getDTDHandler() {
		return null;
	}

	/**
	 * @see org.xml.sax.XMLReader#setContentHandler(org.xml.sax.ContentHandler)
	 */
	public void setContentHandler(ContentHandler contentHandler) {
		this.contentHandler = contentHandler;
	}

	/**
	 * @see org.xml.sax.XMLReader#getContentHandler()
	 */
	public ContentHandler getContentHandler() {
		return contentHandler;
	}

	/**
	 * @see org.xml.sax.XMLReader#setErrorHandler(org.xml.sax.ErrorHandler)
	 */
	public void setErrorHandler(ErrorHandler errorHandler) {
	}

	/**
	 * @see org.xml.sax.XMLReader#getErrorHandler()
	 */
	public ErrorHandler getErrorHandler() {
		return null;
	}

	/**
	 * @see org.xml.sax.XMLReader#parse(org.xml.sax.InputSource)
	 */
	public void parse(InputSource inputSource) throws IOException, SAXException {
		if(inputSource instanceof FileInputSource) {
			parse(((FileInputSource)inputSource).getFile());
		}
		else throw new IllegalArgumentException("expected instance of '" +
			FileInputSource.class.getName() + "'");
	}

	/**
	 * @see org.xml.sax.XMLReader#parse(java.lang.String)
	 */
	public void parse(String uri) throws IOException, SAXException {
		parse(new File(uri));
	}
	
	public void parse(File root) throws IOException, SAXException {
		if(contentHandler != null) {
			contentHandler.startDocument();

			if(root.isDirectory()) processDirectory(root);
			else processFile(root);

			contentHandler.endDocument();
		}
	}
	
	private void processDirectory(File dir) throws IOException, SAXException {
		AttributesImpl attrs = new AttributesImpl();
		populateAttributes(attrs, dir);
		
		contentHandler.startElement(DEFAULT_URI, DIR_ELEMENT, 
			createQName(DEFAULT_PREFIX, DIR_ELEMENT), attrs);

		File[] file = dir.listFiles();
		for(int i = 0, n = file.length; i < n; ++i) {
			if(file[i].canRead() && file[i].isDirectory()) {
				processDirectory(file[i]);
			}
			else processFile(file[i]); 
		}

		contentHandler.endElement(DEFAULT_URI, DIR_ELEMENT, 
			createQName(DEFAULT_PREFIX, DIR_ELEMENT));
	}

	private void processFile(File file) throws IOException, SAXException {
		AttributesImpl attrs = new AttributesImpl();
		attrs.addAttribute(DEFAULT_URI, LEN_ATTR, createQName(DEFAULT_PREFIX,
			LEN_ATTR), CDATA, String.valueOf(file.length()));
		populateAttributes(attrs, file);

		contentHandler.startElement(DEFAULT_URI, FILE_ELEMENT, 
			createQName(DEFAULT_PREFIX, FILE_ELEMENT), attrs);

		contentHandler.endElement(DEFAULT_URI, FILE_ELEMENT, 
			createQName(DEFAULT_PREFIX, FILE_ELEMENT));
	}
	
	private void populateAttributes(AttributesImpl attrs, File file) 
		throws IOException {
		
		attrs.addAttribute(DEFAULT_URI, ABS_ATTR, createQName(DEFAULT_PREFIX,
			ABS_ATTR), CDATA, String.valueOf(file.isAbsolute()));

		attrs.addAttribute(DEFAULT_URI, ABS_PATH_ATTR, createQName(DEFAULT_PREFIX,
			ABS_PATH_ATTR), CDATA, file.getAbsolutePath());

		attrs.addAttribute(DEFAULT_URI, CAN_PATH_ATTR, createQName(DEFAULT_PREFIX,
			CAN_PATH_ATTR), CDATA, file.getCanonicalPath());

		attrs.addAttribute(DEFAULT_URI, HID_ATTR, createQName(DEFAULT_PREFIX,
			HID_ATTR), CDATA, String.valueOf(file.isHidden()));

		attrs.addAttribute(DEFAULT_URI, LM_ATTR, createQName(DEFAULT_PREFIX,
			LM_ATTR), CDATA, String.valueOf(new Date(file.lastModified())));

		attrs.addAttribute(DEFAULT_URI, NAME_ATTR, createQName(DEFAULT_PREFIX,
			NAME_ATTR), CDATA, file.getName());

		attrs.addAttribute(DEFAULT_URI, PATH_ATTR, createQName(DEFAULT_PREFIX,
			PATH_ATTR), CDATA, file.getPath());

		attrs.addAttribute(DEFAULT_URI, READ_ATTR, createQName(DEFAULT_PREFIX,
			READ_ATTR), CDATA, String.valueOf(file.canRead()));

		attrs.addAttribute(DEFAULT_URI, WRITE_ATTR, createQName(DEFAULT_PREFIX,
			WRITE_ATTR), CDATA, String.valueOf(file.canWrite()));
	}
	
	private static String createQName(String prefix, String name) {
		return prefix == null || prefix.length() == 0 ?
			name : prefix + ":" + name;
	}
}
