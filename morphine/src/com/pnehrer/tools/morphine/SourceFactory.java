package com.pnehrer.tools.morphine;

import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import org.xml.sax.Attributes;

/**
 * @author Peter Nehrer
 */
public interface SourceFactory {

	public Source createSource(Attributes attrs, String content)
		throws TransformerException;
}
