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
    
    public Object createTransformerElement() {
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
				reader.setProperty(
					"http://xml.org/sax/properties/lexical-handler",
					multiplexor);
			}
			else if(transformerElement != null) {
				TransformerHandler th = transformerElement.makeTransformerHandler();
				reader.setContentHandler(th);
				reader.setDTDHandler(th);
				reader.setProperty(
					"http://xml.org/sax/properties/lexical-handler", th);
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
