/*
 * Created on Jan 19, 2004
 *
 * Copyright (C) 2004  Sean Ruff
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package org.un4given.editorviewer.action;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.actions.ActionGroup;
import org.un4given.EditorViewerPlugin;
import org.un4given.Messages;
import org.un4given.editorviewer.views.EditorViewer;
import org.un4given.editorviewer.views.EditorViewerContentProvider;

/**
 * Adds view menus to switch between flat,semi-flat,structured, and type layout.
 * <p>
 * @author Sean Ruff
 */

public class LayoutActionGroup extends MultiActionGroup {
	
	/**
	 * Constructor
	 * @param viewer
	 * @param layoutMode
	 */
	public LayoutActionGroup(ActionGroup group, EditorViewer viewer){//, int layoutMode) {
		super(createActions(viewer), EditorViewerContentProvider.HIER_LAYOUT);
	}

	/**
	 * Fill the action bars with the submenu and actions
	 */
	public void fillActionBars(IActionBars actionBars) {
		super.fillActionBars(actionBars);
		contributeToViewMenu(actionBars.getMenuManager());
	}

	/**
	 * Contribute the submenu and actions to the MenuManager
	 * @param viewMenu MenuManager to modify
	 */
	private void contributeToViewMenu(IMenuManager viewMenu) {
		// Create layout sub menu	
		IMenuManager layoutSubMenu= new MenuManager(Messages.getString("LayoutActionGroup.0")); //$NON-NLS-1$
		final String layoutGroupName= Messages.getString("LayoutActionGroup.1");  //$NON-NLS-1$
		GroupMarker marker = new GroupMarker(layoutGroupName);
		
		viewMenu.add(marker);
		viewMenu.appendToGroup(layoutGroupName, layoutSubMenu);
		addActions(layoutSubMenu);
	}
	
	/**
	 * Create a list of actions to add to the new menu
	 * @param viewer
	 * @return IAction[]
	 */
	private static IAction[] createActions(EditorViewer viewer) {

		IAction hierLayoutAction = new LayoutAction(viewer, EditorViewerContentProvider.HIER_LAYOUT);
		hierLayoutAction.setText(Messages.getString("LayoutActionGroup.3")); //$NON-NLS-1$
		hierLayoutAction.setToolTipText(Messages.getString("LayoutActionGroup.4")); //$NON-NLS-1$
		hierLayoutAction.setDescription(Messages.getString("LayoutActionGroup.5")); //$NON-NLS-1$
		hierLayoutAction.setImageDescriptor(EditorViewerPlugin.getImageDescriptor(Messages.getString("LayoutActionGroup.6"))); //$NON-NLS-1$

		IAction flatLayoutAction = new LayoutAction(viewer, EditorViewerContentProvider.FLAT_LAYOUT);
		flatLayoutAction.setText(Messages.getString("LayoutActionGroup.7")); //$NON-NLS-1$
		flatLayoutAction.setToolTipText(Messages.getString("LayoutActionGroup.8")); //$NON-NLS-1$
		flatLayoutAction.setDescription(Messages.getString("LayoutActionGroup.9")); //$NON-NLS-1$
		flatLayoutAction.setImageDescriptor(EditorViewerPlugin.getImageDescriptor(Messages.getString("LayoutActionGroup.10"))); //$NON-NLS-1$
	
		IAction semiLayoutAction = new LayoutAction(viewer, EditorViewerContentProvider.SEMI_LAYOUT);
		semiLayoutAction.setText(Messages.getString("LayoutActionGroup.11")); //$NON-NLS-1$
		semiLayoutAction.setToolTipText(Messages.getString("LayoutActionGroup.12")); //$NON-NLS-1$
		semiLayoutAction.setDescription(Messages.getString("LayoutActionGroup.13")); //$NON-NLS-1$
		semiLayoutAction.setImageDescriptor(EditorViewerPlugin.getImageDescriptor(Messages.getString("LayoutActionGroup.14"))); //$NON-NLS-1$
		
		//NOTE: the value of the LayoutMode parameter (second param) suppliedto each LayoutAction instance
		//must match the position within the following returned array
		// Example since EditorViewerPlugin.HIERARCHICAL_MODE == 0 then it must be the first element in the array
		return new IAction[]{ hierLayoutAction, flatLayoutAction, semiLayoutAction };
	}


	public int getLayoutMode(){
		return this.getSelection();
	}

}

	/**
	 * Action class which will run the appropriate action
	 * depending on it's supplied layoutMode flag
	 * @author nbk4bsx 
	 * @since   Jan 12, 2004 
	 *
	 */
	class LayoutAction extends Action implements IAction {
	
		/**
		 * Mode for this action
		 */
		private int _layoutMode;

		/**
		 * EditorViewer for callbacks
		 */
		private EditorViewer _viewer;
		
		public int getLayoutMode(){
			return _layoutMode;
		}
		
		/**
		 * Create a new LayoutAction with the specified layoutMode
		 * @param viewer
		 * @param layoutMode
		 */
		public LayoutAction(EditorViewer viewer, int layout) {
			_layoutMode = layout;
			_viewer = viewer;
		}
	
		/**
		 * @see org.eclipse.jface.action.IAction#run()
		 */
		public void run() {
			_viewer.setLayout(this._layoutMode);
		}
}
