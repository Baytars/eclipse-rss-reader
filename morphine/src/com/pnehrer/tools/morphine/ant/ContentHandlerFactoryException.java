package com.pnehrer.tools.morphine.ant;

import org.xml.sax.SAXException;

/**
 * @author Peter Nehrer
 */
public class ContentHandlerFactoryException extends SAXException {

	/**
	 * Constructor for ContentHandlerFactoryException.
	 * @param arg0
	 */
	public ContentHandlerFactoryException(String arg0) {
		super(arg0);
	}

	/**
	 * Constructor for ContentHandlerFactoryException.
	 * @param arg0
	 */
	public ContentHandlerFactoryException(Exception arg0) {
		super(arg0);
	}

	/**
	 * Constructor for ContentHandlerFactoryException.
	 * @param arg0
	 * @param arg1
	 */
	public ContentHandlerFactoryException(String arg0, Exception arg1) {
		super(arg0, arg1);
	}

}
