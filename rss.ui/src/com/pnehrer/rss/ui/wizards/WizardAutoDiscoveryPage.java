/*
 * Created on Oct 25, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.pnehrer.rss.ui.wizards;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.StringTokenizer;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.htmlparser.Node;
import org.htmlparser.Parser;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.lexer.Page;
import org.htmlparser.tags.BaseHrefTag;
import org.htmlparser.tags.Tag;
import org.htmlparser.util.LinkProcessor;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;
import org.htmlparser.util.SimpleNodeIterator;

/**
 * @author pnehrer
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class WizardAutoDiscoveryPage extends WizardPage {

	private static final String RSS_TYPE = "application/rss+xml";
	private static final String RDF_TYPE = "application/rdf+xml";
	private static final String APP_TYPE = "application/xml";
	private static final String XML_TYPE = "text/xml";
	private static final String ATOM_TYPE = "application/atom+xml";
	private static final String[] RSS_RELS = {"alternate"};
	private static final String[] ATOM_RELS = {"alternate", "service.feed"};
	
	private Text urlText;
	private TableViewer feedList;
	
	/**
	 * @param pageName
	 * @param title
	 * @param titleImage
	 */
	public WizardAutoDiscoveryPage(String pageName, String title,
			ImageDescriptor titleImage) {
		super(pageName, title, titleImage);
		setPageComplete(false);
	}

    /* (non-Javadoc)
     * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
     */
    public void createControl(Composite parent) {
        initializeDialogUnits(parent);
        Composite topLevel = new Composite(parent,SWT.NONE);
        topLevel.setLayout(new GridLayout(3, false));
        topLevel.setLayoutData(new GridData(
            GridData.VERTICAL_ALIGN_FILL | GridData.HORIZONTAL_ALIGN_FILL));
        topLevel.setFont(parent.getFont());

        Label label = new Label(topLevel, SWT.LEFT);
        label.setText("Web Page URL:");
        label.setLayoutData(
        	new GridData(
        		GridData.HORIZONTAL_ALIGN_END | GridData.VERTICAL_ALIGN_CENTER));
        label.setFont(parent.getFont());
        
        urlText = new Text(topLevel, SWT.SINGLE | SWT.BORDER);
        GridData gd =
        	new GridData(
        		GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_CENTER);
        gd.widthHint = 250;
        urlText.setLayoutData(gd);
        urlText.setFont(parent.getFont());
        
        final Button loadButton = new Button(topLevel, SWT.PUSH | SWT.BORDER);
        loadButton.setText("&Load");
        gd = 
        	new GridData(
        		GridData.HORIZONTAL_ALIGN_END | GridData.VERTICAL_ALIGN_FILL);
        gd.widthHint = 100;
        loadButton.setLayoutData(gd);
        loadButton.setFont(parent.getFont());
        loadButton.setEnabled(false);
        loadButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				try {
					Parser parser = new Parser(urlText.getText().trim());
					Page page = parser.getLexer().getPage();
					String pageUrl = page.getUrl();
					Node[] bases = parser.extractAllNodesThatAre(BaseHrefTag.class);
					if (bases != null && bases.length > 0)
						((BaseHrefTag)bases[0]).doSemanticAction();

					parser.reset();
					NodeList nodes = 
						parser.extractAllNodesThatMatch(
							new TagNameFilter("link"));
					LinkProcessor linkProcessor = page.getLinkProcessor();
					ArrayList list = new ArrayList();
					for (SimpleNodeIterator i = nodes.elements(); i.hasMoreNodes();) {
						Tag tag = (Tag)i.nextNode();
						String type = tag.getAttribute("type");
						if (RSS_TYPE.equalsIgnoreCase(type) 
							|| RDF_TYPE.equalsIgnoreCase(type)
							|| APP_TYPE.equalsIgnoreCase(type)
							|| XML_TYPE.equalsIgnoreCase(type)) {

							if (!containsKeyword(tag.getAttribute("rel"), RSS_RELS))
								continue;
						}
						else if (ATOM_TYPE.equalsIgnoreCase(type)) {
							if (!containsKeyword(tag.getAttribute("rel"), ATOM_RELS))
								continue;
						}
						else
							continue;

						String href = tag.getAttribute("href"); 
						list.add(
							new Link(
								tag.getAttribute("title"), 
								linkProcessor.extract(href, pageUrl)));
					}
					
					feedList.setInput(list.toArray());
					feedList.getTable().setEnabled(true);
					setErrorMessage(null);
					feedList.getTable().setFocus();
				}
				catch (ParserException ex) {
					setErrorMessage(ex.getLocalizedMessage());
					feedList.getTable().setEnabled(false);
					urlText.setFocus();
				}
			}
		});

        urlText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				if (LinkProcessor.isURL(urlText.getText().trim())) {
					setErrorMessage(null);
					loadButton.setEnabled(true);
				}
				else {
					setErrorMessage("Please enter a valid URL.");
					loadButton.setEnabled(false);
				}
			}
		});
        
        label = new Label(topLevel, SWT.LEFT);
        label.setText("Auto-discovered feeds:");
        gd = new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_CENTER);
        gd.horizontalSpan = 3;
        label.setLayoutData(gd);
        label.setFont(parent.getFont());
        
        Table table = 
        	new Table(
        		topLevel, 
				SWT.SINGLE | SWT.FULL_SELECTION | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
        gd = new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING);
        gd.horizontalSpan = 3;
        gd.heightHint = 200;
        table.setLayoutData(gd);
        table.setFont(parent.getFont());
        
        feedList = new TableViewer(table);
        feedList.setUseHashlookup(true);
        feedList.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				setPageComplete(!feedList.getSelection().isEmpty());
			}
		});
        
        feedList.setContentProvider(new ArrayContentProvider());
        feedList.setLabelProvider(new LabelProvider());
            
        setErrorMessage(null);
        setMessage(null);
        setControl(topLevel);
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.dialogs.IDialogPage#setVisible(boolean)
     */
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        if(visible)
            urlText.setFocus();
    }
    
    /* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.WizardPage#getNextPage()
	 */
	public IWizardPage getNextPage() {
		IWizardPage nextPage = super.getNextPage();
		if (nextPage instanceof WizardChannelOptionsPage) {
			WizardChannelOptionsPage channelOptionsPage = 
				(WizardChannelOptionsPage)nextPage;
			Link link = (Link)
				((IStructuredSelection)feedList.getSelection()).getFirstElement();
			try {
				channelOptionsPage.setURL(new URL(link.url));
			} 
			catch (MalformedURLException e) {
				MessageDialog.openError(
					getShell(), 
					"Auto-detection error", 
					e.getLocalizedMessage());
				return this;
			}
		}
		
		return nextPage;
	}
    
    private class Link {
    	
    	private final String title;
    	
    	private final String url;
    	
    	public Link(String title, String url) {
    		this.title = title;
    		this.url = url;
    	}
    	
    	public boolean equals(Object other) {
    		if (other instanceof Link)
    			return url.equals(((Link)other).url);
    		else
    			return false;
    	}
    	
    	public int hashCode() {
    		return url.hashCode();
    	}
    	
    	public String toString() {
    		return title == null ? url : title;
    	}
    }

    private static boolean containsKeyword(String list, String[] keywords) {   	
		StringTokenizer t = new StringTokenizer(list, " ");
		while (t.hasMoreTokens()) {
			String token = t.nextToken();
			for (int i = 0; i < keywords.length; ++i) 
				if (keywords[i].equalsIgnoreCase(token))
					return true;
		}
			
		return false;
    }
}
