/*
 * Created on Nov 19, 2003
 * Version $Id$
 */
package com.pnehrer.rss.ui.wizards;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.resource.ImageDescriptor;
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
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Text;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.pnehrer.rss.core.IRegisteredTranslator;
import com.pnehrer.rss.core.RSSCore;

/**
 * @author <a href="mailto:pnehrer@freeshell.org">Peter Nehrer</a>
 */
public class WizardChannelOptionsPage extends WizardPage {

    private static final IRegisteredTranslator[] NO_TRANSLATORS = {};
    private static final short URL_COMPLETE = 1;
    private static final short TRANSLATOR_COMPLETE = 2;
    private static final short UPDATE_INTERVAL_COMPLETE = 4;
    private static final short PAGE_COMPLETE =
        URL_COMPLETE
        + TRANSLATOR_COMPLETE
        + UPDATE_INTERVAL_COMPLETE;

    private URL url;
    private Text urlText;
    private Button loadButton;
    private Document document;
    private IRegisteredTranslator[] translators;
    private List translatorList;
    private Integer updateInterval;
    private Text updateIntervalText;
    private short pageComplete;

    /**
     * @param pageName
     * @param title
     * @param titleImage
     */
    public WizardChannelOptionsPage(
        String pageName,
        String title,
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

        Label label = new Label(topLevel, SWT.SINGLE);
        label.setLayoutData(new GridData());
        label.setText("URL:");

        urlText = new Text(topLevel, SWT.BORDER);
        urlText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        urlText.addModifyListener(new ModifyListener() {
                public void modifyText(ModifyEvent e) {
                    if(urlText.getText().trim().length() == 0) {
                        url = null;
                        setIncomplete(URL_COMPLETE);
                    }
                    else {
                        try {
                            url = new URL(urlText.getText().trim());
                            loadButton.setEnabled(true);
                            setComplete(URL_COMPLETE);
                        }
                        catch(MalformedURLException ex) {
                            url = null;
                            loadButton.setEnabled(false);
                            setIncomplete(URL_COMPLETE);
                        }
                    }
                }
            });

        loadButton = new Button(topLevel, SWT.PUSH | SWT.BORDER);
        loadButton.setLayoutData(
            new GridData(
                GridData.HORIZONTAL_ALIGN_END | GridData.VERTICAL_ALIGN_FILL));
        loadButton.setText("&Load");
        loadButton.addSelectionListener(new SelectionAdapter() {
                public void widgetSelected(SelectionEvent e) {
                    loadDocument();
                }
            });
        
        loadButton.setEnabled(false);

        label = new Label(topLevel, SWT.SINGLE);
        GridData layoutData = new GridData();
        layoutData.horizontalSpan = 3;
        label.setLayoutData(layoutData);
        label.setText("Select channel source translator:");
        
        translatorList = new List(topLevel, SWT.SINGLE | SWT.BORDER);
        layoutData = new GridData(GridData.FILL_BOTH);
        layoutData.horizontalSpan = 3;
        translatorList.setLayoutData(layoutData);
        translatorList.addSelectionListener(new SelectionAdapter() {
                public void widgetSelected(SelectionEvent e) {
                    if(translatorList.getSelectionCount() == 1)
                        setComplete(TRANSLATOR_COMPLETE);
                    else
                        setIncomplete(TRANSLATOR_COMPLETE);
                }
            });
            
        label = new Label(topLevel, SWT.SINGLE);
        label.setText("Update interval (minutes):");

        updateIntervalText = new Text(topLevel, SWT.SINGLE | SWT.BORDER);
        layoutData = new GridData(GridData.FILL_HORIZONTAL);
        layoutData.horizontalSpan = 2;
        updateIntervalText.setLayoutData(layoutData);
        updateIntervalText.addModifyListener(new ModifyListener() {
                public void modifyText(ModifyEvent e) {
                    if(updateIntervalText.getText().trim().length() == 0) {
                        updateInterval = null;
                        setIncomplete(UPDATE_INTERVAL_COMPLETE);
                    }
                    else {
                        try {
                            updateInterval = new Integer(
                                updateIntervalText.getText());
                            if(updateInterval.intValue() >= 0) {
                                setComplete(UPDATE_INTERVAL_COMPLETE);
                            }
                            else {
                                updateInterval = null;
                                setIncomplete(UPDATE_INTERVAL_COMPLETE);
                            }
                        }
                        catch(NumberFormatException ex) {
                            updateInterval = null;
                            setIncomplete(UPDATE_INTERVAL_COMPLETE);
                        }
                    }
                }
            });
        
        setErrorMessage(null);
        setMessage(null);
        setControl(topLevel);
    }

    public URL getURL() {
        return url;
    }
    
    public Document getDocument() {
        return document;
    }
    
    public IRegisteredTranslator getTranslator() {
        return translators[translatorList.getSelectionIndex()];
    }
    
    public Integer getUpdateInterval() {
        return updateInterval = new Integer(updateIntervalText.getText());
    }

    private void loadDocument() {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            document = builder.parse(url.openStream());
            translators = RSSCore.getPlugin().getTranslators(document);
            setErrorMessage(null);
        }
        catch(ParserConfigurationException ex) {
            document = null;
            setErrorMessage(ex.getMessage());
        }
        catch(SAXException ex) {
            document = null;
            setErrorMessage(ex.getMessage());
        }
        catch(IOException ex) {
            document = null;
            setErrorMessage(ex.getMessage());
        } 
        catch(CoreException ex) {
            translators = NO_TRANSLATORS;
            setErrorMessage(ex.getMessage());
        }

        String[] items = new String[translators.length];
        for(int i = 0, n = items.length; i < n; ++i)
            items[i] = translators[i].getDescription();
            
        translatorList.deselectAll();
        translatorList.setItems(items);
        setIncomplete(TRANSLATOR_COMPLETE);
    }
    
    private void setComplete(short bit) {
        pageComplete |= bit;
        setPageComplete(pageComplete == PAGE_COMPLETE);
    }
    
    private void setIncomplete(short bit) {
        pageComplete &= ~bit;
        setPageComplete(false);
    }
}
