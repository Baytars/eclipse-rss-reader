package com.pnehrer.tools.morphine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

/**
 * @author Peter Nehrer
 */
public class EventMultiplexor implements ContentHandler,
	LexicalHandler, DTDHandler {
	
	private List contentHandlers = new ArrayList();
	private List lexicalHandlers = new ArrayList();
	private List dtdHandlers = new ArrayList();
	
	public void addContentHandler(ContentHandler contentHandler) {
		contentHandlers.add(contentHandler);
	}
	
	public void removeContentHandler(ContentHandler contentHandler) {
		contentHandlers.remove(contentHandler);
	}
	
	public List getContentHandlers() {
		return Collections.unmodifiableList(contentHandlers);
	}

	public void addLexicalHandler(LexicalHandler lexicalHandler) {
		lexicalHandlers.add(lexicalHandler);
	}
	
	public void removeLexicalHandler(LexicalHandler lexicalHandler) {
		lexicalHandlers.remove(lexicalHandler);
	}
	
	public List getLexicalHandlers() {
		return Collections.unmodifiableList(lexicalHandlers);
	}

	public void addDTDHandler(DTDHandler dtdHandler) {
		dtdHandlers.add(dtdHandler);
	}
	
	public void removeDTDHandler(DTDHandler dtdHandler) {
		dtdHandlers.remove(dtdHandler);
	}
	
	public List getDTDHandlers() {
		return Collections.unmodifiableList(dtdHandlers);
	}
	
	/**
	 * @see org.xml.sax.ContentHandler#setDocumentLocator(org.xml.sax.Locator)
	 */
	public void setDocumentLocator(Locator arg0) {
		for(Iterator i = contentHandlers.iterator(); i.hasNext();) {
			ContentHandler item = (ContentHandler)i.next();
			item.setDocumentLocator(arg0);
		}
	}

	/**
	 * @see org.xml.sax.ContentHandler#startDocument()
	 */
	public void startDocument() throws SAXException {
		for(Iterator i = contentHandlers.iterator(); i.hasNext();) {
			ContentHandler item = (ContentHandler)i.next();
			item.startDocument();
		}
	}

	/**
	 * @see org.xml.sax.ContentHandler#endDocument()
	 */
	public void endDocument() throws SAXException {
		for(Iterator i = contentHandlers.iterator(); i.hasNext();) {
			ContentHandler item = (ContentHandler)i.next();
			item.endDocument();
		}
	}

	/**
	 * @see org.xml.sax.ContentHandler#startPrefixMapping(java.lang.String, java.lang.String)
	 */
	public void startPrefixMapping(String arg0, String arg1)
		throws SAXException {
		for(Iterator i = contentHandlers.iterator(); i.hasNext();) {
			ContentHandler item = (ContentHandler)i.next();
			item.startPrefixMapping(arg0, arg1);
		}
	}

	/**
	 * @see org.xml.sax.ContentHandler#endPrefixMapping(java.lang.String)
	 */
	public void endPrefixMapping(String arg0) throws SAXException {
		for(Iterator i = contentHandlers.iterator(); i.hasNext();) {
			ContentHandler item = (ContentHandler)i.next();
			item.endPrefixMapping(arg0);
		}
	}

	/**
	 * @see org.xml.sax.ContentHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
	 */
	public void startElement(String arg0, String arg1, String arg2, 
		Attributes arg3) throws SAXException {
			
		for(Iterator i = contentHandlers.iterator(); i.hasNext();) {
			ContentHandler item = (ContentHandler)i.next();
			item.startElement(arg0, arg1, arg2, arg3);
		}
		
	}

	/**
	 * @see org.xml.sax.ContentHandler#endElement(java.lang.String, java.lang.String, java.lang.String)
	 */
	public void endElement(String arg0, String arg1, String arg2)
		throws SAXException {

		for(Iterator i = contentHandlers.iterator(); i.hasNext();) {
			ContentHandler item = (ContentHandler)i.next();
			item.endElement(arg0, arg1, arg2);
		}
	}

	/**
	 * @see org.xml.sax.ContentHandler#characters(char, int, int)
	 */
	public void characters(char[] arg0, int arg1, int arg2)
		throws SAXException {

		for(Iterator i = contentHandlers.iterator(); i.hasNext();) {
			ContentHandler item = (ContentHandler)i.next();
			item.characters(arg0, arg1, arg2);
		}
	}

	/**
	 * @see org.xml.sax.ContentHandler#ignorableWhitespace(char, int, int)
	 */
	public void ignorableWhitespace(char[] arg0, int arg1, int arg2)
		throws SAXException {

		for(Iterator i = contentHandlers.iterator(); i.hasNext();) {
			ContentHandler item = (ContentHandler)i.next();
			item.ignorableWhitespace(arg0, arg1, arg2);
		}
	}

	/**
	 * @see org.xml.sax.ContentHandler#processingInstruction(java.lang.String, java.lang.String)
	 */
	public void processingInstruction(String arg0, String arg1)
		throws SAXException {

		for(Iterator i = contentHandlers.iterator(); i.hasNext();) {
			ContentHandler item = (ContentHandler)i.next();
			item.processingInstruction(arg0, arg1);
		}
	}

	/**
	 * @see org.xml.sax.ContentHandler#skippedEntity(java.lang.String)
	 */
	public void skippedEntity(String arg0) throws SAXException {
		for(Iterator i = contentHandlers.iterator(); i.hasNext();) {
			ContentHandler item = (ContentHandler)i.next();
			item.skippedEntity(arg0);
		}
	}

	/**
	 * @see org.xml.sax.ext.LexicalHandler#comment(char, int, int)
	 */
	public void comment(char[] arg0, int arg1, int arg2) throws SAXException {
		for(Iterator i = lexicalHandlers.iterator(); i.hasNext();) {
			LexicalHandler item = (LexicalHandler)i.next();
			item.comment(arg0, arg1, arg2);
		}
	}

	/**
	 * @see org.xml.sax.ext.LexicalHandler#endCDATA()
	 */
	public void endCDATA() throws SAXException {
		for(Iterator i = lexicalHandlers.iterator(); i.hasNext();) {
			LexicalHandler item = (LexicalHandler)i.next();
			item.endCDATA();
		}
	}

	/**
	 * @see org.xml.sax.ext.LexicalHandler#endDTD()
	 */
	public void endDTD() throws SAXException {
		for(Iterator i = lexicalHandlers.iterator(); i.hasNext();) {
			LexicalHandler item = (LexicalHandler)i.next();
			item.endDTD();
		}
	}

	/**
	 * @see org.xml.sax.ext.LexicalHandler#endEntity(java.lang.String)
	 */
	public void endEntity(String arg0) throws SAXException {
		for(Iterator i = lexicalHandlers.iterator(); i.hasNext();) {
			LexicalHandler item = (LexicalHandler)i.next();
			item.endEntity(arg0);
		}
	}

	/**
	 * @see org.xml.sax.ext.LexicalHandler#startCDATA()
	 */
	public void startCDATA() throws SAXException {
		for(Iterator i = lexicalHandlers.iterator(); i.hasNext();) {
			LexicalHandler item = (LexicalHandler)i.next();
			item.startCDATA();
		}
	}

	/**
	 * @see org.xml.sax.ext.LexicalHandler#startDTD(java.lang.String, java.lang.String, java.lang.String)
	 */
	public void startDTD(String arg0, String arg1, String arg2)
		throws SAXException {

		for(Iterator i = lexicalHandlers.iterator(); i.hasNext();) {
			LexicalHandler item = (LexicalHandler)i.next();
			item.startDTD(arg0, arg1, arg2);
		}
	}

	/**
	 * @see org.xml.sax.ext.LexicalHandler#startEntity(java.lang.String)
	 */
	public void startEntity(String arg0) throws SAXException {
		for(Iterator i = lexicalHandlers.iterator(); i.hasNext();) {
			LexicalHandler item = (LexicalHandler)i.next();
			item.startEntity(arg0);
		}
	}

	/**
	 * @see org.xml.sax.DTDHandler#notationDecl(java.lang.String, java.lang.String, java.lang.String)
	 */
	public void notationDecl(String arg0, String arg1, String arg2)
		throws SAXException {

		for(Iterator i = dtdHandlers.iterator(); i.hasNext();) {
			DTDHandler item = (DTDHandler)i.next();
			item.notationDecl(arg0, arg1, arg2);
		}
	}

	/**
	 * @see org.xml.sax.DTDHandler#unparsedEntityDecl(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	public void unparsedEntityDecl(String arg0, String arg1, String arg2, 
		String arg3) throws SAXException {

		for(Iterator i = dtdHandlers.iterator(); i.hasNext();) {
			DTDHandler item = (DTDHandler)i.next();
			item.unparsedEntityDecl(arg0, arg1, arg2, arg3);
		}
	}
}
