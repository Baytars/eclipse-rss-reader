/*
 * Created on Mar 11, 2005
 * Version $Id$
 */
package com.pnehrer.rss.core.util;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.pnehrer.rss.core.ITranslator;
import com.pnehrer.rss.core.RSSCore;

/**
 * @author pnehrer
 * 
 */
public abstract class XSLBasedTranslator implements ITranslator {

	protected static final String ATTR_TITLE = "title";

	protected static final String ATTR_DESCRIPTION = "description";

	protected static final String ATTR_LINK = "link";

	protected static final String ELEM_ITEM = "item";

	protected abstract Templates getTemplates(Document document)
			throws CoreException;

	protected Document preProcessDocument(Document document) {
		return document;
	}

	protected Document stripHTML(Document document) {
		Element channel = document.getDocumentElement();
		if (channel.hasAttribute(ATTR_TITLE))
			stripHTML(channel, ATTR_TITLE);

		if (channel.hasAttribute(ATTR_DESCRIPTION))
			stripHTML(channel, ATTR_DESCRIPTION);

		NodeList list = channel.getElementsByTagName(ELEM_ITEM);
		for (int i = 0, n = list.getLength(); i < n; ++i) {
			Element element = (Element) list.item(i);
			if (element.hasAttribute(ATTR_TITLE))
				stripHTML(element, ATTR_TITLE);

			if (element.hasAttribute(ATTR_DESCRIPTION))
				stripHTML(element, ATTR_DESCRIPTION);
		}

		return document;
	}

	protected void stripHTML(Element element, String attribute) {
		String text = element.getAttribute(attribute);
		try {
			String result = HTMLHelper.stripHTML(text);
			element.setAttribute(attribute, result);
		} catch (IOException e) {
			// ignore
		}
	}

	protected Document postProcessDocument(Document document) {
		return resolveRelativeLinks(stripHTML(document));
	}

	protected Document resolveRelativeLinks(Document document) {
		Element channel = document.getDocumentElement();
		URI baseUri;
		if (channel.hasAttribute(ATTR_LINK))
			try {
				baseUri = new URI(channel.getAttribute(ATTR_LINK));
			} catch (URISyntaxException e) {
				baseUri = null;
			}
		else
			baseUri = null;

		if (baseUri != null) {
			NodeList list = channel.getElementsByTagName(ELEM_ITEM);
			for (int i = 0, n = list.getLength(); i < n; ++i) {
				Element element = (Element) list.item(i);
				if (element.hasAttribute(ATTR_LINK)) {
					String link = element.getAttribute(ATTR_LINK);
					URI itemUri;
					try {
						itemUri = new URI(link);
					} catch (URISyntaxException e) {
						continue;
					}

					URI resolvedUri = baseUri.resolve(itemUri);
					element.setAttribute(ATTR_LINK, resolvedUri.toString());
				}
			}
		}

		return document;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.pnehrer.rss.core.ITranslator#translate(org.w3c.dom.Document)
	 */
	public Document translate(Document document) throws CoreException {
		Templates templates = getTemplates(document);
		if (templates == null)
			throw new CoreException(new Status(IStatus.ERROR,
					RSSCore.PLUGIN_ID, 0,
					"could not obtain stylesheet template", null));

		DOMResult result = new DOMResult();
		try {
			Transformer transformer = templates.newTransformer();
			transformer.transform(new DOMSource(preProcessDocument(document)),
					result);
		} catch (TransformerException ex) {
			throw new CoreException(new Status(IStatus.ERROR,
					RSSCore.PLUGIN_ID, 0, "could not translate channel source",
					ex));
		}

		return postProcessDocument((Document) result.getNode());
	}
}
