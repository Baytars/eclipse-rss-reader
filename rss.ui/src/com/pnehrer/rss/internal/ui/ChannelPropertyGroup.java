/*
 * Created on Nov 16, 2003
 * Version $Id$
 */
package com.pnehrer.rss.internal.ui;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.core.runtime.CoreException;
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
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Text;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.pnehrer.rss.core.IChannel;
import com.pnehrer.rss.core.IRegisteredTranslator;
import com.pnehrer.rss.core.RSSCore;
import com.pnehrer.rss.ui.IPageContainer;

/**
 * @author <a href="mailto:pnehrer@freeshell.org">Peter Nehrer</a>
 */
public class ChannelPropertyGroup {

    private static final IRegisteredTranslator[] NO_TRANSLATORS = {};
    private static final short URL_COMPLETE = 1;
    private static final short TRANSLATOR_COMPLETE = 2;
    private static final short COMPLETE = URL_COMPLETE | TRANSLATOR_COMPLETE;

    private final IPageContainer pageContainer;
    private URL url;
    private Text urlText;
    private Button loadButton;
    private Document document;
    private IRegisteredTranslator[] translators;
    private List translatorList;
    private String defaultTranslatorId;
    private short complete;

    public ChannelPropertyGroup(IPageContainer pageContainer) {
        this(pageContainer, null);
    }

    public ChannelPropertyGroup(
        IPageContainer pageContainer, 
        IChannel channel) {
            
        this.pageContainer = pageContainer;
        if(channel != null) {
            url = channel.getURL();
            translators = new IRegisteredTranslator[] {channel.getTranslator()};
            defaultTranslatorId = channel.getTranslator().getId();
        }
    }

    public URL getURL() {
        return url;
    }
    
    public void setURL(URL url) {
        this.url = url;
        if(urlText != null)
            urlText.setText(url == null ? "" : url.toExternalForm());
            
        if(loadButton != null)
            loadButton.setEnabled(url != null);

        setComplete(URL_COMPLETE, url != null);
    }
   
    public Document getDocument() {
        return document;
    }
    
    public IRegisteredTranslator getTranslator() {
        return translators[translatorList.getSelectionIndex()];
    }
    
    public void selectTranslator(String translatorId) {
        if(translatorList != null) {
            translatorList.deselectAll();
            setComplete(TRANSLATOR_COMPLETE, false);
            if(translatorId != null)
                for(int i = 0, n = translators.length; i < n; ++i)
                    if(translatorId.equals(translators[i].getId())) {
                        translatorList.select(i);
                        setComplete(TRANSLATOR_COMPLETE, true);
                        break;
                    }
        }
    }
    
    public void setTranslators(IRegisteredTranslator[] translators) {
        this.translators = translators;

        if(translatorList != null) {
            String[] items = new String[translators.length];
            for(int i = 0, n = items.length; i < n; ++i)
                items[i] = translators[i].getDescription();
                
            translatorList.deselectAll();
            translatorList.setItems(items);
            if(translatorList.getItemCount() > 0)
                translatorList.select(0);

            setComplete(
                TRANSLATOR_COMPLETE, 
                translatorList.getSelectionCount() == 1);
        }
    }

    public void createContents(Composite topLevel) {
        int columns = topLevel.getLayout() instanceof GridLayout ?
            ((GridLayout)topLevel.getLayout()).numColumns :
            3;

        Label label = new Label(topLevel, SWT.SINGLE);
        label.setText("URL:");

        urlText = new Text(topLevel, SWT.BORDER);
        GridData layoutData = new GridData(GridData.FILL_HORIZONTAL);
        layoutData.horizontalSpan = columns - 2;
        urlText.setLayoutData(layoutData);
        urlText.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                if(urlText.getText().trim().length() == 0) {
                    url = null;
                    setComplete(URL_COMPLETE, false);
                    pageContainer.setErrorMessage("Channel URL must be a valid URL.");
                }
                else {
                    try {
                        url = new URL(urlText.getText().trim());
                        loadButton.setEnabled(true);
                        setComplete(URL_COMPLETE, true);
                        pageContainer.setErrorMessage(null);
                    }
                    catch(MalformedURLException ex) {
                        url = null;
                        loadButton.setEnabled(false);
                        setComplete(URL_COMPLETE, false);
                        pageContainer.setErrorMessage("Channel URL must be a valid URL.");
                    }
                }
            }
        });
            
        loadButton = new Button(topLevel, SWT.PUSH | SWT.BORDER);
        layoutData = new GridData(
            GridData.HORIZONTAL_ALIGN_END | GridData.VERTICAL_ALIGN_FILL);
        layoutData.widthHint = 100;
        loadButton.setLayoutData(layoutData);
        loadButton.setText("&Load");
        loadButton.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                loadDocument();
                translatorList.setFocus();
            }
        });

        label = new Label(topLevel, SWT.SINGLE);
        layoutData = new GridData();
        layoutData.horizontalSpan = columns;
        label.setLayoutData(layoutData);
        label.setText("Select channel source translator:");
    
        translatorList = new List(
            topLevel, 
            SWT.SINGLE | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);

        layoutData = new GridData(GridData.FILL_HORIZONTAL);
        layoutData.horizontalSpan = columns;
        layoutData.widthHint = 250;
        layoutData.heightHint = 200;
        translatorList.setLayoutData(layoutData);
        translatorList.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                boolean selected = translatorList.getSelectionCount() == 1; 
                setComplete(TRANSLATOR_COMPLETE, selected);
                pageContainer.setErrorMessage(
                    selected ? 
                        null : 
                        "Select channel translator.");
            }
        });

        setURL(url);
        setTranslators(
            translators == null ? 
                new IRegisteredTranslator[0] : 
                translators);

        if(defaultTranslatorId != null) 
            selectTranslator(defaultTranslatorId);
    }
    
    public void setFocus() {
        urlText.selectAll();
        urlText.setFocus();
    }
    
    private void setComplete(short element, boolean complete) {
        if(complete) {
            this.complete |= element;
            pageContainer.setComplete(this.complete == COMPLETE);
        }
        else {
            this.complete &= ~element;
            pageContainer.setComplete(false);
        }
    }

    private void loadDocument() {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            document = builder.parse(url.openStream());
            translators = RSSCore.getPlugin().getTranslators(document);
            pageContainer.setErrorMessage(null);
        }
        catch(ParserConfigurationException ex) {
            document = null;
            pageContainer.setErrorMessage(ex.getMessage());
        }
        catch(SAXException ex) {
            document = null;
            pageContainer.setErrorMessage(ex.getMessage());
        }
        catch(IOException ex) {
            document = null;
            pageContainer.setErrorMessage(ex.getMessage());
        } 
        catch(CoreException ex) {
            translators = NO_TRANSLATORS;
            pageContainer.setErrorMessage(ex.getMessage());
        }

        String[] items = new String[translators.length];
        for(int i = 0, n = items.length; i < n; ++i)
            items[i] = translators[i].getDescription();
            
        translatorList.deselectAll();
        translatorList.setItems(items);
    }
}
