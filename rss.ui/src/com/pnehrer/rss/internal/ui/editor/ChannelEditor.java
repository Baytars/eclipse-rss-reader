/*
 * Created on Mar 17, 2004 Version $Id: ChannelEditor.java,v 1.2 2004/07/07
 * 04:14:11 pnehrer Exp $
 */
package com.pnehrer.rss.internal.ui.editor;

import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Hyperlink;
import org.eclipse.ui.forms.widgets.ImageHyperlink;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.part.EditorPart;

import com.pnehrer.rss.core.IChannel;
import com.pnehrer.rss.core.IItem;
import com.pnehrer.rss.core.IRSSElement;
import com.pnehrer.rss.core.RSSCore;
import com.pnehrer.rss.ui.ILinkBrowser;
import com.pnehrer.rss.ui.RSSUI;

public class ChannelEditor extends EditorPart implements
		IResourceChangeListener {

	private IChannel channel;

	private FormToolkit toolkit;

	private ScrolledForm form;

	private Image image;

	private Image image16x16;

	private ImageHyperlink title;

	private Label description;

	private ArrayList itemGroups;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.ISaveablePart#doSave(org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void doSave(IProgressMonitor monitor) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.ISaveablePart#doSaveAs()
	 */
	public void doSaveAs() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.ISaveablePart#isSaveAsAllowed()
	 */
	public boolean isSaveAsAllowed() {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IWorkbenchPart#setFocus()
	 */
	public void setFocus() {
		if (form != null && !form.isDisposed())
			form.setFocus();
	}

	/**
	 * @see EditorPart#init
	 */
	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
		if (input instanceof IFileEditorInput) {
			setSite(site);
			setInput(input);
			try {
				channel = RSSCore.getPlugin().getChannel(
						((IFileEditorInput) input).getFile());
			} catch (CoreException e) {
				throw new PartInitException(e.getStatus());
			}
		} else
			throw new PartInitException("Input must be a channel file.");

		ResourcesPlugin.getWorkspace().addResourceChangeListener(this,
				IResourceChangeEvent.POST_CHANGE);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createPartControl(Composite parent) {
		ImageDescriptor descriptor = RSSUI.getDefault().getImageDescriptor(
				channel);
		if (descriptor != null)
			image = descriptor.createImage();

		image16x16 = RSSUI.getDefault().getImageDescriptor16x16(channel)
				.createImage();

		setPartName(channel.getTitle());
		setTitleToolTip(channel.getLink());
		setTitleImage(image16x16);

		toolkit = new FormToolkit(parent.getDisplay());
		form = toolkit.createScrolledForm(parent);
		form.getBody().setLayout(new GridLayout());

		title = toolkit.createImageHyperlink(form.getBody(), SWT.WRAP);
		title.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		title.setFont(form.getFont());
		title.setImage(image);
		title.setUnderlined(false);
		title.addHyperlinkListener(new LinkOpener(channel));

		description = toolkit.createLabel(form.getBody(), channel
				.getDescription());
		description.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		itemGroups = new ArrayList(channel.getItems().length);
		createContent();
	}

	private void createContent() {
		title.setText(channel.getTitle());
		description.setText(channel.getDescription());

		for (Iterator i = itemGroups.iterator(); i.hasNext();) {
			Composite item = (Composite) i.next();
			item.dispose();
			i.remove();
		}

		IItem[] items = channel.getItems();
		for (int i = 0; i < items.length; ++i)
			itemGroups.add(createItemGroup(items[i]));
	}

	private Composite createItemGroup(IItem item) {
		Composite group = toolkit.createComposite(form.getBody());
		group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		group.setLayout(new GridLayout());

		Composite separator = toolkit.createCompositeSeparator(group);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.heightHint = 2;
		separator.setLayoutData(gd);

		ExpandableComposite section = toolkit.createExpandableComposite(group,
				ExpandableComposite.TWISTIE);
		section.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		section.setText(item.getTitle());
		Hyperlink link = toolkit.createHyperlink(section, "Browse", SWT.NONE);
		link.addHyperlinkListener(new LinkOpener(item));
		section.setTextClient(link);
		Label description = toolkit.createLabel(section, item.getDescription(),
				SWT.WRAP);
		section.setClient(description);
		section.addExpansionListener(new ExpansionAdapter() {
			public void expansionStateChanged(ExpansionEvent e) {
				form.reflow(true);
			}
		});

		return group;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.EditorPart#isDirty()
	 */
	public boolean isDirty() {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IWorkbenchPart#dispose()
	 */
	public void dispose() {
		if (image16x16 != null)
			image16x16.dispose();

		if (image != null)
			image.dispose();

		if (form != null)
			form.dispose();

		if (toolkit != null)
			toolkit.dispose();

		super.dispose();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.resources.IResourceChangeListener#resourceChanged(org.eclipse.core.resources.IResourceChangeEvent)
	 */
	public void resourceChanged(IResourceChangeEvent event) {
		final IResourceDelta delta = event.getDelta().findMember(
				channel.getFile().getFullPath());
		if (delta != null) {
			Control ctrl = form;
			if (ctrl != null && !ctrl.isDisposed()) {
				// Do a sync exec, not an async exec, since the resource
				// delta
				// must be traversed in this method. It is destroyed
				// when this method returns.
				ctrl.getDisplay().syncExec(new Runnable() {
					public void run() {
						if (delta.getKind() == IResourceDelta.CHANGED
								&& (delta.getFlags() & IResourceDelta.MARKERS) != 0) {

							createContent();
							form.reflow(true);
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

		public void linkActivated(HyperlinkEvent event) {
			try {
				ILinkBrowser linkBrowser = RSSUI.getDefault().getLinkBrowser(
						element);
				if (linkBrowser != null) {
					IWorkbenchWindow window = wb.getActiveWorkbenchWindow();
					if (window == null) {
						IWorkbenchWindow[] windows = wb.getWorkbenchWindows();
						if (windows != null && windows.length > 0)
							window = windows[0];
					}

					if (window != null) {
						linkBrowser.open(element, window.getActivePage());
						if(element instanceof IItem)
							((IItem)element).resetUpdateFlag();
					}
				}
			} catch (CoreException ex) {
				ErrorDialog.openError(((Control) event.getSource()).getShell(),
						"Browser Error", "Could not open browser.", ex
								.getStatus());
			}
		}
	}
}