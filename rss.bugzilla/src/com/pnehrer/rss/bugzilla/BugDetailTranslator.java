/*
 * Created on Mar 11, 2005
 * Version $Id$
 */
package com.pnehrer.rss.bugzilla;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

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
public class BugDetailTranslator extends XSLBasedTranslator {

	private static final String TEMPLATES = "bugdetail.xsl";

	private static final String ROOT_ELEMENT = "bugzilla";

	private static final String ATTR_DATE = "date";

	private static final DateFormat dateFormat;

	static {
		dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
	}

	private Templates templates;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.pnehrer.rss.core.ITranslator#canTranslate(org.w3c.dom.Document)
	 */
	public boolean canTranslate(Document document) {
		Element root = document.getDocumentElement();
		return root != null && ROOT_ELEMENT.equals(root.getNodeName());
	}

	protected Document postProcessDocument(Document document) {
		Element channel = document.getDocumentElement();
		String dateStr = channel.getAttribute(ATTR_DATE);
		if (dateStr != null) {
			try {
				channel.setAttribute(ATTR_DATE, DateFormat.getInstance()
						.format(dateFormat.parse(dateStr)));
			} catch (ParseException e) {
				// ignore
			}
		}

		NodeList items = channel.getElementsByTagName(ELEM_ITEM);
		for (int i = 0, n = items.getLength(); i < n; ++i) {
			Element item = (Element) items.item(i);
			dateStr = item.getAttribute(ATTR_DATE);
			if (dateStr != null) {
				try {
					item.setAttribute(ATTR_DATE, DateFormat.getInstance()
							.format(dateFormat.parse(dateStr)));
				} catch (ParseException e) {
					// ignore
				}
			}
		}

		return super.postProcessDocument(document);
	}

	/* (non-Javadoc)
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
