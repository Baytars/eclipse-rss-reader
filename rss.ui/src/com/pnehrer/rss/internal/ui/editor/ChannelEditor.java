/*
 * Created on Mar 17, 2004
 * Version $Id$
 */
package com.pnehrer.rss.internal.ui.editor;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;
import org.eclipse.update.ui.forms.internal.FormWidgetFactory;
import org.eclipse.update.ui.forms.internal.HyperlinkAdapter;
import org.eclipse.update.ui.forms.internal.HyperlinkSettings;

import com.pnehrer.rss.core.IChannel;
import com.pnehrer.rss.core.IItem;
import com.pnehrer.rss.core.IRSSElement;
import com.pnehrer.rss.core.RSSCore;
import com.pnehrer.rss.ui.ILinkBrowser;
import com.pnehrer.rss.ui.RSSUI;

/**
 * @author <a href="mailto:pnehrer@freeshell.org">Peter Nehrer</a>
 * @see EditorPart
 */
public class ChannelEditor extends EditorPart implements IResourceChangeListener {

    private ScrolledComposite scrollable;
    private Color background;
    private Image image;
    private FormWidgetFactory factory;
    private Font titleFont;
    private Image image16x16;
    private IChannel channel;
    private boolean dirty;

	/**
	 * @see EditorPart#createPartControl
	 */
	public void createPartControl(Composite parent) {
        background = new Color(Display.getCurrent(), 255, 255, 255);

        ImageDescriptor descriptor = RSSUI.getDefault().getImageDescriptor(channel);
        if(descriptor != null)
            image = descriptor.createImage();

        factory = new FormWidgetFactory();
        factory.setHyperlinkUnderlineMode(HyperlinkSettings.UNDERLINE_ROLLOVER);

        titleFont = parent.getFont();
        FontData[] fd = titleFont.getFontData();
        titleFont = 
            new Font(
                parent.getDisplay(), 
                fd[0].getName(), 
                fd[0].getHeight(), 
                fd[0].getStyle() | SWT.BOLD);
        
        image16x16 = RSSUI.getDefault().getImageDescriptor16x16(channel).createImage();
                    
        scrollable = new ScrolledComposite(parent, SWT.V_SCROLL | SWT.H_SCROLL);
        scrollable.setFont(parent.getFont());
        scrollable.setExpandVertical(true);
        scrollable.setExpandHorizontal(true);

        createContent();
	}
    
