/*
 * Created on Nov 17, 2003
 * Version $Id$
 */
package com.pnehrer.rss.ui.views;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.help.browser.IBrowser;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IOpenListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.OpenEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.eclipse.ui.part.ISetSelectionTarget;
import org.eclipse.ui.part.ViewPart;

import com.pnehrer.rss.core.IChannel;
import com.pnehrer.rss.core.IItem;
import com.pnehrer.rss.ui.RSSUI;

/**
 * @author <a href="mailto:pnehrer@freeshell.org">Peter Nehrer</a>
 */
public class ChannelNavigator extends ViewPart implements ISetSelectionTarget {

    private TreeViewer viewer;

    /* (non-Javadoc)
     * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
     */
    public void createPartControl(Composite parent) {
        Tree tree = new Tree(parent, SWT.MULTI);
        tree.setLayoutData(new GridData(GridData.FILL_BOTH));
        
        viewer = new TreeViewer(tree);
        viewer.setUseHashlookup(true);
        
        viewer.addOpenListener(new IOpenListener() {
                public void open(OpenEvent event) {
                    IStructuredSelection selection = 
                        (IStructuredSelection)event.getSelection();
                    if(!selection.isEmpty()) {
                        Object element = selection.getFirstElement();
                        String url;
                        if(element instanceof IChannel)
                            url = ((IChannel)element).getLink();
                        else if(element instanceof IItem)
                            url = ((IItem)element).getLink();
                        else
                            url = null;

                        if(url != null) {                            
                            try {
                                IBrowser browser = 
                                    RSSUI.getDefault().createBrowser();
                                browser.displayURL(url);
                            }
                            catch(CoreException ex) {
                                ErrorDialog.openError(
                                    getViewSite().getShell(),
                                    "Browser Error",
                                    "Could not open browser.",
                                    ex.getStatus());
                            }
                            catch(Exception ex) {
                                MessageDialog.openError(
                                    getViewSite().getShell(),
                                    "Browser Error",
                                    "Could not open link " + url 
                                        + ". Exception: " + ex);
                            }
                        }
                    }                    
                }
            });
        
        viewer.setContentProvider(new ChannelNavigatorContentProvider());
        viewer.setLabelProvider(new WorkbenchLabelProvider());
        viewer.setInput(ResourcesPlugin.getWorkspace());

        getSite().setSelectionProvider(viewer);
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
     */
    public void setFocus() {
        viewer.getTree().setFocus();
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.part.ISetSelectionTarget#selectReveal(org.eclipse.jface.viewers.ISelection)
     */
    public void selectReveal(ISelection selection) {
        viewer.setSelection(selection, true);
    }
}
