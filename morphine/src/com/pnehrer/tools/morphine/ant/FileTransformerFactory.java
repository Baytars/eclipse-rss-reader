package com.pnehrer.tools.morphine.ant;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;

import org.xml.sax.ContentHandler;

import com.pnehrer.tools.morphine.FileTransformer;
import com.pnehrer.tools.morphine.ResultFactory;
import com.pnehrer.tools.morphine.SourceFactory;

/**
 * @author Peter Nehrer
 */
public class FileTransformerFactory implements ContentHandlerFactory {

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
		if(resultFactoryName == null) {
			throw new ContentHandlerFactoryException("result-factory not specified");
		}

		SourceFactory sourceFactory = null;
		ResultFactory resultFactory = null;
		Transformer transformer = null; 
		try {		
			Class sourceFactoryClass = loader.loadClass(sourceFactoryName);
			sourceFactory = (SourceFactory)sourceFactoryClass.newInstance();
		
			Class resultFactoryClass = loader.loadClass(resultFactoryName);
			resultFactory = (ResultFactory)resultFactoryClass.newInstance();
			
			String src = (String)props.remove("xsl");
			TransformerFactory tf = TransformerFactory.newInstance();
			transformer = src == null ?
				tf.newTransformer() : tf.newTransformer(new StreamSource(src));
		}
		catch(Exception ex) {
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
