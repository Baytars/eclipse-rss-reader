/*
 * Created on Mar 11, 2005
 * Version $Id$ 
 */
package com.pnehrer.rss.bugzilla;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.StringTokenizer;

import javax.xml.transform.Templates;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.pnehrer.rss.core.util.XSLBasedTranslator;

/**
 * @author pnehrer
 * 
 */
public class BugListTranslator extends XSLBasedTranslator {

	private static final String TEMPLATES = "buglist.xsl";

	private static final String RDF_ELEMENT_NS = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";

	private static final String RDF_ELEMENT = "RDF";

	private static final String RESULT_ELEMENT_NS = "http://www.bugzilla.org/rdf#";

	private static final String RESULT_ELEMENT = "result";

	private Templates templates;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.pnehrer.rss.core.ITranslator#canTranslate(org.w3c.dom.Document)
	 */
	public boolean canTranslate(Document document) {
		Element root = document.getDocumentElement();
		if (root == null || !RDF_ELEMENT.equals(root.getNodeName()))
			return false;

		NodeList results = root.getElementsByTagNameNS(RESULT_ELEMENT_NS,
				RESULT_ELEMENT);
		return results.getLength() == 1;
	}

	protected Document postProcessDocument(Document document) {
		Element channel = document.getDocumentElement();
		String link = channel.getAttribute(ATTR_LINK);
		try {
			URL url = new URL(link);
			String content = url.getQuery();
			if (content != null)
				content = URLDecoder.decode(content, "UTF-8");
			StringTokenizer t = new StringTokenizer(content, "&");
			while (t.hasMoreTokens()) {
				String[] pair = t.nextToken().split("=");
				if ("content".equals(pair[0]))
					channel.setAttribute(ATTR_TITLE, "Bugzilla query: " + pair[1]);
			}
		} catch (MalformedURLException e) {
			// ignore
		} catch (UnsupportedEncodingException e) {
			// ignore
		}

		return super.postProcessDocument(document);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.pnehrer.rss.core.util.XSLBasedTranslator#getTemplates(org.w3c.dom.Document)
	 */
	protected Templates getTemplates(Document document) throws CoreException {
		if (templates == null)
			createTemplates();

		return templates;
	}

	private synchronized void createTemplates() {
		if (templates != null)
			return;

		TransformerFactory tf = TransformerFactory.newInstance();
		try {
			templates = tf.newTemplates(new StreamSource(BugzillaRSSPlugin
					.getDefault().openStream(new Path(TEMPLATES))));
		} catch (IOException e) {
			BugzillaRSSPlugin.getDefault().log(e);
		} catch (TransformerConfigurationException e) {
			BugzillaRSSPlugin.getDefault().log(e);
		}
	}
}
