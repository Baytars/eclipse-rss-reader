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
import java.text.DateFormat;
import java.util.Date;
import java.util.Properties;
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
		channel.setAttribute("date", DateFormat.getInstance()
				.format(new Date()));
		String link = channel.getAttribute(ATTR_LINK);
		if (link != null) {
			link = link.replaceAll("&ctype=rdf", "");
			channel.setAttribute(ATTR_LINK, link);
		}
		
		try {
			URL url = new URL(link);
			String query = url.getQuery();
			if (query != null) {
				Properties params = new Properties();
				StringTokenizer t = new StringTokenizer(query, "&");
				while (t.hasMoreTokens()) {
					String[] pair = t.nextToken().split("=");
					if (pair.length < 2)
						continue;
					
					if ("bug_status".equals(pair[0])
							|| "product".equals(pair[0])
							|| "content".equals(pair[0]))
						try {
							params.put(pair[0], URLDecoder.decode(pair[1], "UTF-8"));
						} catch (UnsupportedEncodingException ex) {
							continue;
						}
						
					if ("title".equals(pair[0]))
						try {
							channel.setAttribute(ATTR_TITLE, URLDecoder.decode(
									pair[1], "UTF-8"));
						} catch (UnsupportedEncodingException ex) {
							continue;
						}
				}

				StringBuffer buf = new StringBuffer("Bugzilla query");
				if (!params.isEmpty())
					buf.append(" (");
				
				String content = params.getProperty("content");
				if (content != null) {
					buf.append("content=").append(content);
					if (params.size() > 1)
						buf.append(", ");
				}
				
				String product = params.getProperty("product");
				if (product != null) {
					buf.append("product=").append(product);
					if ((content == null && params.size() > 1)
							|| params.size() > 2)
						buf.append(", ");
				}
				
				String status = params.getProperty("bug_status");
				if (status != null) {
					if ("__all__".equals(status))
						status = "all";
					else if ("__open__".equals(status))
						status = "open";
					else if ("__closed__".equals(status))
						status = "closed";
					
					buf.append("status=").append(status);
				}
				
				if (!params.isEmpty())
					buf.append(')');

				buf.append(" at ");
				buf.append(channel.getAttribute(ATTR_DESCRIPTION));
				channel.setAttribute(ATTR_DESCRIPTION, buf.toString());
			}
		} catch (MalformedURLException e) {
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
