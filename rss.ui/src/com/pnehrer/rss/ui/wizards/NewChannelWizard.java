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
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorRegistry;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.eclipse.ui.dialogs.WizardNewFileCreationPage;

import com.pnehrer.rss.core.RSSCore;

/**
 * @author <a href="mailto:pnehrer@freeshell.org">Peter Nehrer</a>
 * @see Wizard
 */
public class NewChannelWizard extends Wizard implements INewWizard {
    
    private IWorkbench workbench;
    private IStructuredSelection selection;
    private WizardNewChannelPage newChannelPage;
    private WizardNewFileCreationPage newFilePage;
    
    public NewChannelWizard() {
        setWindowTitle("New RSS Feed");
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.jface.wizard.IWizard#addPages()
     */
    public void addPages() {
        newChannelPage = new WizardNewChannelPage("channel", "RSS Feed URL", null);
        addPage(newChannelPage);
        
        newFilePage = new WizardNewFileCreationPage("file", selection);
        addPage(newFilePage);
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
        final URL url = newChannelPage.getURL();
        final IFile file = newFilePage.createNewFile();
        setNeedsProgressMonitor(true);
        try {
            getContainer().run(false, true, new WorkspaceModifyOperation() {
                    protected void execute(IProgressMonitor monitor) 
                        throws CoreException, 
                        InvocationTargetException, 
                        InterruptedException {
        
                        RSSCore.getPlugin().download(
                            url, 
                            file, 
                            monitor);
                            
                        IEditorRegistry registry = workbench.getEditorRegistry();
                        IEditorDescriptor editor = registry.getDefaultEditor(file);
                        if(editor == null) editor = registry.getDefaultEditor();

                        IWorkbenchWindow[] windows = workbench.getWorkbenchWindows();
                        if(windows != null && windows.length > 0) {
                            windows[0].getActivePage().openEditor(
                                file, 
                                editor.getId(), 
                                true);
                        }
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
                "RSS Feed Creation Error",
                "Could not create RSS feed. Exception: " + ex);
            return false;
        }
	}
}
