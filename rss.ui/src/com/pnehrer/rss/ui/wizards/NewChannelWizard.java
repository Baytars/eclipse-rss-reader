/*
 * Created on Nov 14, 2003
 * Version $Id$
 */
package com.pnehrer.rss.ui.wizards;

import java.lang.reflect.InvocationTargetException;
import java.net.URL;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.eclipse.ui.dialogs.ContainerGenerator;
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
        setNeedsProgressMonitor(true);
        try {
            getContainer().run(false, true, new WorkspaceModifyOperation() {
                protected void execute(IProgressMonitor monitor) 
                    throws CoreException, 
                        InvocationTargetException, 
                        InterruptedException {

//                    final IFile[] files = new IFile[1];
//                    Display.getDefault().syncExec(new Runnable() {
//                        public void run() {
//                            files[0] = newFileCreationPage.createNewFile();    
//                        }
//                    });
//
//                    RSSCore.getPlugin().createChannel(
//                        files[0], 
//                        translator, 
//                        document,
//                        url,
//                        updateInterval,
//                        monitor);

                    if(monitor != null)
                        monitor.beginTask("Creating Channel...", 2);
                        
                    try {
                        IPath containerPath = 
                            newFileCreationPage.getContainerFullPath();
                        ContainerGenerator generator = 
                            new ContainerGenerator(containerPath);
                        IContainer container = generator.generateContainer(
                            monitor == null ?
                                null :
                                new SubProgressMonitor(monitor, 1));
    
                        IFile file = container.getFile(
                            new Path(newFileCreationPage.getFileName()));
                        RSSCore.getPlugin().createChannel(
                            file, 
                            translator, 
                            document,
                            url,
                            updateInterval,
                            monitor == null ?
                                null :
                                new SubProgressMonitor(monitor, 1));

//                        Display.getDefault().asyncExec(new Runnable() {
//                            public void run() {    
                        IWorkbenchWindow window = 
                            workbench.getActiveWorkbenchWindow();
                        if(window != null)
                            BasicNewResourceWizard.selectAndReveal(
                                file, 
                                window);
//                            }
//                        });
                            
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
                    finally {
                        if(monitor != null)
                            monitor.done();
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
                "RSS Channel Creation Error",
                "Could not create RSS channel. Exception: " + ex);
            return false;
        }
	}
}
