package com.pnehrer.tools.morphine;

import javax.xml.transform.Result;
import javax.xml.transform.TransformerException;
import org.xml.sax.Attributes;

/**
 * @author Peter Nehrer
 */
public interface ResultFactory {

	public Result createResult(Attributes attrs, String content)
		throws TransformerException;
}
