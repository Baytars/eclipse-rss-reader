/*
 * Created on Nov 14, 2003
 * Version $Id$
 */
package com.pnehrer.rss.ui.wizards;

import java.lang.reflect.InvocationTargetException;
import java.net.URL;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.eclipse.ui.dialogs.WizardNewFileCreationPage;
import org.eclipse.ui.wizards.newresource.BasicNewResourceWizard;
import org.w3c.dom.Document;

import com.pnehrer.rss.core.IRegisteredTranslator;
import com.pnehrer.rss.core.RSSCore;

/**
 * @author <a href="mailto:pnehrer@freeshell.org">Peter Nehrer</a>
 * @see Wizard
 */
public class NewChannelWizard extends Wizard implements INewWizard {
    
    private IWorkbench workbench;
    private IStructuredSelection selection;
    private WizardChannelOptionsPage channelOptionsPage;
    private WizardNewFileCreationPage newFileCreationPage;

    public NewChannelWizard() {
        setWindowTitle("New RSS Channel");
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.jface.wizard.IWizard#addPages()
     */
    public void addPages() {
        channelOptionsPage = new WizardChannelOptionsPage(
            "options",
            "Channel Options",
            null);
        channelOptionsPage.setDescription("Enter channel options.");
        addPage(channelOptionsPage);
        
        newFileCreationPage = new WizardNewFileCreationPage("file", selection);
        newFileCreationPage.setDescription("Specify channel file (*.rss).");
        newFileCreationPage.setTitle("Channel File");
        newFileCreationPage.setFileName("channel1.rss");
        addPage(newFileCreationPage);
    }

    /**
     * @see Wizard#init
     */
    public void init(IWorkbench workbench, IStructuredSelection selection) {
        this.workbench = workbench;
        this.selection = selection;
    }

	/**
	 * @see Wizard#performFinish
	 */
	public boolean performFinish() {
        final URL url = channelOptionsPage.getURL();
        final Document document = channelOptionsPage.getDocument();
        final IRegisteredTranslator translator = 
            channelOptionsPage.getTranslator();
        final Integer updateInterval = channelOptionsPage.getUpdateInterval();
        final IFile file = newFileCreationPage.createNewFile();
        setNeedsProgressMonitor(true);
        try {
            getContainer().run(false, true, new WorkspaceModifyOperation() {
                protected void execute(IProgressMonitor monitor) 
                    throws CoreException, 
                        InvocationTargetException, 
                        InterruptedException {
    
                    RSSCore.getPlugin().createChannel(
                        file, 
                        translator, 
                        document,
                        url,
                        updateInterval,
                        monitor);

                    IWorkbenchWindow[] windows = workbench.getWorkbenchWindows();
                    if(windows != null && windows.length > 0)
                        BasicNewResourceWizard.selectAndReveal(file, windows[0]);
                        
//                    IEditorRegistry registry = workbench.getEditorRegistry();
//                    IEditorDescriptor editor = registry.getDefaultEditor(file);
//                    if(editor == null) editor = registry.getDefaultEditor();
//
//                    IWorkbenchWindow[] windows = workbench.getWorkbenchWindows();
//                    if(windows != null && windows.length > 0) {
//                        windows[0].getActivePage().openEditor(
//                            file, 
//                            editor.getId(), 
//                            true);
//                    }
                }
            });
                
            return true;
        }
        catch(InterruptedException ex) {
            return false;
        } 
        catch(InvocationTargetException ex) {
            MessageDialog.openError(
                getShell(), 
                "RSS Channel Creation Error",
                "Could not create RSS channel. Exception: " + ex);
            return false;
        }
	}
}
