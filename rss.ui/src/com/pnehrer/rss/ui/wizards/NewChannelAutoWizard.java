/*
 * Created on Oct 25, 2004
 * Version $Id$
 */
package com.pnehrer.rss.ui.wizards;

import org.eclipse.jface.wizard.Wizard;

/**
 * @author <a href="mailto:pnehrer@freeshell.org">Peter Nehrer</a>
 * @see Wizard
 */
public class NewChannelAutoWizard extends NewChannelWizard {
    
    private WizardAutoDiscoveryPage autoDiscoveryPage;

    public NewChannelAutoWizard() {
        setWindowTitle("New RSS Channel using Auto-discovery");
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.jface.wizard.IWizard#addPages()
     */
    public void addPages() {
    	autoDiscoveryPage = new WizardAutoDiscoveryPage(
    		"autodiscovery",
			"Auto-discovery",
			null);
    	autoDiscoveryPage.setDescription("Specify HTML page to examine.");
    	addPage(autoDiscoveryPage);
        
    	super.addPages();
    }
}
