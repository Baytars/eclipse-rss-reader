/*
 * Created on Jan 15, 2004
 * Version $Id$
 */
package com.pnehrer.rss.ui;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

/**
 * @author <a href="mailto:pnehrer@freeshell.org">Peter Nehrer</a>
 * @see IPerspectiveFactory
 */
public class RSSPerspective implements IPerspectiveFactory {

	/**
	 * @see IPerspectiveFactory#createInitialLayout
	 */
    public void createInitialLayout(IPageLayout layout) {
        // Add "new wizards".
        layout.addNewWizardShortcut("com.pnehrer.rss.ui.wizards.NewChannelWizard");
        layout.addNewWizardShortcut("com.pnehrer.rss.ui.wizards.NewChannelAutoWizard");
        layout.addNewWizardShortcut("org.eclipse.ui.wizards.new.folder");

        // Add "show views".
        layout.addShowViewShortcut("com.pnehrer.rss.ui.views.ChannelNavigator");
        layout.addShowViewShortcut("com.pnehrer.rss.ui.views.ChannelDetail");
        layout.addShowViewShortcut(IPageLayout.ID_RES_NAV);
        layout.addShowViewShortcut(IPageLayout.ID_BOOKMARKS);
        layout.addShowViewShortcut(IPageLayout.ID_OUTLINE);
        layout.addShowViewShortcut(IPageLayout.ID_PROP_SHEET);
        layout.addShowViewShortcut(IPageLayout.ID_TASK_LIST);
    
        layout.addActionSet(IPageLayout.ID_NAVIGATE_ACTION_SET);
        layout.addActionSet("com.pnehrer.rss.ui.actionSet1");
        
        layout.addPerspectiveShortcut("org.eclipse.ui.views.ResourceNavigator");
        
        layout.addShowInPart(IPageLayout.ID_RES_NAV);
        layout.addShowInPart("com.pnehrer.rss.ui.views.ChannelNavigator");
        layout.addShowInPart("com.pnehrer.rss.ui.views.ChannelDetail");

        // Everything starts with the editor area...
        String editorArea = layout.getEditorArea();

        // Top left.
        IFolderLayout topLeft = layout.createFolder(
            "topLeft", 
            IPageLayout.LEFT, 
            (float)0.26, 
            editorArea);

        topLeft.addView("com.pnehrer.rss.ui.views.ChannelNavigator");
        topLeft.addView(IPageLayout.ID_RES_NAV);
        topLeft.addPlaceholder(IPageLayout.ID_BOOKMARKS);

        // Bottom left.
        IFolderLayout bottomLeft = layout.createFolder(
            "bottomLeft", 
            IPageLayout.BOTTOM, 
            (float)0.66, 
            "topLeft");
            
        bottomLeft.addView(IPageLayout.ID_PROP_SHEET);
        bottomLeft.addPlaceholder(IPageLayout.ID_OUTLINE);

        // Bottom right.
        IFolderLayout bottomRight = layout.createFolder(
            "bottomRight", 
            IPageLayout.BOTTOM, 
            (float)0.66, 
            editorArea);

        bottomRight.addView("com.pnehrer.rss.ui.views.ChannelDetail");
        bottomRight.addPlaceholder(IPageLayout.ID_TASK_LIST);

        // Hide editors
        layout.setEditorAreaVisible(false);
    }
}
