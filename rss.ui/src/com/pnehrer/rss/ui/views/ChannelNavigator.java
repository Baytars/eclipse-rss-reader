/*
 * Created on Nov 17, 2003
 * Version $Id$
 */
package com.pnehrer.rss.ui.views;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.IJobChangeListener;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.IOpenListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.OpenEvent;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.actions.ActionContext;
import org.eclipse.ui.part.ISetSelectionTarget;
import org.eclipse.ui.part.IShowInSource;
import org.eclipse.ui.part.IShowInTarget;
import org.eclipse.ui.part.ShowInContext;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.progress.IWorkbenchSiteProgressService;

import com.pnehrer.rss.core.IChannel;
import com.pnehrer.rss.core.IItem;
import com.pnehrer.rss.core.IRSSElement;

/**
 * @author <a href="mailto:pnehrer@freeshell.org">Peter Nehrer</a>
 */
public class ChannelNavigator extends ViewPart implements ISetSelectionTarget,
		IShowInSource, IShowInTarget, IResourceChangeListener {

	private static final String TAG_SELECTION = "selection";

	private static final String TAG_EXPANDED = "expanded";

	private static final String TAG_ELEMENT = "element";

	private static final String TAG_PATH = "path";

	private static final String TAG_LINK = "link";

	private static final String TAG_SHOW_NEW_ONLY = "showNewOnly";

	private IMemento memento;

	private TreeViewer viewer;

	private ChannelNavigatorActionGroup actionGroup;

	private boolean showNewOnly;

	private IWorkbenchSiteProgressService workbenchSiteProgressService;

	private IJobChangeListener jobChangeListener;

	private void initContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				ChannelNavigator.this.fillContextMenu(manager);
			}
		});

		Menu menu = menuMgr.createContextMenu(viewer.getControl());
		viewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuMgr, viewer);
	}

	private void fillContextMenu(IMenuManager menu) {
		IStructuredSelection selection = (IStructuredSelection) viewer
				.getSelection();
		actionGroup.setContext(new ActionContext(selection));
		actionGroup.fillContextMenu(menu);
	}

	private void updateActionBars(IStructuredSelection selection) {
		actionGroup.setContext(new ActionContext(selection));
		actionGroup.updateActionBars();
	}

	private void updateStatusLine(IStructuredSelection selection) {
		String msg;
		switch (selection.size()) {
		case 0:
			msg = "No selection.";
			break;

		case 1:
			Object object = selection.getFirstElement();
			if (object instanceof IAdaptable) {
				IRSSElement rssElement = (IRSSElement) ((IAdaptable) object)
						.getAdapter(IRSSElement.class);
				if (rssElement == null) {
					IResource resource = (IResource) ((IAdaptable) object)
							.getAdapter(IResource.class);
					msg = resource == null ? null : resource.getFullPath()
							.toString();
				} else {
					msg = rssElement == null ? null : rssElement.getLink();
				}
			} else
				msg = null;

			break;

		default:
			msg = "Multiple selection.";
		}

		getViewSite().getActionBars().getStatusLineManager().setMessage(msg);
	}

	private void handleSelectionChanged(SelectionChangedEvent event) {
		IStructuredSelection sel = (IStructuredSelection) event.getSelection();
		updateStatusLine(sel);
		updateActionBars(sel);
	}

	private void handleOpen(OpenEvent event) {
		IStructuredSelection selection = (IStructuredSelection) event
				.getSelection();
		actionGroup.runDefaultAction(selection);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createPartControl(Composite parent) {
		Tree tree = new Tree(parent, SWT.MULTI);
		tree.setLayoutData(new GridData(GridData.FILL_BOTH));

		viewer = new TreeViewer(tree);
		viewer.setUseHashlookup(true);

		viewer.addOpenListener(new IOpenListener() {
			public void open(OpenEvent event) {
				handleOpen(event);
			}
		});

		viewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				handleSelectionChanged(event);
			}
		});

		viewer.setContentProvider(new ChannelNavigatorContentProvider());
		viewer.setLabelProvider(new ChannelNavigatorLabelProvider());

		viewer.addFilter(new ViewerFilter() {
			public boolean select(Viewer viewer, Object parentElement,
					Object element) {

				if (element instanceof IItem)
					return (((IItem) element).isUpdated() || !isShowNewOnly());
				else if (element instanceof IChannel)
					return (((IChannel) element).hasUpdates() || !isShowNewOnly());
				else
					return true;
			}
		});

		viewer.setInput(ResourcesPlugin.getWorkspace().getRoot());

		initContextMenu();
		actionGroup = new ChannelNavigatorActionGroup(this);
		actionGroup.fillActionBars(getViewSite().getActionBars());
		updateActionBars((IStructuredSelection) viewer.getSelection());

		getSite().setSelectionProvider(viewer);

		if (memento != null) {
			restoreState(memento);
			memento = null;
		}

		workbenchSiteProgressService = (IWorkbenchSiteProgressService) getViewSite()
				.getAdapter(IWorkbenchSiteProgressService.class);
		if (workbenchSiteProgressService != null)
			ResourcesPlugin.getWorkspace().addResourceChangeListener(this);

		jobChangeListener = new JobChangeAdapter() {

			private String partName = getPartName();

			public void running(IJobChangeEvent event) {
				if (!event.getJob().belongsTo(IChannel.UPDATE_JOB_FAMILY))
					return;

				Display display = getViewSite().getShell().getDisplay();
				if (display == null || display.isDisposed())
					return;

				display.asyncExec(new Runnable() {
					public void run() {
						setPartName(partName + " [updating]");
					}
				});
			}

			public void done(IJobChangeEvent event) {
				if (!event.getJob().belongsTo(IChannel.UPDATE_JOB_FAMILY))
					return;

				Display display = getViewSite().getShell().getDisplay();
				if (display == null || display.isDisposed())
					return;

				display.asyncExec(new Runnable() {
					public void run() {
						setPartName(partName);
					}
				});
			}
		};

		Platform.getJobManager().addJobChangeListener(jobChangeListener);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
	 */
	public void setFocus() {
		viewer.getTree().setFocus();
	}

	public TreeViewer getViewer() {
		return viewer;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.ISetSelectionTarget#selectReveal(org.eclipse.jface.viewers.ISelection)
	 */
	public void selectReveal(ISelection selection) {
		if (selection instanceof IStructuredSelection) {
			List list = new ArrayList();
			for (Iterator i = ((IStructuredSelection) selection).iterator(); i
					.hasNext();) {

				Object o = i.next();
				IRSSElement rssElement = (IRSSElement) ((IAdaptable) o)
						.getAdapter(IRSSElement.class);
				if (rssElement != null) {
					list.add(rssElement);
				} else {
					IResource res = (IResource) ((IAdaptable) o)
							.getAdapter(IResource.class);
					if (res != null && res.getType() != IResource.ROOT)
						list.add(res);
				}
			}

			viewer.setSelection(new StructuredSelection(list), true);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.IShowInSource#getShowInContext()
	 */
	public ShowInContext getShowInContext() {
		return new ShowInContext(viewer.getInput(), viewer.getSelection());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.IShowInTarget#show(org.eclipse.ui.part.ShowInContext)
	 */
	public boolean show(ShowInContext context) {
		ArrayList toSelect = new ArrayList();
		ISelection sel = context.getSelection();
		if (sel instanceof IStructuredSelection) {
			IStructuredSelection ssel = (IStructuredSelection) sel;
			for (Iterator i = ssel.iterator(); i.hasNext();) {
				Object o = i.next();
				if (o instanceof IAdaptable) {
					IRSSElement rssElement = (IRSSElement) ((IAdaptable) o)
							.getAdapter(IRSSElement.class);
					if (rssElement != null) {
						toSelect.add(rssElement);
					} else {
						IResource res = (IResource) ((IAdaptable) o)
								.getAdapter(IResource.class);
						if (res != null && res.getType() != IResource.ROOT)
							toSelect.add(res);
					}
				}
			}
		}

		if (toSelect.isEmpty()) {
			Object input = context.getInput();
			if (input instanceof IAdaptable) {
				IRSSElement rssElement = (IRSSElement) ((IAdaptable) input)
						.getAdapter(IRSSElement.class);
				if (rssElement != null) {
					toSelect.add(rssElement);
				} else {
					IResource res = (IResource) ((IAdaptable) input)
							.getAdapter(IResource.class);
					if (res != null && res.getType() != IResource.ROOT)
						toSelect.add(res);
				}
			}
		}

		if (toSelect.isEmpty())
			return false;
		else {
			selectReveal(new StructuredSelection(toSelect));
			return true;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IViewPart#init(org.eclipse.ui.IViewSite,
	 *      org.eclipse.ui.IMemento)
	 */
	public void init(IViewSite site, IMemento memento) throws PartInitException {

		super.init(site, memento);
		this.memento = memento;
	}

	private void restoreState(IMemento memento) {
		setShowNewOnly(Boolean.TRUE.equals(new Boolean(memento
				.getString(TAG_SHOW_NEW_ONLY))));
		actionGroup.setShowNewOnly(showNewOnly);

		IContainer container = ResourcesPlugin.getWorkspace().getRoot();
		IMemento childMem = memento.getChild(TAG_EXPANDED);
		if (childMem != null) {
			ArrayList elements = new ArrayList();
			IMemento[] elementMem = childMem.getChildren(TAG_ELEMENT);
			for (int i = 0; i < elementMem.length; ++i) {
				IResource res = container.findMember(elementMem[i]
						.getString(TAG_PATH));
				if (res != null) {
					IRSSElement rssElement = (IRSSElement) res
							.getAdapter(IRSSElement.class);
					if (rssElement == null)
						elements.add(res);
					else
						elements.add(rssElement);
				}
			}

			viewer.setExpandedElements(elements.toArray());
		}

		childMem = memento.getChild(TAG_SELECTION);
		if (childMem != null) {
			ArrayList elements = new ArrayList();
			IMemento[] elementMem = childMem.getChildren(TAG_ELEMENT);
			for (int i = 0; i < elementMem.length; ++i) {
				IResource res = container.findMember(elementMem[i]
						.getString(TAG_PATH));
				if (res != null) {
					IRSSElement rssElement = (IRSSElement) res
							.getAdapter(IRSSElement.class);
					if (rssElement == null)
						elements.add(res);
					else {
						String link = elementMem[i].getString(TAG_LINK);
						if (link == null)
							elements.add(rssElement);
						else {
							IItem[] items = rssElement.getChannel().getItems();
							boolean found = false;
							for (int j = 0; j < items.length; ++j) {
								if (link.equals(items[j].getLink())) {
									elements.add(items[j]);
									found = true;
									break;
								}
							}

							if (!found)
								elements.add(rssElement);
						}
					}
				}
			}

			viewer.setSelection(new StructuredSelection(elements));
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IViewPart#saveState(org.eclipse.ui.IMemento)
	 */
	public void saveState(IMemento memento) {
		if (viewer == null) {
			if (this.memento != null)
				memento.putMemento(this.memento);
		} else {
			memento.putString(TAG_SHOW_NEW_ONLY, String.valueOf(showNewOnly));

			Object expandedElements[] = viewer.getVisibleExpandedElements();
			if (expandedElements.length > 0) {
				IMemento expandedMem = memento.createChild(TAG_EXPANDED);
				for (int i = 0; i < expandedElements.length; ++i) {
					IAdaptable adaptable = (IAdaptable) expandedElements[i];
					IMemento elementMem = expandedMem.createChild(TAG_ELEMENT);
					IResource res = (IResource) ((IAdaptable) adaptable)
							.getAdapter(IResource.class);
					if (res != null && res.getType() != IResource.ROOT) {
						elementMem.putString(TAG_PATH, res.getFullPath()
								.toString());
					}
				}
			}

			IStructuredSelection sel = (IStructuredSelection) viewer
					.getSelection();
			if (!sel.isEmpty()) {
				IMemento selectionMem = memento.createChild(TAG_SELECTION);
				for (Iterator i = sel.iterator(); i.hasNext();) {
					IAdaptable adaptable = (IAdaptable) i.next();
					IMemento elementMem = selectionMem.createChild(TAG_ELEMENT);
					IResource res = (IResource) ((IAdaptable) adaptable)
							.getAdapter(IResource.class);
					if (res != null && res.getType() != IResource.ROOT) {
						elementMem.putString(TAG_PATH, res.getFullPath()
								.toString());
					}

					IRSSElement rssElement = (IRSSElement) ((IAdaptable) adaptable)
							.getAdapter(IRSSElement.class);
					if (rssElement instanceof IItem) {
						elementMem.putString(TAG_LINK, rssElement.getLink());
					}
				}
			}
		}
	}

	void setShowNewOnly(boolean showNewOnly) {
		boolean oldValue = this.showNewOnly;
		this.showNewOnly = showNewOnly;
		if (oldValue != showNewOnly)
			viewer.refresh();
	}

	private boolean isShowNewOnly() {
		return showNewOnly;
	}

	public void resourceChanged(IResourceChangeEvent event) {
		Control ctrl = viewer.getControl();
		if (ctrl == null || ctrl.isDisposed())
			return;

		final boolean[] notify = new boolean[1];
		try {
			event.getDelta().accept(new IResourceDeltaVisitor() {
				public boolean visit(IResourceDelta delta) throws CoreException {
					if (notify[0])
						return false;

					if (delta.getResource().getType() == IResource.FILE
							&& "rss".equals(delta.getResource()
									.getFileExtension())) {

						notify[0] = true;
					}

					return true;
				}
			});
		} catch (CoreException e) {
			// ignore
		}

		ctrl.getDisplay().asyncExec(new Runnable() {
			/*
			 * (non-Javadoc)
			 * 
			 * @see java.lang.Runnable#run()
			 */
			public void run() {
				// Abort if this happens after disposes
				Control ctrl = viewer.getControl();
				if (ctrl == null || ctrl.isDisposed())
					return;

				if (notify[0])
					workbenchSiteProgressService.warnOfContentChange();
			}
		});
	}

	public void dispose() {
		ResourcesPlugin.getWorkspace().removeResourceChangeListener(this);
		if (jobChangeListener != null)
			Platform.getJobManager().removeJobChangeListener(jobChangeListener);

		super.dispose();
	}
}
