/*
 * Created on Nov 18, 2003
 * Version $Id$
 */
package com.pnehrer.rss.internal.core;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

import javax.xml.transform.Templates;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.pnehrer.rss.core.RSSCore;
import com.pnehrer.rss.core.util.XSLBasedTranslator;

/**
 * @author <a href="mailto:pnehrer@freeshell.org">Peter Nehrer </a>
 */
public class DefaultTranslator extends XSLBasedTranslator {

	private static final String RSS_URI = null;

	private static final String RDF_URI = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";

	private static final String RSS09_URI = "http://my.netscape.com/rdf/simple/0.9/";

	private static final String RSS10_URI = "http://purl.org/rss/1.0/";

	private static final String ATOM_URI = "http://purl.org/atom/ns#";

	private static final String RSS_ELEMENT = "rss";

	private static final String VERSION_ATTR = "version";

	private static final String RDF_ELEMENT = "RDF";

	private static final String CHANNEL_ELEMENT = "channel";

	private static final String FEED_ELEMENT = "feed";

	private static final Collection RSS_VERSIONS = Arrays.asList(new String[] {
			"0.91", "0.92", "2.0", "2" });

	private static final String ATOM_VERSION = "0.3";

	private static final String SIMPLE_TEMPLATES = "rss-simple.xsl";

	private static final String RDF_TEMPLATES = "rss-rdf.xsl";

	private static final String ATOM_TEMPLATES = "atom.xsl";

	private Templates simpleTemplates;

	private Templates rdfTemplates;

	private Templates atomTemplates;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.pnehrer.rss.core.ITranslator#canTranslate(org.w3c.dom.Document)
	 */
	public boolean canTranslate(Document document) {
		Element element = document.getDocumentElement();
		return (RSS_ELEMENT.equals(element.getLocalName())
				&& (RSS_URI == null ? element.getNamespaceURI() == null
						: RSS_URI.equals(element.getNamespaceURI())) && RSS_VERSIONS
				.contains(element.getAttribute(VERSION_ATTR)))
				|| (RDF_ELEMENT.equals(element.getLocalName())
						&& RDF_URI.equals(element.getNamespaceURI()) && (hasRSS09Channel(element) || hasRSS10Channel(element)))
				|| (FEED_ELEMENT.equals(element.getLocalName())
						&& ATOM_URI.equals(element.getNamespaceURI()) && ATOM_VERSION
						.equals(element.getAttribute(VERSION_ATTR)));
	}

	protected Templates getTemplates(Document document) throws CoreException {
		Element element = document.getDocumentElement();
		Templates templates;
		if (RSS_ELEMENT.equals(element.getLocalName())
				&& (RSS_URI == null ? element.getNamespaceURI() == null
						: RSS_URI.equals(element.getNamespaceURI()))
				&& RSS_VERSIONS.contains(element.getAttribute(VERSION_ATTR))) {

			if (simpleTemplates == null)
				createSimpleTemplates();

			templates = simpleTemplates;
		} else if (FEED_ELEMENT.equals(element.getLocalName())
				&& ATOM_URI.equals(element.getNamespaceURI())
				&& ATOM_VERSION.equals(element.getAttribute(VERSION_ATTR))) {

			if (atomTemplates == null)
				createAtomTemplates();

			templates = atomTemplates;
		} else {
			if (rdfTemplates == null)
				createRDFTemplates();

			templates = rdfTemplates;
		}

		return templates;
	}

	private boolean hasRSS09Channel(Element element) {
		NodeList list = element.getChildNodes();
		for (int i = 0, n = list.getLength(); i < n; ++i) {
			Node node = list.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE
					&& CHANNEL_ELEMENT.equals(node.getLocalName())
					&& RSS09_URI.equals(node.getNamespaceURI()))

				return true;
		}

		return false;
	}

	private boolean hasRSS10Channel(Element element) {
		NodeList list = element.getChildNodes();
		for (int i = 0, n = list.getLength(); i < n; ++i) {
			Node node = list.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE
					&& CHANNEL_ELEMENT.equals(node.getLocalName())
					&& RSS10_URI.equals(node.getNamespaceURI()))

				return true;
		}

		return false;
	}

	private synchronized void createSimpleTemplates() throws CoreException {
		if (simpleTemplates == null) {
			try {
				TransformerFactory factory = TransformerFactory.newInstance();
				simpleTemplates = factory.newTemplates(new StreamSource(RSSCore
						.getPlugin().openStream(new Path(SIMPLE_TEMPLATES))));
			} catch (TransformerConfigurationException ex) {
				throw new CoreException(new Status(IStatus.ERROR,
						RSSCore.PLUGIN_ID, 0,
						"could not create transformation templates "
								+ SIMPLE_TEMPLATES, ex));
			} catch (IOException ex) {
				throw new CoreException(new Status(IStatus.ERROR,
						RSSCore.PLUGIN_ID, 0,
						"could not read transformation templates "
								+ SIMPLE_TEMPLATES, ex));
			}
		}
	}

	private synchronized void createRDFTemplates() throws CoreException {
		if (rdfTemplates == null) {
			try {
				TransformerFactory factory = TransformerFactory.newInstance();
				rdfTemplates = factory.newTemplates(new StreamSource(RSSCore
						.getPlugin().openStream(new Path(RDF_TEMPLATES))));
			} catch (TransformerConfigurationException ex) {
				throw new CoreException(new Status(IStatus.ERROR,
						RSSCore.PLUGIN_ID, 0,
						"could not create transformation templates "
								+ RDF_TEMPLATES, ex));
			} catch (IOException ex) {
				throw new CoreException(new Status(IStatus.ERROR,
						RSSCore.PLUGIN_ID, 0,
						"could not read transformation templates "
								+ RDF_TEMPLATES, ex));
			}
		}
	}

	private synchronized void createAtomTemplates() throws CoreException {
		if (atomTemplates == null) {
			try {
				TransformerFactory factory = TransformerFactory.newInstance();
				atomTemplates = factory.newTemplates(new StreamSource(RSSCore
						.getPlugin().openStream(new Path(ATOM_TEMPLATES))));
			} catch (TransformerConfigurationException ex) {
				throw new CoreException(new Status(IStatus.ERROR,
						RSSCore.PLUGIN_ID, 0,
						"could not create transformation templates "
								+ ATOM_TEMPLATES, ex));
			} catch (IOException ex) {
				throw new CoreException(new Status(IStatus.ERROR,
						RSSCore.PLUGIN_ID, 0,
						"could not read transformation templates "
								+ ATOM_TEMPLATES, ex));
			}
		}
	}
}