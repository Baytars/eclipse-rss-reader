package com.pnehrer.tools.morphine.ant;

import java.util.Map;
import org.xml.sax.ContentHandler;

/**
 * @author Peter Nehrer
 */
public interface ContentHandlerFactory {

	public ContentHandler create(Map properties) 
		throws ContentHandlerFactoryException;
}
