package com.pnehrer.tools.morphine;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

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
			this.attrs = attrs;
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
				throw new SAXException(ex);
			}				
		}		
	}
}
