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