    private void createContent() {
        Composite composite = new Composite(scrollable, SWT.NONE);
        composite.setLayoutData(new GridData(GridData.FILL_BOTH));
        composite.setLayout(new GridLayout());
        composite.setFont(scrollable.getFont());
        composite.setBackground(background);

        CLabel title = new CLabel(composite, SWT.SHADOW_NONE | SWT.WRAP);
        title.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING | GridData.VERTICAL_ALIGN_BEGINNING));
        title.setImage(image);
        title.setFont(JFaceResources.getBannerFont());
        title.setText(channel.getTitle());
        
        factory.turnIntoHyperlink(title, new LinkOpener(channel));
    
        Label description = new Label(composite, SWT.WRAP);
        description.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING));
        description.setBackground(background);
        if(channel.getDescription() != null)
            description.setText(channel.getDescription());        
        
        Label spacer = new Label(composite, SWT.SEPARATOR | SWT.HORIZONTAL);
        spacer.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING));
        spacer.setBackground(background);
        
        IItem[] items = channel.getItems();
        for(int i = 0; i < items.length; ++i)
            createItemGroup(composite, items[i]);
            
        scrollable.setContent(composite);
        scrollable.setMinSize(composite.computeSize(SWT.DEFAULT, SWT.DEFAULT));

        setTitle(channel.getTitle());
        setTitleToolTip(channel.getLink());
        setTitleImage(image16x16);
    }
    
    private void createItemGroup(Composite composite, IItem item) {
        Label title = new Label(composite, SWT.WRAP);
        title.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING | GridData.VERTICAL_ALIGN_BEGINNING));
        title.setFont(titleFont);
        title.setText(item.getTitle());
        
        factory.turnIntoHyperlink(title, new LinkOpener(item));
        
        Label description = new Label(composite, SWT.WRAP);
        GridData gd = new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING);
        gd.horizontalIndent = 20;
        description.setLayoutData(gd);
        description.setFont(composite.getFont());
        description.setBackground(background);
        if(item.getDescription() != null)
            description.setText(item.getDescription());
    }

	/**
	 * @see EditorPart#setFocus
	 */
	public void setFocus() {
        //title.setFocus();
	}

	/**
	 * @see EditorPart#doSave
	 */
	public void doSave(IProgressMonitor monitor) {
        if(monitor != null)
            monitor.beginTask("", 1);
            
        try {
            // TODO Implement me!
            // ...
            setDirty(false);
        }
        finally {
            if(monitor != null)
                monitor.done();
        }
	}

	/**
	 * @see EditorPart#doSaveAs
	 */
	public void doSaveAs() {
	}

	/**
	 * @see EditorPart#isDirty
	 */
	public boolean isDirty() {
		return dirty;
	}

	/**
	 * @see EditorPart#isSaveAsAllowed
	 */
	public boolean isSaveAsAllowed() {
		return false;
	}

	/**
	 * @see EditorPart#gotoMarker
	 */
	public void gotoMarker(IMarker marker) {
	}

	/**
	 * @see EditorPart#init
	 */
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
        if(input instanceof IFileEditorInput) {
            setSite(site);
            setInput(input);
            try {
                channel = RSSCore.getPlugin().getChannel(((IFileEditorInput)input).getFile());
            }
            catch(CoreException e) {
                throw new PartInitException(e.getStatus());
            }
        }
        else
            throw new PartInitException("Input must be a channel file.");

        ResourcesPlugin.getWorkspace().addResourceChangeListener(
            this,
            IResourceChangeEvent.POST_CHANGE);
	}
    
    /* (non-Javadoc)
     * @see org.eclipse.ui.IWorkbenchPart#dispose()
     */
    public void dispose() {
        if(image16x16 != null)
            image16x16.dispose();
            
        if(titleFont != null)
            titleFont.dispose();
            
        if(factory != null)
            factory.dispose();

        if(image != null)
            image.dispose();
            
        if(background != null)
            background.dispose();
            
        super.dispose();
    }
    
    private void setDirty(boolean dirty) {
        boolean changed = this.dirty != dirty;
        this.dirty = dirty;
        if(changed)
            firePropertyChange(PROP_DIRTY);
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResourceChangeListener#resourceChanged(org.eclipse.core.resources.IResourceChangeEvent)
     */
    public void resourceChanged(IResourceChangeEvent event) {
        final IResourceDelta delta = 
            event.getDelta().findMember(channel.getFile().getFullPath());
        if(delta != null) {
            Control ctrl = scrollable;
            if(ctrl != null && !ctrl.isDisposed()) {
                // Do a sync exec, not an async exec, since the resource delta
                // must be traversed in this method.  It is destroyed
                // when this method returns.
                ctrl.getDisplay().syncExec(new Runnable() {
                    public void run() {
                        if(delta.getKind() == IResourceDelta.CHANGED
                            && (delta.getFlags() & IResourceDelta.MARKERS) != 0) {

                            createContent();
                        }
                    }
                });
            }

        }
    }
    
    private class LinkOpener extends HyperlinkAdapter {
        
        private IRSSElement element;
        private IWorkbench wb = RSSUI.getDefault().getWorkbench();
        
        public LinkOpener(IRSSElement element) {
            this.element = element;
        }

        public void linkActivated(Control linkLabel) {
            try {
                ILinkBrowser linkBrowser = RSSUI.getDefault().getLinkBrowser(element);
                if(linkBrowser != null) {
                    IWorkbenchWindow window = wb.getActiveWorkbenchWindow();
                    if(window == null) {
                        IWorkbenchWindow[] windows = wb.getWorkbenchWindows();
                        if(windows != null && windows.length > 0)
                            window = windows[0];
                    }
                    
                    if(window != null) {
                        linkBrowser.open(element, window.getActivePage());
                    }
                }
            }
            catch(CoreException ex) {
                ErrorDialog.openError(
                    linkLabel.getShell(),
                    "Browser Error",
                    "Could not open browser.",
                    ex.getStatus());
            }
        }
    }
}
